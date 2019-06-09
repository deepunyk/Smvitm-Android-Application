package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class User_profile_Activity extends AppCompatActivity {

    TextView name, branch, sem, section,usn;
    Button btnPost, btnGet, signOut;
    ProgressDialog loading;
    Toolbar toolbar;
    String student_name, student_usn, student_branch, student_sem, student_section;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        usn = (TextView)findViewById(R.id.usn);
        name = (TextView)findViewById(R.id.name);
        branch = (TextView)findViewById(R.id.branch);
        sem = (TextView)findViewById(R.id.sem);
        section = (TextView)findViewById(R.id.section);

        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        String def_usn = sharedPreferences.getString("stud_usn", "");
        student_usn = def_usn;

        String detail_check = sharedPreferences.getString("User details", "");
        if(detail_check.equals("1")){
            student_name = sharedPreferences.getString("Student name", "");
            student_sem = sharedPreferences.getString("Student sem", "");
            student_usn = sharedPreferences.getString("Student usn", "");
            student_section = sharedPreferences.getString("Student section", "");
            student_branch = sharedPreferences.getString("Student branch", "");
            usn.setText(student_usn);
            sem.setText(student_sem);
            section.setText(student_section);
            branch.setText(student_branch);
            name.setText(student_name);
        }
        else {
            addItemToSheet();
        }

        btnPost = (Button)findViewById(R.id.btnPost);
        btnGet = (Button)findViewById(R.id.btnGet);
        signOut = (Button)findViewById(R.id.signOut);

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putString("login_Activity", "0").apply();
                FirebaseAuth.getInstance().signOut();
                Intent go = new Intent(User_profile_Activity.this, login_Activity.class);
                startActivity(go);
                finish();
            }
        });

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_icon));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(User_profile_Activity.this, MainActivity.class);
                startActivity(main);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        toolbar.setTitle("User Profile");
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToSheet();
            }
        });

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getItems();
            }
        });

    }

    private void   addItemToSheet() {

        loading = ProgressDialog.show(this,"Fetching student details","Please wait");
        final String studName = student_usn;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxIVco5JVe3XJhe0JNEl1zPSCJO-2FFYT6YE1FW8d58VSUUCV8/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        loading.dismiss();
                        student_name = response.substring( 0, response.indexOf(","));
                        response = response.substring(response.indexOf(",")+1, response.length());
                        student_sem = response.substring( 0, response.indexOf(","));
                        response = response.substring(response.indexOf(",")+1, response.length());
                        student_branch = response.substring( 0, response.indexOf(","));
                        response = response.substring(response.indexOf(",")+1, response.length());
                        student_section = response;

                        sharedPreferences.edit().putString("Student name", student_name).apply();
                        sharedPreferences.edit().putString("Student sem", student_sem).apply();
                        sharedPreferences.edit().putString("Student branch", student_branch).apply();
                        sharedPreferences.edit().putString("Student section", student_section).apply();
                        sharedPreferences.edit().putString("Student usn", student_usn).apply();
                        sharedPreferences.edit().putString("User details", "1").apply();
                        name.setText(student_name);
                        sem.setText(student_sem);
                        branch.setText(student_branch);
                        usn.setText(student_usn);
                        section.setText(student_section);
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

                parmas.put("action","getDetails");
                parmas.put("usn",studName);

                return parmas;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void getItems() {

        loading =  ProgressDialog.show(this,"Loading","please wait",false,true);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycbxIVco5JVe3XJhe0JNEl1zPSCJO-2FFYT6YE1FW8d58VSUUCV8/exec?action=getStudents",
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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResposnce) {
        String names = "";
        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("students");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String studName = jo.getString("studName");
                names += studName + " ";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loading.dismiss();
    }

}
