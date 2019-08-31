package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class Timetable_offline_activity extends AppCompatActivity {

    private ArrayList<String> arr_time = new ArrayList<>();
    private ArrayList<String> arr_monday = new ArrayList<>();
    private ArrayList<String> arr_tuesday = new ArrayList<>();
    private ArrayList<String> arr_wednesday = new ArrayList<>();
    private ArrayList<String> arr_thursday = new ArrayList<>();
    private ArrayList<String> arr_friday = new ArrayList<>();
    private ArrayList<String> arr_saturday = new ArrayList<>();
    private ArrayList<String> arr_curDay = new ArrayList<>();

    SharedPreferences sharedPreferences;

    String url = "https://script.google.com/macros/s/AKfycbyHjV637lf3NmMpuk4mOtCHBGZLGSgqY3nlfh0uAAdnW00ke02x/exec?action=";
    String today_day;
    String[] day_list = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    RecyclerView recyclerView;
    ProgressDialog loader;
    BubbleNavigationConstraintView bv;
    public static int n = 0;
    int prevPos;
    int weekDay = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_offline_activity);

        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("T section").apply();
        sharedPreferences.edit().remove("T branch").apply();
        sharedPreferences.edit().remove("T sem").apply();

        bv = (BubbleNavigationConstraintView) findViewById(R.id.bubble_nav_view1);

        getDay();

        setData();

        bv.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {
                weekDay = position+1;
                    prevPos = position;
                    today_day = day_list[position];
                    setData();
            }
        });
    }

    private void setData(){
        try {
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
        recyclerView = findViewById(R.id.recyclerview);
        Timetable_Recycler_ViewAdapter adapter = new Timetable_Recycler_ViewAdapter(arr_curDay,arr_time,this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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
}
