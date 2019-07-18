package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Faculty_profile_activity extends AppCompatActivity {

    TextView name, branch;
    SharedPreferences sharedPreferences;
    String fname, fbranch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_profile_activity);
        name = (TextView)findViewById(R.id.Fname);
        branch = (TextView)findViewById(R.id.Fbranch);
        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        if(sharedPreferences.contains("Faculty name")){
            fname = sharedPreferences.getString("Faculty name","");
            fbranch = sharedPreferences.getString("Faculty branch","");
        }
        else{
            Intent i = new Intent(Faculty_profile_activity.this, Edit_New_Faculty_Profile.class);
            startActivity(i);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
        }

    }

    public void onBackPressed() {
        Intent go = new Intent(Faculty_profile_activity.this, MainActivity.class);
        this.startActivity(go);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }
}
