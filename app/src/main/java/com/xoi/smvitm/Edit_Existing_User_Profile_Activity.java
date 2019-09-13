package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.dd.processbutton.iml.ActionProcessButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.goodiebag.carouselpicker.CarouselPicker;

public class Edit_Existing_User_Profile_Activity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String student_branch, student_email, student_usn, student_semester, student_section, student_name;
    int student_dp, image_pos;
    EditText name;
    TextView branch, usn, change_dp_txt, usr_dp_txt;
    MaterialSpinner section, semester;
    ActionProcessButton submit;
    String[] section_list = {"A" , "B" , "C"};
    String[] semester_list = {"1" , "3" , "5" , "7"};
    int[] profile_pic_loc = {R.drawable.ic_usrpr1,R.drawable.ic_usrpr2,R.drawable.ic_usrpr3,R.drawable.ic_usrpr4,R.drawable.ic_usrpr5,R.drawable.ic_usrpr6,R.drawable.ic_usrpr7,R.drawable.ic_usrpr8,R.drawable.ic_usrpr9,R.drawable.ic_usrpr10};
    int section_index, semester_index;
    ImageView user_dp;
    Button back;
    CarouselPicker carouselPicker;
    List<CarouselPicker.PickerItem> imageItems = new ArrayList<>();
    Button user_dp_select_but;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit__existing__user__profile);

        name = (EditText) findViewById(R.id.name);
        usn = (TextView) findViewById(R.id.usn);
        branch = (TextView) findViewById(R.id.branch);
        section = (MaterialSpinner) findViewById(R.id.section);
        semester = (MaterialSpinner) findViewById(R.id.semester);
        submit = (ActionProcessButton) findViewById(R.id.submit);
        back = (Button)findViewById(R.id.back);
        user_dp = (ImageView)findViewById(R.id.user_profile_pic);
        carouselPicker = (CarouselPicker) findViewById(R.id.dp_select);
        change_dp_txt = (TextView)findViewById(R.id.change_dp_txt);
        user_dp_select_but = (Button)findViewById(R.id.user_dp_select_but);
        usr_dp_txt = (TextView) findViewById(R.id.usr_dp_txt);
        submit.setMode(ActionProcessButton.Mode.ENDLESS);

        section.setItems(section_list);
        semester.setItems(semester_list);

        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        student_usn = sharedPreferences.getString("Student usn", "");
        student_email = sharedPreferences.getString("Student Email", "");
        student_branch = sharedPreferences.getString("Student branch", "");
        student_semester = sharedPreferences.getString("Student sem", "");
        student_section = sharedPreferences.getString("Student section", "");
        student_name = sharedPreferences.getString("Student name", "");
        student_dp = sharedPreferences.getInt("Student dp", 0);

        section_index = getListIndex(section_list, student_section);
        semester_index = getListIndex(semester_list,student_semester);

        name.setText(student_name);
        branch.setText(student_branch);
        usn.setText(student_usn);
        semester.setSelectedIndex(semester_index);
        section.setSelectedIndex(section_index);
        user_dp.setImageResource(profile_pic_loc[student_dp]);

        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.ic_usrpr1));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.ic_usrpr2));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.ic_usrpr3));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.ic_usrpr4));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.ic_usrpr5));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.ic_usrpr6));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.ic_usrpr7));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.ic_usrpr8));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.ic_usrpr9));
        imageItems.add(new CarouselPicker.DrawableItem(R.drawable.ic_usrpr10));

        CarouselPicker.CarouselViewAdapter imageAdapter = new CarouselPicker.CarouselViewAdapter(this, imageItems, 0);
        carouselPicker.setAdapter(imageAdapter);

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
                    Toast.makeText(Edit_Existing_User_Profile_Activity.this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(Edit_Existing_User_Profile_Activity.this, User_profile_Activity.class);
                startActivity(go);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
            }
        });

        change_dp_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carouselPicker.setVisibility(View.VISIBLE);
                user_dp_select_but.setVisibility(View.VISIBLE);
                usr_dp_txt.setVisibility(View.VISIBLE);
                submit.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
                carouselPicker.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        image_pos = position;
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
            }
        });

        user_dp_select_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_dp_select_but.setVisibility(View.GONE);
                sharedPreferences.edit().putInt("Student dp", image_pos).apply();
                carouselPicker.setVisibility(View.GONE);
                submit.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
                usr_dp_txt.setVisibility(View.GONE);
                user_dp.setImageResource(profile_pic_loc[image_pos]);
                Intent intent = getIntent();
                finish();
                startActivity(intent);
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
                            Intent go = new Intent(Edit_Existing_User_Profile_Activity.this, User_profile_Activity.class);
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

                parmas.put("action", "editStudentDetails");
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

    private int getListIndex(String[] list, String item){

        int i;
        for(i = 0; i < list.length; i++){
            if(list[i].equals(item)){
                break;
            }
        }
        return i;
    }
    public void onBackPressed() {
        Intent go = new Intent(Edit_Existing_User_Profile_Activity.this, User_profile_Activity.class);
        this.startActivity(go);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }

}
