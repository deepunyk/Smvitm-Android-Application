package com.xoi.smvitm;

import android.content.Intent;
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
    int branch, sem, section;
    Button apply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_timetable_activity);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8), (int)(height*.7));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;
        getWindow().setAttributes(params);

        branch_select = (MaterialSpinner)findViewById(R.id.branch_select);
        sem_select = (MaterialSpinner)findViewById(R.id.sem_select);
        section_select = (MaterialSpinner)findViewById(R.id.section_select);
        branch_select.setItems(branch_list);
        sem_select.setItems(sem_list);
        section_select.setItems(section_list);

        apply = (Button)findViewById(R.id.apply);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                branch = branch_select.getSelectedIndex();
                sem = sem_select.getSelectedIndex();
                section = section_select.getSelectedIndex();
                Timetable_fragment.branch = branch;
                Timetable_fragment.sem = sem;
                Timetable_fragment.section = section;
                Timetable_fragment.branch_txt.setText(branch_list[branch]);
                Timetable_fragment.section_txt.setText(section_list[section]);
                Timetable_fragment.sem_txt.setText(sem_list[sem]);
                finish();
            }
        });

    }
}
