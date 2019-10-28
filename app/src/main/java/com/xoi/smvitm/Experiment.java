package com.xoi.smvitm;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import java.util.HashMap;
import java.util.Map;

public class Experiment extends AppCompatActivity {

    Button send_but;
    EditText usn_txt,class_txt;
    String usn,st_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        send_but = (Button) findViewById(R.id.send_but);
        usn_txt = (EditText)findViewById(R.id.usn_txt);
        class_txt = (EditText)findViewById(R.id.class_txt);

        send_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usn = usn_txt.getText().toString();
                st_class = class_txt.getText().toString();
                addUser();
                usn_txt.setText("");
                class_txt.setText("");
            }
        });
    }

    private void addUser(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxmVUYyvp573OyFZKvc6LtYCQRXtkOIgJPZuH8W65rTGHYFenhH/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Success")) {
                            Toast.makeText(Experiment.this, "Done", Toast.LENGTH_SHORT).show();
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(usn_txt.getWindowToken(), 0);
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

                parmas.put("action", "addUser");
                parmas.put("usn", usn);
                parmas.put("class", st_class);
                return parmas;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}
