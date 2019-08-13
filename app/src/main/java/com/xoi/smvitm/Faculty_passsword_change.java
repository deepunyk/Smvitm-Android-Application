package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

public class Faculty_passsword_change extends AppCompatActivity {


    EditText newpass, confirmpass;
    ActionProcessButton pass_but;
    String newstr, constr;
    String femail;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_passsword_change);

        newpass = (EditText)findViewById(R.id.pass_edit);
        confirmpass = (EditText)findViewById(R.id.confirm_pass);
        pass_but = (ActionProcessButton)findViewById(R.id.change_pass);

        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        femail = sharedPreferences.getString("Faculty email","");

        pass_but.setMode(ActionProcessButton.Mode.ENDLESS);

        pass_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newstr = newpass.getText().toString();
                constr = confirmpass.getText().toString();
                if(newstr.equals(constr)){
                    if(newstr.length() < 5){
                        Toast.makeText(Faculty_passsword_change.this, "Password too short. It should contain atleast 5 characters", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        try {
                            changePassword();
                            pass_but.setProgress(1);
                        }
                        catch (Exception e){
                            pass_but.setProgress(1);
                        }
                    }
                }else{
                    Toast.makeText(Faculty_passsword_change.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changePassword(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxm3DdKNuviEY4r2HO4iEnPdssmYLdgFaqreP06JQ0AFEjHNQ/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Success")) {
                            pass_but.setProgress(100);
                            pass_but.setText("Success");
                            Intent go = new Intent(Faculty_passsword_change.this, Faculty_profile_activity.class);
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

                parmas.put("action", "changePassword");
                parmas.put("password", newstr);
                parmas.put("email", femail);
                return parmas;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    public void onBackPressed() {
        Intent go = new Intent(Faculty_passsword_change.this, Faculty_profile_activity.class);
        this.startActivity(go);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }
}
