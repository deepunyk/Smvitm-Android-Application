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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Home3_fragment extends Fragment {

    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> links = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    View view;
    ProgressDialog loading;
    SharedPreferences sharedPreferences;
    Button refresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_3,container,false);
        refresh = (Button)view.findViewById(R.id.btnRefresh);

        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        try {
            descriptions = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Circular description", ObjectSerializer.serialize(new ArrayList<String>())));
            links = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Circular link", ObjectSerializer.serialize(new ArrayList<String>())));
            dates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Circular dates", ObjectSerializer.serialize(new ArrayList<String>())));
            initRecyclerView();
        }
        catch (Exception e){
            refresh.performClick();
        }
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                descriptions.removeAll(descriptions);
                links.removeAll(links);
                dates.removeAll(dates);
                getCirculars();
            }
        });
        return view;
    }

    private void getCirculars() {
        loading =  ProgressDialog.show(getActivity(),"Updating the list","please wait",false,true);

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
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(descriptions,links,dates,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        loading.dismiss();
    }
}
