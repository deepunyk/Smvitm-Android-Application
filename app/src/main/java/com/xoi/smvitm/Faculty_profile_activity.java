package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class Faculty_profile_activity extends AppCompatActivity {

    TextView name, branch, info, desig, email;
    ImageView photo;
    SharedPreferences sharedPreferences;
    String fname, fbranch, finfo, fphoto, fdesig, femail;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_profile_activity);
        name = (TextView)findViewById(R.id.Fname);
        branch = (TextView)findViewById(R.id.Fbranch);
        desig = (TextView)findViewById(R.id.Fdesig);
        email = (TextView)findViewById(R.id.Femail);
        info = (TextView)findViewById(R.id.Finfo);
        photo = (ImageView) findViewById(R.id.Fphoto);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_icon));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent go = new Intent(Faculty_profile_activity.this, MainActivity.class);
                    startActivity(go);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                }
                catch (Exception e){
                    Toast.makeText(Faculty_profile_activity.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        toolbar.setTitle("Faculty Profile");

        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);

        fname = sharedPreferences.getString("Faculty name","");
        fbranch = sharedPreferences.getString("Faculty branch","");
        fdesig = sharedPreferences.getString("Faculty desig","");
        femail = sharedPreferences.getString("Faculty email","");
        fphoto = sharedPreferences.getString("Faculty photo","");
        finfo = sharedPreferences.getString("Faculty info","");

        name.setText(fname);
        desig.setText(fdesig);
        branch.setText(fbranch);
        email.setText(femail);
        info.setText(finfo);
        Glide.with(Faculty_profile_activity.this).load(fphoto).placeholder(R.drawable.user_profile_icon).error(R.drawable.college_logo).into(photo);


    }

    public void onBackPressed() {
        Intent go = new Intent(Faculty_profile_activity.this, MainActivity.class);
        this.startActivity(go);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }
}
