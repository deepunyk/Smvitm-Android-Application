package com.xoi.smvitm;

import android.app.DownloadManager;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Circular_fragment extends Fragment implements IRefreshStatus {

    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> links = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> total = new ArrayList<>();
    View view;
    SharedPreferences sharedPreferences;
    ProgressDialog loader;
    Button refresh;
    RecyclerRefreshLayout refreshLayout;
    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_circular,container,false);
        refreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.main_swipe);
        loader = new ProgressDialog(getActivity());

        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("Circular description")) {
            try {
                descriptions = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Circular description", ObjectSerializer.serialize(new ArrayList<String>())));
                links = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Circular link", ObjectSerializer.serialize(new ArrayList<String>())));
                dates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Circular dates", ObjectSerializer.serialize(new ArrayList<String>())));
                initRecyclerView();
                checkCircular();
            } catch (Exception e) {
                refresh();
            }
        }
        else{
            refresh();
        }
        refreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        return view;
    }

    private void refresh(){
        descriptions.removeAll(descriptions);
        links.removeAll(links);
        dates.removeAll(dates);
        getCirculars();
        refreshing();
    }
    private void getCirculars() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxIpWEw0QJ5FEc_106HZqOS6DD2QKpRdoZ1nLmUxDyda-v8M-g/exec?action=getCirculars",
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
            JSONArray jarray = jobj.getJSONArray("circulars");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String description_json = jo.getString("description");
                descriptions.add(description_json);
                String links_json = jo.getString("link");
                links.add(links_json);
                String dates_json = jo.getString("date");
                dates.add(dates_json);
            }try {
                sharedPreferences.edit().putString("Circular description", ObjectSerializer.serialize(descriptions)).apply();
                sharedPreferences.edit().putString("Circular link", ObjectSerializer.serialize(links)).apply();
                sharedPreferences.edit().putString("Circular dates", ObjectSerializer.serialize(dates)).apply();
            }
            catch (Exception e) {
            }
            initRecyclerView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(){
        recyclerView = view.findViewById(R.id.recyclerview);
        Circular_Recycler_View_Adapter adapter = new Circular_Recycler_View_Adapter(descriptions,links,dates,getActivity());
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
        loader.setTitle("Updating circulars");
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

    private void checkCircular() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxIpWEw0QJ5FEc_106HZqOS6DD2QKpRdoZ1nLmUxDyda-v8M-g/exec?action=getCirculars",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems1(response);
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

    private void parseItems1(String jsonResposnce) {
        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("circulars");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String description_json = jo.getString("description");
                total.add(description_json);
            }
            int totalItems, prevTotalItems;
            totalItems = total.toArray().length;
            prevTotalItems = descriptions.toArray().length;
            if(totalItems != prevTotalItems){
                Toast.makeText(getActivity(), "You have news Circulars. Swipe down to refresh", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
