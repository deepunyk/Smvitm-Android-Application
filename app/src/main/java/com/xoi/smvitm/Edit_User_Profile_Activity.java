package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

public class Edit_User_Profile_Activity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String student_branch, student_email, student_usn, student_semester, student_section, student_name;
    EditText name, usn, branch;
    MaterialSpinner section, semester;
    ActionProcessButton submit;
    String[] section_list = {"A" , "B" , "C"};
    String[] semester_list = {"1" , "3" , "5" , "7"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__user__profile_);

        name = (EditText) findViewById(R.id.name);
        usn = (EditText) findViewById(R.id.usn);
        branch = (EditText) findViewById(R.id.branch);
        section = (MaterialSpinner) findViewById(R.id.section);
        semester = (MaterialSpinner) findViewById(R.id.semester);
        submit = (ActionProcessButton) findViewById(R.id.submit);

        submit.setMode(ActionProcessButton.Mode.ENDLESS);

        usn.setInputType(0);
        branch.setInputType(0);

        section.setItems(section_list);
        semester.setItems(semester_list);

        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        student_usn = sharedPreferences.getString("stud_usn", "");
        student_email = sharedPreferences.getString("Student Email", "");
        student_branch = student_email.substring(student_email.indexOf(".") + 3, student_email.indexOf(".") + 5);
        getBranch(student_branch);
        sharedPreferences.edit().putString("Student branch", student_branch).apply();
        branch.setText(student_branch);
        usn.setText(student_usn);

        section.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                student_section = section_list[position];
            }
        });
        semester.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                student_semester = semester_list[position];
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit.setProgress(1);
                student_name = name.getText().toString();
                student_semester = semester_list[semester.getSelectedIndex()];
                student_section = section_list[section.getSelectedIndex()];
                if (student_name.equals("")){
                    Toast.makeText(Edit_User_Profile_Activity.this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
                    submit.setProgress(100);
                    submit.setText("Submit");
                    submit.isEnabled();
                }
                else {
                    addNewUserDetails();
                    submit.setEnabled(false);
                }
            }
        });

    }

    private void addNewUserDetails() {
        final String studName = student_usn;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxIVco5JVe3XJhe0JNEl1zPSCJO-2FFYT6YE1FW8d58VSUUCV8/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Success")) {
                            submit.setProgress(100);
                            sharedPreferences.edit().putString("User details", "1").apply();
                            sharedPreferences.edit().putString("Student name", student_name).apply();
                            sharedPreferences.edit().putString("Student branch", student_branch).apply();
                            sharedPreferences.edit().putString("Student sem", student_semester).apply();
                            sharedPreferences.edit().putString("Student usn", student_usn).apply();
                            sharedPreferences.edit().putString("Student section", student_section).apply();
                            sharedPreferences.edit().putString("Student Email", student_email).apply();
                            Intent go = new Intent(Edit_User_Profile_Activity.this, User_profile_Activity.class);
                            startActivity(go);
                            finish();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                parmas.put("action", "addNewStudent");
                parmas.put("name", student_name);
                parmas.put("branch", student_branch);
                parmas.put("semester", student_semester);
                parmas.put("section", student_section);
                parmas.put("usn", student_usn);
                return parmas;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void getBranch(String branch) {
        switch (student_branch) {
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
