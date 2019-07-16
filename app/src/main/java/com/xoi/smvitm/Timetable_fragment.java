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

    private ArrayList<String> firsts = new ArrayList<>();
    private ArrayList<String> seconds = new ArrayList<>();
    private ArrayList<String> thirds = new ArrayList<>();
    private ArrayList<String> fourths = new ArrayList<>();
    private ArrayList<String> fifths = new ArrayList<>();
    private ArrayList<String> sixths = new ArrayList<>();
    private ArrayList<String> sevenths = new ArrayList<>();
    private ArrayList<String> classes = new ArrayList<>();
    private ArrayList<String> timings = new ArrayList<>();

    String today_day;
    String[] day_list = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    View view;
    SharedPreferences sharedPreferences;
    ArrayList<String> first, second, third, fourth, fifth, sixth, seventh;
    RecyclerView recyclerView;
    RecyclerRefreshLayout refreshLayout;
    ProgressDialog loader;
    BubbleNavigationConstraintView bv;
    String branch, sem, section;
    String curClass;
    int prevPos;
    int weekDay = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_timebtable, container, false);

        first = new ArrayList<>();
        second = new ArrayList<>();
        third = new ArrayList<>();
        fourth = new ArrayList<>();
        fifth = new ArrayList<>();
        sixth = new ArrayList<>();
        seventh = new ArrayList<>();

        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        section = sharedPreferences.getString("Student section", "");
        branch = sharedPreferences.getString("Student branch", "");
        sem = sharedPreferences.getString("Student sem", "");
        sharedPreferences.edit().remove("T section").apply();
        sharedPreferences.edit().remove("T branch").apply();
        sharedPreferences.edit().remove("T sem").apply();

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
        first.removeAll(first);
        second.removeAll(second);
        third.removeAll(third);
        fourth.removeAll(fourth);
        fifth.removeAll(fifth);
        sixth.removeAll(sixth);
        seventh.removeAll(seventh);
        classes.removeAll(classes);
        timings.removeAll(timings);
        if(sharedPreferences.contains("Table download")){
            setData();
        }
        else{
            refresh();
        }

    }

    private void refresh() {
        sharedPreferences.edit().remove("First tt").apply();
        sharedPreferences.edit().remove("Second tt").apply();
        sharedPreferences.edit().remove("Third tt").apply();
        sharedPreferences.edit().remove("Fourth tt").apply();
        sharedPreferences.edit().remove("Fifth tt").apply();
        sharedPreferences.edit().remove("Sixth tt").apply();
        sharedPreferences.edit().remove("Seventh tt").apply();
        first.removeAll(first);
        second.removeAll(second);
        third.removeAll(third);
        fourth.removeAll(fourth);
        fifth.removeAll(fifth);
        sixth.removeAll(sixth);
        seventh.removeAll(seventh);
        classes.removeAll(classes);
        timings.removeAll(timings);
        getTimetable();
        refreshing();
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


    private void getTimetable() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbyHjV637lf3NmMpuk4mOtCHBGZLGSgqY3nlfh0uAAdnW00ke02x/exec",
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
            JSONArray jarray = jobj.getJSONArray(curClass);
            Toast.makeText(getActivity(), curClass, Toast.LENGTH_SHORT).show();
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String first_json = jo.getString("First");
                firsts.add(first_json);
                String second_json = jo.getString("Second");
                seconds.add(second_json);
                String third_json = jo.getString("Third");
                thirds.add(third_json);
                String fourth_json = jo.getString("Fourth");
                fourths.add(fourth_json);
                String fifth_json = jo.getString("Fifth");
                fifths.add(fifth_json);
                String sixth_json = jo.getString("Sixth");
                sixths.add(sixth_json);
                String seventh_json = jo.getString("Seventh");
                sevenths.add(seventh_json);
            }
            downloadData();
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Error while downloading data", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadData(){
        try {
            sharedPreferences.edit().putString("First tt", ObjectSerializer.serialize(firsts)).apply();
            sharedPreferences.edit().putString("Second tt", ObjectSerializer.serialize(seconds)).apply();
            sharedPreferences.edit().putString("Third tt", ObjectSerializer.serialize(thirds)).apply();
            sharedPreferences.edit().putString("Fourth tt", ObjectSerializer.serialize(fourths)).apply();
            sharedPreferences.edit().putString("Fifth tt", ObjectSerializer.serialize(fifths)).apply();
            sharedPreferences.edit().putString("Sixth tt", ObjectSerializer.serialize(sixths)).apply();
            sharedPreferences.edit().putString("Seventh tt", ObjectSerializer.serialize(sevenths)).apply();
            sharedPreferences.edit().putString("Table download", "1").apply();
            setData();
        }
        catch (Exception e){
            Toast.makeText(getActivity(), "Server Error. Please try restarting the app", Toast.LENGTH_SHORT).show();
        }

    }

    private void setData(){
        try {
            firsts = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("First tt", ObjectSerializer.serialize(new ArrayList<String>())));
            seconds = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Second tt", ObjectSerializer.serialize(new ArrayList<String>())));
            thirds = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Third tt", ObjectSerializer.serialize(new ArrayList<String>())));
            fourths = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Fourth tt", ObjectSerializer.serialize(new ArrayList<String>())));
            fifths = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Fifth tt", ObjectSerializer.serialize(new ArrayList<String>())));
            sixths = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Sixth tt", ObjectSerializer.serialize(new ArrayList<String>())));
            sevenths = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Seventh tt", ObjectSerializer.serialize(new ArrayList<String>())));
            timings.add(firsts.get(0));
            timings.add(seconds.get(0));
            timings.add(thirds.get(0));
            timings.add(fourths.get(0));
            timings.add(fifths.get(0));
            timings.add(sixths.get(0));
            timings.add(sevenths.get(0));
            classes.add(firsts.get(weekDay));
            classes.add(seconds.get(weekDay));
            classes.add(thirds.get(weekDay));
            classes.add(fourths.get(weekDay));
            classes.add(fifths.get(weekDay));
            classes.add(sixths.get(weekDay));
            classes.add(sevenths.get(weekDay));
        }
        catch (Exception e){
            Toast.makeText(getActivity(), "Failed to update the timetable. Please restart the app and try again", Toast.LENGTH_SHORT).show();
        }
        initRecyclerView();
    }

    private void initRecyclerView() {
        recyclerView = view.findViewById(R.id.recyclerview);
        Timetable_Recycler_ViewAdapter adapter = new Timetable_Recycler_ViewAdapter(classes,timings,getActivity());
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

}
