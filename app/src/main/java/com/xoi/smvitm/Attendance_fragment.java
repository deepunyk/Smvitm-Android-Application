package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.attendance_RV_Adapter;
import com.dinuscxj.refresh.IRefreshStatus;
import com.dinuscxj.refresh.RecyclerRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Attendance_fragment extends Fragment implements IRefreshStatus {

    View view;
    Toolbar toolbar;
    String usn, curClass, br, sn, sm, profile, last_up;
    SharedPreferences sharedPreferences;
    ProgressDialog loader;
    String url = "https://script.google.com/macros/s/AKfycbzSd8DVy3ziW1L8gXKvcSWyY7SXAHsibCX5R0MejRVpvlxTBNWN/exec";
    ArrayList<String> subjects = new ArrayList();
    ArrayList<String> class_attend = new ArrayList();
    ArrayList<String> class_total = new ArrayList();
    RecyclerRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    TextView last_up_txt;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_attendance, container, false);
        refreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.main_swipe);

        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        loader = new ProgressDialog(getActivity());

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        last_up_txt = (TextView)view.findViewById(R.id.last_up_txt);

        usn = sharedPreferences.getString("Student usn", "");

        if(sharedPreferences.contains("Attendance details")){
            try {
                subjects = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Attendance subject", ObjectSerializer.serialize(new ArrayList<String>())));
                class_attend = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Attendance class attend", ObjectSerializer.serialize(new ArrayList<String>())));
                class_total = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Attendance class total", ObjectSerializer.serialize(new ArrayList<String>())));
                last_up = sharedPreferences.getString("Attendance last updated","");
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

    public void refresh(){
        subjects.removeAll(subjects);
        class_total.removeAll(class_total);
        class_attend.removeAll(class_attend);
        getAttendance();
        refreshing();
    }

    public void getAttendance() {
        setCurClass();
        refreshing();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbzSd8DVy3ziW1L8gXKvcSWyY7SXAHsibCX5R0MejRVpvlxTBNWN/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String response_original = response;
                        while (true){
                            if(response.substring(0,1).equals("!")){
                                break;
                            }
                            subjects.add(response.substring(0, response.indexOf(",")));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            class_attend.add(response.substring(0, response.indexOf(",")));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            class_total.add(response.substring(0, response.indexOf(",")));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                        }
                        response = response.substring(response.indexOf(",") + 1, response.length());
                        subjects.add(response.substring(0, response.indexOf(",")));
                        response = response.substring(response.indexOf(",") + 1, response.length());
                        class_attend.add(response.substring(0, response.indexOf(",")));
                        response = response.substring(response.indexOf(",") + 1, response.length());
                        response = response.substring(response.indexOf(",") + 1, response.length());
                        class_total.add(response.substring(0, response.indexOf(",")));
                        response = response.substring(response.indexOf(",") + 1, response.length());
                        last_up = response;
                        refreshComplete();
                        try {
                            sharedPreferences.edit().putString("Attendance subject", ObjectSerializer.serialize(subjects)).apply();
                            sharedPreferences.edit().putString("Attendance class attend", ObjectSerializer.serialize(class_attend)).apply();
                            sharedPreferences.edit().putString("Attendance class total", ObjectSerializer.serialize(class_total)).apply();
                            sharedPreferences.edit().putString("Attendance details", ObjectSerializer.serialize(class_total)).apply();
                            sharedPreferences.edit().putString("Attendance last updated", last_up).apply();
                        }
                        catch (Exception e) {
                        }
                        initRecyclerView();
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

                parmas.put("action",curClass);
                parmas.put("usn",usn);

                return parmas;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }

    private void initRecyclerView(){

        last_up_txt.setText("Last updated on " + last_up);
        ArrayList<String> lsub = new ArrayList<>();
        ArrayList<String> rsub = new ArrayList<>();
        ArrayList<String> lca = new ArrayList<>();
        ArrayList<String> rca = new ArrayList<>();
        ArrayList<String> lct = new ArrayList<>();
        ArrayList<String> rct = new ArrayList<>();

        int j = 0,k=0,l=0;
        for(int i = 0; i<(subjects.size()/2);i++){
            lsub.add(subjects.get(j++));
            rsub.add(subjects.get(j++));
            lca.add(class_attend.get(k++));
            rca.add(class_attend.get(k++));
            lct.add(class_total.get(l++));
            rct.add(class_total.get(l++));
        }
        recyclerView = view.findViewById(R.id.recyclerview);
        attendance_RV_Adapter adapter = new attendance_RV_Adapter(lsub,rsub,lca,rca,lct,rct,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setRefreshing(false);
        refreshComplete();
    }

    private void setCurClass() {

        profile = sharedPreferences.getString("login_Activity", "");
        if (profile.equals("1")) {
            sn = sharedPreferences.getString("Student section", "A");
            br = sharedPreferences.getString("Student branch", "Computer Science");
            sm = sharedPreferences.getString("Student sem", "1");
        } else {
            Toast.makeText(getActivity(), "Only students can view their attendance", Toast.LENGTH_LONG).show();
        }

        if (br.equals("Computer Science")) {
            if (sm.equals("1")) {
                if (sn.equals("A")) {
                    curClass = "CSE1A";
                } else if (sn.equals("B")) {
                    curClass = "CSE1B";
                } else {
                    curClass = "CSE1C";
                }
            } else if (sm.equals("3")) {
                if (sn.equals("A")) {
                    curClass = "CSE2A";
                } else if (sn.equals("B")) {
                    curClass = "CSE2B";
                } else {
                    curClass = "CSE2C";
                }
            } else if (sm.equals("5")) {
                if (sn.equals("A")) {
                    curClass = "CSE3A";
                } else if (sn.equals("B")) {
                    curClass = "CSE3B";
                } else {
                    curClass = "CSE3C";
                }
            } else {
                if (sn.equals("A")) {
                    curClass = "CSE4A";
                } else if (sn.equals("B")) {
                    curClass = "CSE4B";
                } else {
                    curClass = "CSE4C";
                }
            }
        } else if (br.equals("Electronics")) {
            if (sm.equals("1")) {
                if (sn.equals("A")) {
                    curClass = "ECE1A";
                } else if (sn.equals("B")) {
                    curClass = "ECE1B";
                } else {
                    curClass = "ECE1C";
                }
            } else if (sm.equals("3")) {
                if (sn.equals("A")) {
                    curClass = "ECE2A";
                } else if (sn.equals("B")) {
                    curClass = "ECE2B";
                } else {
                    curClass = "ECE2C";
                }
            } else if (sm.equals("5")) {
                if (sn.equals("A")) {
                    curClass = "ECE3A";
                } else if (sn.equals("B")) {
                    curClass = "ECE3B";
                } else {
                    curClass = "ECE3C";
                }
            } else {
                if (sn.equals("A")) {
                    curClass = "ECE4A";
                } else if (sn.equals("B")) {
                    curClass = "ECE4B";
                } else {
                    curClass = "ECE4C";
                }
            }
        } else if (br.equals("Mechanical")) {
            if (sm.equals("1")) {
                if (sn.equals("A")) {
                    curClass = "MECH1A";
                } else if (sn.equals("B")) {
                    curClass = "MECH1B";
                } else {
                    curClass = "MECH1C";
                }
            } else if (sm.equals("3")) {
                if (sn.equals("A")) {
                    curClass = "MECH2A";
                } else if (sn.equals("B")) {
                    curClass = "MECH2B";
                } else {
                    curClass = "MECH2C";
                }
            } else if (sm.equals("5")) {
                if (sn.equals("A")) {
                    curClass = "MECH3A";
                } else if (sn.equals("B")) {
                    curClass = "MECH3B";
                } else {
                    curClass = "MECH3C";
                }
            } else {
                if (sn.equals("A")) {
                    curClass = "MECH4A";
                } else if (sn.equals("B")) {
                    curClass = "MECH4B";
                } else {
                    curClass = "MECH4C";
                }
            }
        } else {
            if (sm.equals("1")) {
                if (sn.equals("A")) {
                    curClass = "CIV1A";
                } else if (sn.equals("B")) {
                    curClass = "CIV1B";
                } else {
                    curClass = "CIV1C";
                }
            } else if (sm.equals("3")) {
                if (sn.equals("A")) {
                    curClass = "CIV2A";
                } else if (sn.equals("B")) {
                    curClass = "CIV2B";
                } else {
                    curClass = "CIV2C";
                }
            } else if (sm.equals("5")) {
                if (sn.equals("A")) {
                    curClass = "CIV3A";
                } else if (sn.equals("B")) {
                    curClass = "CIV3B";
                } else {
                    curClass = "CIV3C";
                }
            } else {
                if (sn.equals("A")) {
                    curClass = "CIV4A";
                } else if (sn.equals("B")) {
                    curClass = "CIV4B";
                } else {
                    curClass = "CIV4C";
                }
            }
        }

    }

    @Override
    public void reset() {

    }

    @Override
    public void refreshing() {
        loader.setTitle("Updating attendance list");
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
