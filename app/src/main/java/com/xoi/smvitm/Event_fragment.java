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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dinuscxj.refresh.IRefreshStatus;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.github.ybq.android.spinkit.SpinKitView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Event_fragment extends Fragment implements IRefreshStatus {
    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> imgLinks = new ArrayList<>();
    private ArrayList<String> conducts = new ArrayList<>();
    private ArrayList<String> details = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> brochures = new ArrayList<>();

    View view;
    SharedPreferences sharedPreferences;
    ProgressDialog loader;
    Button refresh;
    RecyclerRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    String url = "https://script.google.com/macros/s/AKfycbxmtou-2wG8wZA8JZlaCgcW_Ky4baECBIpQYX39PsOx30SWOyc/exec?action=";
    String wUrl = url + "getWorkshop";
    String pUrl = url + "getProgram";
    String cUrl = url + "getCompetition";
    BubbleNavigationConstraintView bubbleToggleView;
    SpinKitView load_an;
    TextView load_txt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_event, container, false);
        refreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.main_swipe);
        loader = new ProgressDialog(getActivity());
        bubbleToggleView = (BubbleNavigationConstraintView) view.findViewById(R.id.bubble_nav_view);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        url = wUrl;
        load_an = (SpinKitView)view.findViewById(R.id.spin_kit);
        load_txt = (TextView)view.findViewById(R.id.load_txt);
        refresh();

        refreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        bubbleToggleView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position) {

                    case 0:
                        url = wUrl;
                        refresh();
                        break;
                    case 1:
                        url = cUrl;
                        refresh();
                        break;
                    case 2:
                        url = pUrl;
                        refresh();
                        break;
                    default:
                        url = wUrl;
                }
            }
        });


        return view;
    }

    private void refresh() {
        descriptions.removeAll(descriptions);
        imgLinks.removeAll(imgLinks);
        titles.removeAll(titles);
        brochures.removeAll(brochures);
        dates.removeAll(dates);
        conducts.removeAll(conducts);
        details.removeAll(details);
        getCirculars();
        refreshing();
    }

    private void getCirculars() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
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
            JSONArray jarray = jobj.getJSONArray("events");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String description_json = jo.getString("description");
                descriptions.add(description_json);
                String links_json = jo.getString("imagelink");
                imgLinks.add(links_json);
                String title_json = jo.getString("title");
                titles.add(title_json);
                String conduct_json = jo.getString("conducted");
                conducts.add(conduct_json);
                String detail_json = jo.getString("detaildescription");
                details.add(detail_json);
                String date_json = jo.getString("date");
                dates.add(date_json);
                String brochure_json = jo.getString("brochure");
                brochures.add(brochure_json);
            }
            initRecyclerView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerview);
        event_recycler_view_adapter adapter = new event_recycler_view_adapter(titles, descriptions, imgLinks,conducts,details,dates, brochures,getActivity());
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
        loader.setTitle("Updating events");
        loader.setMessage("Please wait...");
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        //loader.show();
        recyclerView.setVisibility(View.GONE);
        load_an.setVisibility(View.VISIBLE);
        load_txt.setVisibility(View.VISIBLE);
    }

    @Override
    public void refreshComplete() {
        //loader.dismiss();
        recyclerView.setVisibility(View.VISIBLE);
        load_an.setVisibility(View.GONE);
        load_txt.setVisibility(View.GONE);
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
