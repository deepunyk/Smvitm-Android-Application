package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.dinuscxj.refresh.IRefreshStatus;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Faculty_fragment extends Fragment implements IRefreshStatus {

    View view;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> designations = new ArrayList<>();
    private ArrayList<String> branches = new ArrayList<>();
    private ArrayList<String> mobiles = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();
    private ArrayList<String> photolinks = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerRefreshLayout refreshLayout;
    ProgressDialog loader;
    String url = "";
    String cs_url = "https://script.google.com/macros/s/AKfycbxm3DdKNuviEY4r2HO4iEnPdssmYLdgFaqreP06JQ0AFEjHNQ/exec?action=getCse";
    String ece_url = "https://script.google.com/macros/s/AKfycbxm3DdKNuviEY4r2HO4iEnPdssmYLdgFaqreP06JQ0AFEjHNQ/exec?action=getEce";
    String mech_url = "https://script.google.com/macros/s/AKfycbxm3DdKNuviEY4r2HO4iEnPdssmYLdgFaqreP06JQ0AFEjHNQ/exec?action=getMech";
    String civil_url = "https://script.google.com/macros/s/AKfycbxm3DdKNuviEY4r2HO4iEnPdssmYLdgFaqreP06JQ0AFEjHNQ/exec?action=getCivil";
    String basic_url = "https://script.google.com/macros/s/AKfycbxm3DdKNuviEY4r2HO4iEnPdssmYLdgFaqreP06JQ0AFEjHNQ/exec?action=getBasic";
    MaterialSpinner branch_select;
    String[] branch_list = {"Basic Science", "Computer Science", "Electronics" , "Mechanical", "Civil"};
    SharedPreferences sharedPreferences;
    String student_branch;
    String profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_faculty,container,false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerview);
        refreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.main_swipe);
        loader = new ProgressDialog(getActivity());
        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);

        profile = sharedPreferences.getString("login_Activity","");

        if(profile.equals("1")) {
            student_branch = sharedPreferences.getString("Student branch", "");
        }
        else{
            String branch = sharedPreferences.getString("Faculty branch", "");
            if(branch.equals("Computer Science")){
                branch = "Computer Science";
            }
            else if(branch.equals("Electronics & Communication Engineering")){
                branch = "Electronics";
            }
            else if(branch.equals("Mechanical Engineering")){
                branch = "Mechanical";
            }
            else if(branch.equals("Physics")||branch.equals("Mathematics")||branch.equals("Chemistry")){
                branch = "Basic Science";
            }
            else{
                branch = "Civil";
            }
            student_branch = branch;
        }

        url = basic_url;

        branch_select = (MaterialSpinner) view.findViewById(R.id.branch_select);
        branch_select.setItems(branch_list);
        setSpinner();
        branch_select.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                String selected_branch = branch_list[position];
                switch (selected_branch){
                    case "Basic Science":
                        url = basic_url;
                        refresh();
                        break;
                    case "Computer Science":
                        url = cs_url;
                        refresh();
                        break;
                    case "Electronics":
                        url = ece_url;
                        refresh();
                        break;
                    case "Mechanical":
                        url = mech_url;
                        refresh();
                        break;
                    case "Civil":
                        url = civil_url;
                        refresh();
                        break;
                    default:
                        url = basic_url;
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
        names.removeAll(names);
        designations.removeAll(designations);
        branches.removeAll(branches);
        mobiles.removeAll(mobiles);
        emails.removeAll(emails);
        photolinks.removeAll(photolinks);

        getFaculty();
        refreshing();
    }

    private void getFaculty() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url,
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
            JSONArray jarray = jobj.getJSONArray("faculty");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String name_json = jo.getString("name");
                names.add(name_json);
                String designation_json = jo.getString("designation");
                designations.add(designation_json);
                String branch_json = jo.getString("branch");
                branches.add(branch_json);
                String mobile_json = jo.getString("mobile");
                mobiles.add(mobile_json);
                String email_json = jo.getString("email");
                emails.add(email_json);
                String photolink_json = jo.getString("photolink");
                photolinks.add(photolink_json);
            }
            initRecyclerView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initRecyclerView(){
        recyclerView = view.findViewById(R.id.recyclerview);
        faculty_recycler_view_adapter adapter = new faculty_recycler_view_adapter(names,designations,branches,mobiles,emails,photolinks,getActivity());
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
        loader.setTitle("Collecting faculty details");
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

    public void setSpinner(){
        switch (student_branch){
            case "Computer Science":
                branch_select.setSelectedIndex(1);
                url = cs_url;
                refresh();
                break;
            case "Electronics":
                branch_select.setSelectedIndex(2);
                url = ece_url;
                refresh();
                break;
            case "Mechanical":
                branch_select.setSelectedIndex(3);
                url = mech_url;
                refresh();
                break;
            case "Civil":
                branch_select.setSelectedIndex(4);
                url = civil_url;
                refresh();
                break;
            default:
                branch_select.setSelectedIndex(0);
                url = basic_url;
                refresh();
        }
    }
}
