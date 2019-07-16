package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class Popup_timetable_activity extends AppCompatActivity {

    MaterialSpinner sem_select, branch_select, section_select;
    String[] branch_list = {"Computer Science","Electronics","Mechanical","Civil"};
    String[] section_list = {"A","B","C"};
    String[] sem_list = {"1","3","5","7"};
    int brancht, semt, sectiont;
    String sbranch, ssection, ssem;
    Button apply;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_timetable_activity);

        branch_select = (MaterialSpinner)findViewById(R.id.branch_select);
        sem_select = (MaterialSpinner)findViewById(R.id.sem_select);
        section_select = (MaterialSpinner)findViewById(R.id.section_select);
        branch_select.setItems(branch_list);
        sem_select.setItems(sem_list);
        section_select.setItems(section_list);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);

        apply = (Button)findViewById(R.id.apply);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brancht = branch_select.getSelectedIndex();
                semt = sem_select.getSelectedIndex();
                sectiont = section_select.getSelectedIndex();
                sbranch = branch_list[brancht];
                ssection = section_list[sectiont];
                ssem = sem_list[semt];
                sharedPreferences.edit().putString("T branch", sbranch).apply();
                sharedPreferences.edit().putString("T sem", sbranch).apply();
                sharedPreferences.edit().putString("T section", sbranch).apply();
                finish();
            }
        });

    }
}
