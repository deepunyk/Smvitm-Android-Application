package com.xoi.smvitm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class splash_Activiy extends AppCompatActivity {
    String login;
    ImageView img;
    TextView txt;
    Boolean internet;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        img = (ImageView) findViewById(R.id.img);
        txt = (TextView) findViewById(R.id.txt);

        internet = checkInternetConnection();
        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);

        final AnimationDrawable animation = new AnimationDrawable();
        animation.addFrame(getResources().getDrawable(R.drawable.light_off), 500);
        animation.addFrame(getResources().getDrawable(R.drawable.light_on), 100);
        animation.setOneShot(false);
        img.setImageDrawable(animation);
        animation.start();

        if (internet) {
            login = sharedPreferences.getString("login_Activity", "");
            sharedPreferences.edit().putString("Internet Connection", "Yes").apply();
            txt.setText("Welcome");
            animation.stop();
            img.setImageResource(R.drawable.light_bulb);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
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
                }
            }, 1500);
        } else {
            new AlertDialog.Builder(splash_Activiy.this)
                    .setTitle("No internet connection")
                    .setMessage("Do you want to go to offline mode?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            sharedPreferences.edit().putString("Internet Connection", "No").apply();
                            Toast.makeText(splash_Activiy.this, "Okie", Toast.LENGTH_SHORT).show();
                        }
                    })
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
}
