package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class login_faculty extends AppCompatActivity {

    EditText email_fac, pass_fac;
    Button sign;
    String email, pass, branch;
    String fac_name, fac_desig, fac_branch, fac_info, fac_email, fac_photo, fac_pass;
    ProgressDialog loading;
    TextView faculty_txt;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_faculty);

        email_fac = (EditText)findViewById(R.id.email_fac);
        pass_fac = (EditText)findViewById(R.id.pass_fac);
        sign = (Button)findViewById(R.id.sign);
        faculty_txt = (TextView)findViewById(R.id.faculty_txt);

        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign.setAlpha(0.7f);
                sign.setBackgroundColor(getResources().getColor(R.color.silver));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        sign.setAlpha(1f);
                        sign.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                }, 50 );

                email = email_fac.getText().toString();
                pass = pass_fac.getText().toString();
                getBranch();
            }
        });

    }

    private void getBranch(){
        try {
            branch = email.substring(email.indexOf(".") + 1, email.indexOf("@"));
            if (branch.equals("cs")) {
                branch = "cse";
            } else if (branch.equals("ec")) {
                branch = "ece";
            } else if (branch.equals("mech")) {
                branch = "mech";
            } else if (branch.equals("civil")) {
                branch = "civil";
            } else if (branch.equals("maths") || branch.equals("physics") || branch.equals("chemistry")) {
                branch = "basic";
            } else {
                branch = "all";
            }
        }
        catch (Exception e){
            branch = "all";
        }
        loginFaculty();
    }
    private void  loginFaculty() {

        loading = ProgressDialog.show(this,"Fetching faculty details","Please wait");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxm3DdKNuviEY4r2HO4iEnPdssmYLdgFaqreP06JQ0AFEjHNQ/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("No user found")){
                            Toast.makeText(login_faculty.this, "Invalid email", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            fac_name = response.substring(0, response.indexOf(","));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            fac_desig = response.substring(0, response.indexOf(","));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            fac_branch = response.substring(0, response.indexOf(","));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            fac_info = response.substring(0, response.indexOf(","));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            fac_email = response.substring(0, response.indexOf(","));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            fac_photo = response.substring(0, response.indexOf(","));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            fac_pass = response;
                            if(pass.equals(fac_pass)){
                                Toast.makeText(login_faculty.this, "Success", Toast.LENGTH_SHORT).show();
                                sharedPreferences.edit().putString("Faculty name", fac_name).apply();
                                sharedPreferences.edit().putString("Faculty desig", fac_desig).apply();
                                sharedPreferences.edit().putString("Faculty branch", fac_branch).apply();
                                sharedPreferences.edit().putString("Faculty info", fac_info).apply();
                                sharedPreferences.edit().putString("Faculty email", fac_email).apply();
                                sharedPreferences.edit().putString("Faculty photo", fac_photo).apply();
                                sharedPreferences.edit().putString("login_Activity", "2").apply();
                                Intent i = new Intent(login_faculty.this, Faculty_profile_activity.class);
                                startActivity(i);
                                finish();
                            }
                            else{
                                Toast.makeText(login_faculty.this, "Invalid password", Toast.LENGTH_SHORT).show();
                            }
                        }
                        loading.dismiss();
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

                parmas.put("action",branch);
                parmas.put("email",email);

                return parmas;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(login_faculty.this,login_Activity.class);
        startActivity(i);
        finish();
    }
}
