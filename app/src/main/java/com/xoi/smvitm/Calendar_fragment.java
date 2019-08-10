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
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Calendar_fragment extends Fragment implements IRefreshStatus {

    View view;
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> day = new ArrayList<>();
    private ArrayList<String> type = new ArrayList<>();
    private ArrayList<String> month = new ArrayList<>();
    SharedPreferences sharedPreferences;
    ProgressDialog loader;
    RecyclerRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    BubbleNavigationConstraintView bubbleToggleView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar,container,false);
        refreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.main_swipe);
        loader = new ProgressDialog(getActivity());
        bubbleToggleView = (BubbleNavigationConstraintView) view.findViewById(R.id.bubble_nav_view);
        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("Calendar description")) {
            try {
                name = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Calendar names", ObjectSerializer.serialize(new ArrayList<String>())));
                date = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Calendar dates", ObjectSerializer.serialize(new ArrayList<String>())));
                day = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Calendar days", ObjectSerializer.serialize(new ArrayList<String>())));
                type = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Calendar types", ObjectSerializer.serialize(new ArrayList<String>())));
                month = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Calendar month", ObjectSerializer.serialize(new ArrayList<String>())));
                initRecyclerView(0);
            } catch (Exception e) {
                refresh();
            }
        }
        else{
            refresh();
        }

        bubbleToggleView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                switch (position) {
                    case 0:
                        initRecyclerView(0);
                        break;
                    case 1:
                        initRecyclerView(1);
                        break;
                    case 2:
                        initRecyclerView(2);
                        break;
                    case 3:
                        initRecyclerView(3);
                        break;
                    case 4:
                        initRecyclerView(4);
                        break;
                    default:
                        initRecyclerView(0);
                }
            }
        });
        refreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }

    private void refresh(){
        name.removeAll(name);
        date.removeAll(date);
        day.removeAll(day);
        type.removeAll(type);
        month.removeAll(month);
        getCalendar();
        refreshing();
    }


    private void getCalendar() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbwXKu5SqHcngKxUjUp-g4-xG81Nm3suTrzQKehVboJu4RgMM_0/exec?action=getCalendar",
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
            JSONArray jarray = jobj.getJSONArray("calendar");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String date_json = jo.getString("date");
                date.add(date_json);
                String day_json = jo.getString("day");
                day.add(day_json);
                String type_json = jo.getString("type");
                type.add(type_json);
                String name_json = jo.getString("name");
                name.add(name_json);
                String month_json = jo.getString("month");
                month.add(month_json);
            }try {
                sharedPreferences.edit().putString("Calendar names", ObjectSerializer.serialize(name)).apply();
                sharedPreferences.edit().putString("Calendar dates", ObjectSerializer.serialize(date)).apply();
                sharedPreferences.edit().putString("Calendar days", ObjectSerializer.serialize(day)).apply();
                sharedPreferences.edit().putString("Calendar types", ObjectSerializer.serialize(type)).apply();
                sharedPreferences.edit().putString("Calendar month", ObjectSerializer.serialize(month)).apply();
                sharedPreferences.edit().putString("Calendar description", "1").apply();
            }
            catch (Exception e) {
            }
            initRecyclerView(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(int month_int){
        ArrayList<String> name_temp = new ArrayList<>();
        ArrayList<String> date_temp = new ArrayList<>();
        ArrayList<String> day_temp = new ArrayList<>();
        ArrayList<String> type_temp = new ArrayList<>();
        String[] montharr = {"August","September","October","November","December"};
        for(int i = 0; i < name.size(); i++){
            if(month.get(i).equals(montharr[month_int])){
                day_temp.add(day.get(i));
                date_temp.add(date.get(i));
                type_temp.add(type.get(i));
                name_temp.add(name.get(i));
            }
        }
        recyclerView = view.findViewById(R.id.recyclerview);
        Calendar_RV_Adapter adapter = new Calendar_RV_Adapter(name_temp,date_temp,day_temp,type_temp,getActivity());
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
        loader.setTitle("Updating calendar");
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
