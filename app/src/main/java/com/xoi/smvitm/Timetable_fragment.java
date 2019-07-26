package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextClock;
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
import com.baoyachi.stepview.VerticalStepView;
import com.dd.processbutton.iml.ActionProcessButton;
import com.dinuscxj.refresh.IRefreshStatus;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class Timetable_fragment extends Fragment implements IRefreshStatus {

    private ArrayList<String> arr_time = new ArrayList<>();
    private ArrayList<String> arr_monday = new ArrayList<>();
    private ArrayList<String> arr_tuesday = new ArrayList<>();
    private ArrayList<String> arr_wednesday = new ArrayList<>();
    private ArrayList<String> arr_thursday = new ArrayList<>();
    private ArrayList<String> arr_friday = new ArrayList<>();
    private ArrayList<String> arr_saturday = new ArrayList<>();
    private ArrayList<String> arr_curDay = new ArrayList<>();

    String url = "https://script.google.com/macros/s/AKfycbyHjV637lf3NmMpuk4mOtCHBGZLGSgqY3nlfh0uAAdnW00ke02x/exec?action=";
    String today_day;
    String[] day_list = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    View view;
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    RecyclerRefreshLayout refreshLayout;
    ProgressDialog loader;
    BubbleNavigationConstraintView bv;
    String branch, sem, section;
    String curClass;
    public static int n = 0;
    int prevPos;
    int weekDay = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_timebtable, container, false);

        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        section = sharedPreferences.getString("Student section", "A");
        branch = sharedPreferences.getString("Student branch", "Computer Science");
        sem = sharedPreferences.getString("Student sem", "A");
        sharedPreferences.edit().remove("T section").apply();
        sharedPreferences.edit().remove("T branch").apply();
        sharedPreferences.edit().remove("T sem").apply();
        //sharedPreferences.edit().remove("Table download").apply();

        setCurClass(getActivity(), branch, sem, section);

        bv = (BubbleNavigationConstraintView) view.findViewById(R.id.bubble_nav_view1);

        refreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.main_swipe);
        loader = new ProgressDialog(getActivity());

        getDay();

        checkDownload();


        refreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        bv.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                weekDay = position+1;
                if(position == 6){
                    bv.setCurrentActiveItem(prevPos);
                    Intent go = new Intent(getActivity(), Popup_timetable_activity.class);
                    startActivity(go);
                    getActivity().overridePendingTransition(R.anim.zoom, R.anim.stay);
                }
                else {
                    prevPos = position;
                    today_day = day_list[position];
                    checkDownload();
                }
            }
        });


        return view;
    }

    private void checkDownload(){
        if(sharedPreferences.contains("Table download")){
            setData();
        }
        else{
            refresh();
        }
    }

    private void refresh() {
        sharedPreferences.edit().remove("Timetable monday").apply();
        sharedPreferences.edit().remove("Timetable tuesday").apply();
        sharedPreferences.edit().remove("Timetable wednesday").apply();
        sharedPreferences.edit().remove("Timetable thursday").apply();
        sharedPreferences.edit().remove("Timetable friday").apply();
        sharedPreferences.edit().remove("Timetable saturday").apply();
        sharedPreferences.edit().remove("Timetable time").apply();
        sharedPreferences.edit().remove("Table download").apply();
        arr_time.removeAll(arr_time);
        arr_monday.removeAll(arr_monday);
        arr_tuesday.removeAll(arr_tuesday);
        arr_wednesday.removeAll(arr_wednesday);
        arr_thursday.removeAll(arr_thursday);
        arr_friday.removeAll(arr_friday);
        arr_saturday.removeAll(arr_saturday);
        arr_curDay.removeAll(arr_curDay);
        getTimetable();
        refreshing();
    }

    private void getTimetable() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url+curClass,
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
            JSONArray jarray = jobj.getJSONArray("class");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String monday_json = jo.getString("monday");
                arr_monday.add(monday_json);
                String tuesday_json = jo.getString("tuesday");
                arr_tuesday.add(tuesday_json);
                String wednesday_json = jo.getString("wednesday");
                arr_wednesday.add(wednesday_json);
                String thursday_json = jo.getString("thursday");
                arr_thursday.add(thursday_json);
                String friday_json = jo.getString("friday");
                arr_friday.add(friday_json);
                String saturday_json = jo.getString("saturday");
                arr_saturday.add(saturday_json);
                String time_json = jo.getString("time");
                arr_time.add(time_json);
            }
            try{
                sharedPreferences.edit().putString("Timetable monday", ObjectSerializer.serialize(arr_monday)).apply();
                sharedPreferences.edit().putString("Timetable tuesday", ObjectSerializer.serialize(arr_tuesday)).apply();
                sharedPreferences.edit().putString("Timetable wednesday", ObjectSerializer.serialize(arr_wednesday)).apply();
                sharedPreferences.edit().putString("Timetable thursday", ObjectSerializer.serialize(arr_thursday)).apply();
                sharedPreferences.edit().putString("Timetable friday", ObjectSerializer.serialize(arr_friday)).apply();
                sharedPreferences.edit().putString("Timetable saturday", ObjectSerializer.serialize(arr_saturday)).apply();
                sharedPreferences.edit().putString("Timetable time", ObjectSerializer.serialize(arr_time)).apply();
            }
            catch (Exception e){
                Toast.makeText(getActivity(), ""+e, Toast.LENGTH_SHORT).show();
            }
            setData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setData(){
        try {
            sharedPreferences.edit().putString("Table download", "1").apply();
            arr_time.removeAll(arr_time);
            arr_monday.removeAll(arr_monday);
            arr_tuesday.removeAll(arr_tuesday);
            arr_wednesday.removeAll(arr_wednesday);
            arr_thursday.removeAll(arr_thursday);
            arr_friday.removeAll(arr_friday);
            arr_saturday.removeAll(arr_saturday);
            arr_curDay.removeAll(arr_curDay);
            arr_monday = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Timetable monday", ObjectSerializer.serialize(new ArrayList<String>())));
            arr_tuesday = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Timetable tuesday", ObjectSerializer.serialize(new ArrayList<String>())));
            arr_wednesday = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Timetable wednesday", ObjectSerializer.serialize(new ArrayList<String>())));
            arr_thursday = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Timetable thursday", ObjectSerializer.serialize(new ArrayList<String>())));
            arr_friday = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Timetable friday", ObjectSerializer.serialize(new ArrayList<String>())));
            arr_saturday = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Timetable saturday", ObjectSerializer.serialize(new ArrayList<String>())));
            arr_time = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Timetable time", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            //refresh();
        }

        if(today_day.equals("Monday")){
            arr_curDay = arr_monday;
        }
        else if(today_day.equals("Tuesday")){
            arr_curDay = arr_tuesday;
        }
        else if(today_day.equals("Wednesday")){
            arr_curDay = arr_wednesday;
        }
        else if(today_day.equals("Thursday")){
            arr_curDay = arr_thursday;
        }else if(today_day.equals("Friday")){
            arr_curDay = arr_friday;
        }
        else if(today_day.equals("Saturday")){
            arr_curDay = arr_saturday;
        }else{
            arr_curDay = arr_monday;
        }
        initRecyclerView();

    }
    private void initRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerview);
        Timetable_Recycler_ViewAdapter adapter = new Timetable_Recycler_ViewAdapter(arr_curDay,arr_time,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setRefreshing(false);
        refreshComplete();
    }

    private void getDay() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (Calendar.MONDAY == dayOfWeek) {
            weekDay = 1;
        } else if (Calendar.TUESDAY == dayOfWeek) {
            weekDay = 2;
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            weekDay = 3;
        } else if (Calendar.THURSDAY == dayOfWeek) {
            weekDay = 4;
        } else if (Calendar.FRIDAY == dayOfWeek) {
            weekDay = 5;
        } else if (Calendar.SATURDAY == dayOfWeek) {
            weekDay = 6;
        } else if (Calendar.SUNDAY == dayOfWeek) {
            weekDay = 1;
        }
        switch (weekDay) {
            case 1:
                today_day = "Monday";
                bv.setCurrentActiveItem(0);
                prevPos = 0;
                break;
            case 2:
                today_day = "Tuesday";
                bv.setCurrentActiveItem(1);
                prevPos = 1;
                break;
            case 3:
                today_day = "Wednesday";
                bv.setCurrentActiveItem(2);
                prevPos = 2;
                break;
            case 4:
                today_day = "Thursday";
                bv.setCurrentActiveItem(3);
                prevPos = 3;
                break;
            case 5:
                today_day = "Friday";
                bv.setCurrentActiveItem(4);
                prevPos = 4;
                break;
            case 6:
                today_day = "Saturday";
                bv.setCurrentActiveItem(5);
                prevPos = 5;
                break;
            default:
                today_day = "Monday";
                bv.setCurrentActiveItem(0);
                prevPos = 0;
        }
    }

    private void setCurClass(Context context, String br, String sm, String sn){
        if(br.equals("Computer Science")){
            if(sm.equals("1")){
                if(sn.equals("A")){
                    curClass = "CSE1A";
                }
                else if(sn.equals("B")){
                    curClass = "CSE1B";
                }
                else{
                    curClass = "CSE1C";
                }
            }
            else if(sm.equals("3")){
                if(sn.equals("A")){
                    curClass = "CSE2A";
                }
                else if(sn.equals("B")){
                    curClass = "CSE2B";
                }
                else{
                    curClass = "CSE2C";
                }
            }
            else if(sm.equals("5")){
                if(sn.equals("A")){
                    curClass = "CSE3A";
                }
                else if(sn.equals("B")){
                    curClass = "CSE3B";
                }
                else{
                    curClass = "CSE3C";
                }
            }
            else{
                if(sn.equals("A")){
                    curClass = "CSE4A";
                }
                else if(sn.equals("B")){
                    curClass = "CSE4B";
                }
                else{
                    curClass = "CSE4C";
                }
            }
        }
        else if(br.equals("Electronics")){
            if(sm.equals("1")){
                if(sn.equals("A")){
                    curClass = "ECE1A";
                }
                else if(sn.equals("B")){
                    curClass = "ECE1B";
                }
                else{
                    curClass = "ECE1C";
                }
            }
            else if(sm.equals("3")){
                if(sn.equals("A")){
                    curClass = "ECE2A";
                }
                else if(sn.equals("B")){
                    curClass = "ECE2B";
                }
                else{
                    curClass = "ECE2C";
                }
            }
            else if(sm.equals("5")){
                if(sn.equals("A")){
                    curClass = "ECE3A";
                }
                else if(sn.equals("B")){
                    curClass = "ECE3B";
                }
                else{
                    curClass = "ECE3C";
                }
            }
            else{
                if(sn.equals("A")){
                    curClass = "ECE4A";
                }
                else if(sn.equals("B")){
                    curClass = "ECE4B";
                }
                else{
                    curClass = "ECE4C";
                }
            }
        }
        else if(br.equals("Mechanical")){
            if(sm.equals("1")){
                if(sn.equals("A")){
                    curClass = "MECH1A";
                }
                else if(sn.equals("B")){
                    curClass = "MECH1B";
                }
                else{
                    curClass = "MECH1C";
                }
            }
            else if(sm.equals("3")){
                if(sn.equals("A")){
                    curClass = "MECH2A";
                }
                else if(sn.equals("B")){
                    curClass = "MECH2B";
                }
                else{
                    curClass = "MECH2C";
                }
            }
            else if(sm.equals("5")){
                if(sn.equals("A")){
                    curClass = "MECH3A";
                }
                else if(sn.equals("B")){
                    curClass = "MECH3B";
                }
                else{
                    curClass = "MECH3C";
                }
            }
            else{
                if(sn.equals("A")){
                    curClass = "MECH4A";
                }
                else if(sn.equals("B")){
                    curClass = "MECH4B";
                }
                else{
                    curClass = "MECH4C";
                }
            }
        }
        else{
            if(sm.equals("1")){
                if(sn.equals("A")){
                    curClass = "CIV1A";
                }
                else if(sn.equals("B")){
                    curClass = "CIV1B";
                }
                else{
                    curClass = "CIV1C";
                }
            }
            else if(sm.equals("3")){
                if(sn.equals("A")){
                    curClass = "CIV2A";
                }
                else if(sn.equals("B")){
                    curClass = "CIV2B";
                }
                else{
                    curClass = "CIV2C";
                }
            }
            else if(sm.equals("5")){
                if(sn.equals("A")){
                    curClass = "CIV3A";
                }
                else if(sn.equals("B")){
                    curClass = "CIV3B";
                }
                else{
                    curClass = "CIV3C";
                }
            }
            else{
                if(sn.equals("A")){
                    curClass = "CIV4A";
                }
                else if(sn.equals("B")){
                    curClass = "CIV4B";
                }
                else{
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
        loader.setTitle("Updating timetable");
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

    @Override
    public void onResume() {
        super.onResume();
        if(sharedPreferences.contains("T branch"))
        {
            branch = sharedPreferences.getString("T branch", "");
            section = sharedPreferences.getString("T section", "");
            sem = sharedPreferences.getString("T sem", "");
            setCurClass(getActivity(), branch, sem, section);
            Toast.makeText(getActivity(), ""+branch + section + sem, Toast.LENGTH_LONG).show();
            refresh();
        }
    }
}
