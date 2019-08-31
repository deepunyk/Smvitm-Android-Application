package com.xoi.smvitm;

import android.Manifest;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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

import pub.devrel.easypermissions.EasyPermissions;


public class splash_Activiy extends AppCompatActivity {
    String login;
    Boolean internet;
    SharedPreferences sharedPreferences;
    ImageView img, bk_img;
    private ArrayList<String> img_urls = new ArrayList<>();
    private ArrayList<String> car_head = new ArrayList<>();
    private ArrayList<String> car_sub = new ArrayList<>();
    private int STORAGE_PERMISSION_CODE = 23;
    ConstraintLayout parent_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        internet = checkInternetConnection();
        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        img = (ImageView)findViewById(R.id.splash_img);
        bk_img = (ImageView)findViewById(R.id.img);

        parent_layout = (ConstraintLayout)findViewById(R.id.splash_parent_layout);
        img.animate().alpha(1).setDuration(2000);
        bk_img.animate().alpha(1).setDuration(500);

        if(isReadStorageAllowed()){
            if (internet) {
                sharedPreferences.edit().putString("Internet Connection", "Yes").apply();
                getImages();

            } else {
                new AlertDialog.Builder(splash_Activiy.this)
                        .setTitle("No internet connection")
                        .setMessage("You are not connected to internet. Do you want to go offline mode?")
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                System.exit(0);
                            }
                        })
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (sharedPreferences.contains("login_Activity")) {
                                    if(sharedPreferences.contains("Table download")) {
                                        Intent go = new Intent(splash_Activiy.this, Timetable_offline_activity.class);
                                        startActivity(go);
                                        finish();
                                    }
                                    else{
                                        Snackbar snackbar = Snackbar
                                                .make(parent_layout, "Go to the timetable option using internet once to use the app in offline mode.", Snackbar.LENGTH_LONG)
                                                .setAction("QUIT", new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        finish();
                                                        System.exit(0);
                                                    }
                                                });
                                        snackbar.show();
                                    }
                                }
                                else{
                                    Toast.makeText(splash_Activiy.this, "Please login using internet connection to use the app in offline mode.", Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setIcon(getResources().getDrawable(R.drawable.nointernet_icon))
                        .show();
            }
        }
        else {
            requestStoragePermission();
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

    //Requesting permission
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

        }

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE){

            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
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
            }else{
                Toast.makeText(this,"Oops you just denied the permission, for the app to run please allow the app to access the storage",Toast.LENGTH_LONG).show();
            }
        }
    }
    private boolean isReadStorageAllowed() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        return false;
    }

}
