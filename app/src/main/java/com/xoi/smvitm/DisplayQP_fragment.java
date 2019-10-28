package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gauravk.bubblenavigation.BubbleToggleView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DisplayQP_fragment extends Fragment {

    View view;
    SharedPreferences sp;
    String selectedClass;
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> link = new ArrayList<>();
    private ArrayList<String> type = new ArrayList<>();
    private ArrayList<String> year = new ArrayList<>();
    private ArrayList<String> code = new ArrayList<>();
    RecyclerView recyclerView;
    String url = "https://script.google.com/macros/s/AKfycbw5xvKc8j0_A8xLex40YNbMXoOMNIhsbY5hPxNOe9UDPd1cQBc/exec?action=";
    LottieAnimationView animationView;
    FloatingActionButton searchBut;
    EditText searchTxt;
    ConstraintLayout searchLayout;
    Display_QP_RV adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_displayqp, container, false);
        sp = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        selectedClass = sp.getString("tempClass","CSE3");
        animationView = (LottieAnimationView)view.findViewById(R.id.animation_view);
        searchBut = (FloatingActionButton)view.findViewById(R.id.search_but);
        searchTxt = (EditText) view.findViewById(R.id.search_txt);
        searchLayout = (ConstraintLayout)view.findViewById(R.id.search_layout);

        searchBut.hide();

        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchBut.hide();
                searchLayout.setVisibility(View.VISIBLE);
            }
        });

        searchTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getQP();

        return view;
    }

    private void getQP() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+""+selectedClass,
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
            JSONArray jarray = jobj.getJSONArray("papers");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String name_json = jo.getString("name");
                name.add(name_json);
                String code_json = jo.getString("code");
                code.add(code_json);
                String link_json = jo.getString("link");
                link.add(link_json);
                String type_json = jo.getString("type");
                type.add(type_json);
                String year_json = jo.getString("year");
                year.add(year_json);
            }
            initRecyclerView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(){

        animationView.animate().scaleX(0f).scaleY(0f).setDuration(500);
        recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.animate().scaleX(0f).scaleY(0f).setDuration(10);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                searchBut.show();
                animationView.setVisibility(View.GONE);
                recyclerView.animate().scaleX(1f).scaleY(1f).setDuration(250);
                adapter = new Display_QP_RV(name,type,year,link,code,getActivity());
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        }, 500);



    }
}
