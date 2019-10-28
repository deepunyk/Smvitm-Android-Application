package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.dinuscxj.refresh.IRefreshStatus;
import com.dinuscxj.refresh.RecyclerRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Feed_fragment extends Fragment implements IRefreshStatus {

    View view;
    private ArrayList<String> desc = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> usn = new ArrayList<>();

    RecyclerRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    ProgressDialog loader;

    SharedPreferences sharedPreferences;

    EditText user_post_txt;
    ActionProcessButton user_post_but;
    String usr_txt, usr_name, usr_usn, usr_date, profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feed, container, false);

        loader = new ProgressDialog(getActivity());
        refreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.main_swipe);

        user_post_but = (ActionProcessButton)view.findViewById(R.id.usr_post_but);
        user_post_txt = (EditText) view.findViewById(R.id.usr_post_txt);

        user_post_but.setMode(ActionProcessButton.Mode.ENDLESS);

        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);

        profile = sharedPreferences.getString("login_Activity", "");

        if (profile.equals("1")) {
            usr_name = sharedPreferences.getString("Student name", "");
            usr_usn = sharedPreferences.getString("Student usn", "");
        } else {
            usr_name = sharedPreferences.getString("Faculty name", "");
            usr_usn = sharedPreferences.getString("Faculty branch", "");
        }

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM");
        usr_date = df.format(c);

        user_post_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usr_txt = user_post_txt.getText().toString();
                user_post_but.setProgress(1);
                user_post_but.setText("Posting");
                post();
            }
        });

        refreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        refresh();
        return view;
    }

    public void refresh(){
        desc.removeAll(desc);
        usn.removeAll(usn);
        date.removeAll(date);
        name.removeAll(name);
        getFeeds();
        refreshing();
    }

    private void post(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyKEtZhACQek_7Lc6ht7OML1H6eLRsmLPVxndAKxVU9Azz7o20V/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Success")) {
                            user_post_but.setProgress(100);
                            Toast.makeText(getActivity(), "Post successful", Toast.LENGTH_SHORT).show();
                            refresh();
                            user_post_txt.setText("");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                parmas.put("action", "addPost");
                parmas.put("desc", usr_txt);
                parmas.put("name", usr_name);
                parmas.put("usn", usr_usn);
                parmas.put("date", usr_date);
                return parmas;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }

    private void getFeeds() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbyKEtZhACQek_7Lc6ht7OML1H6eLRsmLPVxndAKxVU9Azz7o20V/exec?action=getFeeds",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }

    private void parseItems(String jsonResposnce) {
        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("feeds");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String desc_json = jo.getString("text");
                desc.add(desc_json);
                String date_json = jo.getString("date");
                date.add(date_json);
                String usn_json = jo.getString("usn");
                usn.add(usn_json);
                String name_json = jo.getString("name");
                name.add(name_json);
            }
            initRecyclerView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(){
        recyclerView = view.findViewById(R.id.recyclerview);
        feed_rv_adapter adapter = new feed_rv_adapter(name,date,desc,usn,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setRefreshing(false);
        refreshComplete();
    }

    @Override
    public void reset() {

    }

    @Override
    public void refreshing() {
        loader.setTitle("Updating Feed");
        loader.setMessage("Please wait...");
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.show();
    }

    @Override
    public void refreshComplete() {
        loader.dismiss();
    }

    @Override
    public void pullToRefresh() {

    }

    @Override
    public void releaseToRefresh() {

    }

    @Override
    public void pullProgress(float pullDistance, float pullProgress) {

    }
}
