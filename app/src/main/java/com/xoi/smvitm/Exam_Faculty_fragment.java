package com.xoi.smvitm;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.webianks.library.scroll_choice.ScrollChoice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Exam_Faculty_fragment extends Fragment {

    View view;
    ScrollChoice class_scroll;
    ConstraintLayout classDetails_layout,examDetails_layout;
    Button submit_but;
    RecyclerView recyclerView;
    int selectedClass;
    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> portion = new ArrayList<>();
    List<String> selectedClass_data = new ArrayList<>();
    ProgressDialog loader;
    List<String> class_data = new ArrayList<>();


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_exam_faculty, container, false);

        class_scroll = (ScrollChoice) view.findViewById(R.id.scroll_choice);
        submit_but = (Button)view.findViewById(R.id.submit_but);
        classDetails_layout = (ConstraintLayout)view.findViewById(R.id.classDetails_layout);
        examDetails_layout = (ConstraintLayout)view.findViewById(R.id.examDetails_layout);
        loader = new ProgressDialog(getActivity());

        class_data.add("CSE 1st year");
        class_data.add("CSE 2nd year");
        class_data.add("CSE 3rd year");
        class_data.add("CSE 4th year");
        class_data.add("ECE 1st year");
        class_data.add("ECE 2nd year");
        class_data.add("ECE 3rd year");
        class_data.add("ECE 4th year");
        class_data.add("Mech 1st year");
        class_data.add("Mech 2nd year");
        class_data.add("Mech 3rd year");
        class_data.add("Mech 4th year");
        class_data.add("Civil 1st year");
        class_data.add("Civil 2nd year");
        class_data.add("Civil 3rd year");
        class_data.add("Civil 4th year");

        selectedClass_data.add("CSE1");
        selectedClass_data.add("CSE2");
        selectedClass_data.add("CSE3");
        selectedClass_data.add("CSE4");
        selectedClass_data.add("ECE1");
        selectedClass_data.add("ECE2");
        selectedClass_data.add("ECE3");
        selectedClass_data.add("ECE4");
        selectedClass_data.add("MECH1");
        selectedClass_data.add("MECH2");
        selectedClass_data.add("MECH3");
        selectedClass_data.add("MECH4");
        selectedClass_data.add("CIV1");
        selectedClass_data.add("CIV2");
        selectedClass_data.add("CIV3");
        selectedClass_data.add("CIV4");

        class_scroll.addItems(class_data,7);

        submit_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedClass = class_scroll.getCurrentItemPosition();
                classDetails_layout.setVisibility(View.GONE);
                examDetails_layout.setVisibility(View.VISIBLE);
                refreshing();
                getExams();

            }
        });
        return view;
    }

    private void getExams() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbwwK8c4u6cSDuUIU5rhTphcRYlVpLRlF2B4UNLasBppgmO85F5r/exec?action=" + selectedClass_data.get(selectedClass),
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
            }
            else {
                initRecyclerView();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(){
        recyclerView = view.findViewById(R.id.recyclerview);
        exam_faculty_rv_adapter adapter = new exam_faculty_rv_adapter(date,time,name,portion,selectedClass_data.get(selectedClass),getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshComplete();
    }

    public void refreshing() {
        loader.setTitle("Updating exam details");
        loader.setMessage("Please wait...");
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.show();
    }

    public void refreshComplete() {
        loader.dismiss();
    }
}
