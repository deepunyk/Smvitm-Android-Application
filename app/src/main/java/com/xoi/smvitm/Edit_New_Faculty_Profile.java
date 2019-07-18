package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Edit_New_Faculty_Profile extends AppCompatActivity {

    TextView Nbranch;
    EditText Nname;
    Button submit;
    SharedPreferences sharedPreferences;
    String name, brnch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__new__faculty__profile);
        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        Nbranch = (TextView)findViewById(R.id.Fbranch);
        Nname = (EditText)findViewById(R.id.Fname);
        submit = (Button)findViewById(R.id.submit);

        brnch = sharedPreferences.getString("Faculty branch","");
        Nbranch.setText(brnch);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = Nname.getText().toString();
                sharedPreferences.edit().putString("Faculty name",name).apply();
                Intent go = new Intent(Edit_New_Faculty_Profile.this, Faculty_profile_activity.class);
                startActivity(go);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
            }
        });

    }
}
