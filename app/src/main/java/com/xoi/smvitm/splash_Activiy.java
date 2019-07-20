package com.xoi.smvitm;

import android.app.SharedElementCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class splash_Activiy extends AppCompatActivity {
    String login;
    Boolean internet;
    SharedPreferences sharedPreferences;
    ImageView img, bk_img;
    private ArrayList<String> img_urls = new ArrayList<>();
    private ArrayList<String> car_head = new ArrayList<>();
    private ArrayList<String> car_sub = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        internet = checkInternetConnection();
        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        img = (ImageView)findViewById(R.id.splash_img);
        bk_img = (ImageView)findViewById(R.id.img);

        img.animate().alpha(1).setDuration(2000);
        bk_img.animate().alpha(1).setDuration(500);


        if (internet) {
            sharedPreferences.edit().putString("Internet Connection", "Yes").apply();
            getImages();
        } else {
            new AlertDialog.Builder(splash_Activiy.this)
                    .setTitle("No internet connection")
                    .setMessage("This app requires internet connection. Please switch on your internet and try again.")
                    .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    })
                    .setIcon(getResources().getDrawable(R.drawable.nointernet_icon))
                    .show();
        }

    }

    private boolean checkInternetConnection() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        } else
            return false;
    }

    private void getImages() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://script.google.com/macros/s/AKfycby43WKcwcdUyDKlYKf3z5I6-5lzeEak5Pa46UBXRK3qRIHm7W0/exec?action=getImages",
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
        RequestQueue queue = Volley.newRequestQueue(splash_Activiy.this);
        queue.add(stringRequest);
    }

    private void parseItems(String jsonResposnce) {
        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("images");

            for (int i = 0; i < jarray.length(); i++) {
                JSONObject jo = jarray.getJSONObject(i);
                String link_json = jo.getString("link");
                img_urls.add(link_json);
                String head_json = jo.getString("head");
                car_head.add(head_json);
                String sub_json = jo.getString("sub");
                car_sub.add(sub_json);
            }
            try {
                sharedPreferences.edit().putString("Main carousel link", ObjectSerializer.serialize(img_urls)).apply();
                sharedPreferences.edit().putString("Main carousel head", ObjectSerializer.serialize(car_head)).apply();
                sharedPreferences.edit().putString("Main carousel sub", ObjectSerializer.serialize(car_sub)).apply();
            }
            catch (Exception e) {
            }
            if (!sharedPreferences.contains("login_Activity")) {
                Intent go = new Intent(splash_Activiy.this, login_Activity.class);
                startActivity(go);
                finish();
                overridePendingTransition(R.anim.push_up_in, R.anim.stay);
            } else {
                Intent go = new Intent(splash_Activiy.this, MainActivity.class);
                startActivity(go);
                finish();
                overridePendingTransition(R.anim.push_up_in, R.anim.stay);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
