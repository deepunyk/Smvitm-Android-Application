package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;

public class Timetable_new extends AppCompatActivity {

    private ArrayList<String> arr_time = new ArrayList<>();
    private ArrayList<String> arr_monday = new ArrayList<>();
    private ArrayList<String> arr_tuesday = new ArrayList<>();
    private ArrayList<String> arr_wednesday = new ArrayList<>();
    private ArrayList<String> arr_thursday = new ArrayList<>();
    private ArrayList<String> arr_friday = new ArrayList<>();
    private ArrayList<String> arr_saturday = new ArrayList<>();

    View view;
    SharedPreferences sharedPreferences;
    ProgressDialog loader;
    Button refresh;
    RecyclerRefreshLayout refreshLayout;
    RecyclerView recyclerView;
    String url = "https://script.google.com/macros/s/AKfycbyHjV637lf3NmMpuk4mOtCHBGZLGSgqY3nlfh0uAAdnW00ke02x/exec?action=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_new);

    }
}
