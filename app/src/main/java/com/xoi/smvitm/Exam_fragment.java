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

public class Exam_fragment extends Fragment implements IRefreshStatus{

    View view;
    SharedPreferences sharedPreferences;
    ProgressDialog loader;
    RecyclerRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> portion = new ArrayList<>();
    String curClass;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_exam, container, false);

        refreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.main_swipe);
        loader = new ProgressDialog(getActivity());



        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);

        setClass();

        if(sharedPreferences.contains("Exam description")) {
            try {
                date = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Exam date", ObjectSerializer.serialize(new ArrayList<String>())));
                time = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Exam time", ObjectSerializer.serialize(new ArrayList<String>())));
                name = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Exam name", ObjectSerializer.serialize(new ArrayList<String>())));
                portion = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Exam portion", ObjectSerializer.serialize(new ArrayList<String>())));
                initRecyclerView();
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
        date.removeAll(date);
        time.removeAll(time);
        name.removeAll(name);
        portion.removeAll(portion);
        getExams();
        refreshing();
    }

    private void getExams() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbwwK8c4u6cSDuUIU5rhTphcRYlVpLRlF2B4UNLasBppgmO85F5r/exec?action=" + curClass,
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
            JSONArray jarray = jobj.getJSONArray("exams");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String jsonDescription = jo.getString("date");
                date.add(jsonDescription);
                String jsonDate = jo.getString("time");
                time.add(jsonDate);
                String jsonPortion = jo.getString("portion");
                portion.add(jsonPortion);
                String jsonName = jo.getString("name");
                name.add(jsonName);
            }
            if(date.get(0).equals("-")){
                Toast.makeText(getActivity(), "No data found. Please try again after sometime", Toast.LENGTH_LONG).show();
                refreshComplete();
                refreshLayout.setRefreshing(false);
            }
            else {
                try {
                    sharedPreferences.edit().putString("Exam date", ObjectSerializer.serialize(date)).apply();
                    sharedPreferences.edit().putString("Exam time", ObjectSerializer.serialize(time)).apply();
                    sharedPreferences.edit().putString("Exam portion", ObjectSerializer.serialize(portion)).apply();
                    sharedPreferences.edit().putString("Exam name", ObjectSerializer.serialize(name)).apply();
                    sharedPreferences.edit().putString("Exam description", "1").apply();
                } catch (Exception e) {
                }
                initRecyclerView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(){
        recyclerView = view.findViewById(R.id.recyclerview);
        exam_rv_adapter adapter = new exam_rv_adapter(date,time,name,portion,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setRefreshing(false);
        refreshComplete();
    }

    private void setClass() {

        String sn, br, sm;

        sn = sharedPreferences.getString("Student section", "A");
        br = sharedPreferences.getString("Student branch", "Computer Science");
        sm = sharedPreferences.getString("Student sem", "1");

        if (br.equals("Computer Science")) {
            if (sm.equals("1")) {
                if (sn.equals("A")) {
                    curClass = "CSE1";
                } else if (sn.equals("B")) {
                    curClass = "CSE1";
                } else {
                    curClass = "CSE1";
                }
            } else if (sm.equals("3")) {
                if (sn.equals("A")) {
                    curClass = "CSE2";
                } else if (sn.equals("B")) {
                    curClass = "CSE2";
                } else {
                    curClass = "CSE2";
                }
            } else if (sm.equals("5")) {
                if (sn.equals("A")) {
                    curClass = "CSE3";
                } else if (sn.equals("B")) {
                    curClass = "CSE3";
                } else {
                    curClass = "CSE3";
                }
            } else {
                if (sn.equals("A")) {
                    curClass = "CSE4";
                } else if (sn.equals("B")) {
                    curClass = "CSE4";
                } else {
                    curClass = "CSE4";
                }
            }
        } else if (br.equals("Electronics")) {
            if (sm.equals("1")) {
                if (sn.equals("A")) {
                    curClass = "ECE1";
                } else if (sn.equals("B")) {
                    curClass = "ECE1";
                } else {
                    curClass = "ECE1";
                }
            } else if (sm.equals("3")) {
                if (sn.equals("A")) {
                    curClass = "ECE2";
                } else if (sn.equals("B")) {
                    curClass = "ECE2";
                } else {
                    curClass = "ECE2";
                }
            } else if (sm.equals("5")) {
                if (sn.equals("A")) {
                    curClass = "ECE3";
                } else if (sn.equals("B")) {
                    curClass = "ECE3";
                } else {
                    curClass = "ECE3";
                }
            } else {
                if (sn.equals("A")) {
                    curClass = "ECE4";
                } else if (sn.equals("B")) {
                    curClass = "ECE4";
                } else {
                    curClass = "ECE4";
                }
            }
        } else if (br.equals("Mechanical")) {
            if (sm.equals("1")) {
                if (sn.equals("A")) {
                    curClass = "MECH1";
                } else if (sn.equals("B")) {
                    curClass = "MECH1";
                } else {
                    curClass = "MECH1";
                }
            } else if (sm.equals("3")) {
                if (sn.equals("A")) {
                    curClass = "MECH2";
                } else if (sn.equals("B")) {
                    curClass = "MECH2";
                } else {
                    curClass = "MECH2";
                }
            } else if (sm.equals("5")) {
                if (sn.equals("A")) {
                    curClass = "MECH3";
                } else if (sn.equals("B")) {
                    curClass = "MECH3";
                } else {
                    curClass = "MECH3";
                }
            } else {
                if (sn.equals("A")) {
                    curClass = "MECH4";
                } else if (sn.equals("B")) {
                    curClass = "MECH4";
                } else {
                    curClass = "MECH4";
                }
            }
        } else {
            if (sm.equals("1")) {
                if (sn.equals("A")) {
                    curClass = "CIV1";
                } else if (sn.equals("B")) {
                    curClass = "CIV1";
                } else {
                    curClass = "CIV1";
                }
            } else if (sm.equals("3")) {
                if (sn.equals("A")) {
                    curClass = "CIV2";
                } else if (sn.equals("B")) {
                    curClass = "CIV2";
                } else {
                    curClass = "CIV2";
                }
            } else if (sm.equals("5")) {
                if (sn.equals("A")) {
                    curClass = "CIV3";
                } else if (sn.equals("B")) {
                    curClass = "CIV3";
                } else {
                    curClass = "CIV3";
                }
            } else {
                if (sn.equals("A")) {
                    curClass = "CIV4";
                } else if (sn.equals("B")) {
                    curClass = "CIV4";
                } else {
                    curClass = "CIV4";
                }
            }
        }

    }

    @Override
    public void reset() {

    }

    @Override
    public void refreshing() {
        loader.setTitle("Updating exam details");
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
