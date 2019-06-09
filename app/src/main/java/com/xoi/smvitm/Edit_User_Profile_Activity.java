package com.xoi.smvitm;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class Edit_User_Profile_Activity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String student_branch, student_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__user__profile_);
        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        student_email = sharedPreferences.getString("Student Email", "");
        student_branch = student_email.substring(student_email.indexOf(".") + 3, student_email.indexOf(".")+5);
        Toast.makeText(this, student_branch, Toast.LENGTH_SHORT).show();
        getBranch(student_branch);
        sharedPreferences.edit().putString("Student branch", student_branch).apply();
    }
    private void getBranch(String branch){
        switch (student_branch){
            case "cs":
                student_branch = "Computer Science";
                break;
            case "ec":
                student_branch = "Electronics";
                break;
            case "me":
                student_branch = "Mechanical";
                break;
            case "cv":
                student_branch = "Civil";
                break;
            default:
                student_branch = "N/A";
        }
    }
}
