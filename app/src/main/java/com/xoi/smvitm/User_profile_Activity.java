package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.github.lzyzsd.circleprogress.ArcProgress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User_profile_Activity extends AppCompatActivity {

    TextView name, branch, sem, section,usn;
    Button  btnEdit;
    ProgressDialog loading;
    Toolbar toolbar;
    String student_name, student_usn, student_branch, student_sem, student_section;
    SharedPreferences sharedPreferences;
    int[] profile_pic_loc = {R.drawable.ic_usrpr1,R.drawable.ic_usrpr2,R.drawable.ic_usrpr3,R.drawable.ic_usrpr4,R.drawable.ic_usrpr5,R.drawable.ic_usrpr6,R.drawable.ic_usrpr7,R.drawable.ic_usrpr8,R.drawable.ic_usrpr9,R.drawable.ic_usrpr10};
    int student_dp;
    ImageView dp_img;

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
        dp_img = (ImageView)findViewById(R.id.Fphoto);

        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        student_usn = sharedPreferences.getString("stud_usn", "");

        String detail_check = sharedPreferences.getString("User details", "");
        if(detail_check.equals("1")){

            student_section = sharedPreferences.getString("Student section", "");
            student_branch = sharedPreferences.getString("Student branch", "");
            student_sem = sharedPreferences.getString("Student sem", "");
            student_name = sharedPreferences.getString("Student name", "");
            student_usn = sharedPreferences.getString("Student usn", "");
            student_dp = sharedPreferences.getInt("Student dp", 0);

            dp_img.setImageResource(profile_pic_loc[student_dp]);
            usn.setText(student_usn);
            sem.setText(student_sem);
            section.setText(student_section);
            branch.setText(student_branch);
            name.setText(student_name);
        }
        else {
            refreshUserInfo();
        }

        btnEdit = (Button)findViewById(R.id.btnEdit);


        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_icon));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent go = new Intent(User_profile_Activity.this, MainActivity.class);
                    startActivity(go);
                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                }
                catch (Exception e){
                    Toast.makeText(User_profile_Activity.this, ""+e, Toast.LENGTH_SHORT).show();
                }
            }
        });
        toolbar.setTitle("User Profile");

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(User_profile_Activity.this, Edit_Existing_User_Profile_Activity.class);
                startActivity(go);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
            }
        });

    }

    private void  refreshUserInfo() {

        loading = ProgressDialog.show(this,"Fetching student details","Please wait");
        final String studName = student_usn;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxIVco5JVe3XJhe0JNEl1zPSCJO-2FFYT6YE1FW8d58VSUUCV8/exec",
        new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("No user found")){
                            Intent go = new Intent(User_profile_Activity.this, Edit_New_User_Profile_Activity.class);
                            startActivity(go);
                            finish();
                            overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
                        }
                        else {
                            student_name = response.substring(0, response.indexOf(","));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            student_sem = response.substring(0, response.indexOf(","));
                            response = response.substring(response.indexOf(",") + 1, response.length());
                            student_branch = response.substring(0, response.indexOf(","));
                            response = response.substring(response.indexOf(",") + 1, response.length());
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

    @Override
    public void onBackPressed() {
        Intent go = new Intent(User_profile_Activity.this, MainActivity.class);
        this.startActivity(go);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.stay);
    }

    public static class attendance_RV_Adapter extends RecyclerView.Adapter<attendance_RV_Adapter.ViewHolder> {

        ArrayList<String> lsub = new ArrayList<>();
        ArrayList<String> rsub = new ArrayList<>();
        ArrayList<String> lca = new ArrayList<>();
        ArrayList<String> rca = new ArrayList<>();
        ArrayList<String> lct = new ArrayList<>();
        ArrayList<String> rct = new ArrayList<>();
        private Context mContext;

        public attendance_RV_Adapter(ArrayList<String> lsub, ArrayList<String> rsub, ArrayList<String> lca, ArrayList<String> rca, ArrayList<String> lct, ArrayList<String> rct, Context mContext) {
            this.lsub = lsub;
            this.rsub = rsub;
            this.lca = lca;
            this.rca = rca;
            this.lct = lct;
            this.rct = rct;
            this.mContext = mContext;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attendance_layout_list, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }


        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

            float lp, rp;
            float lcla = Integer.parseInt(lca.get(i)), rcla = Integer.parseInt(rca.get(i)), lclt = Integer.parseInt(lct.get(i)), rclt = Integer.parseInt(rct.get(i));
            lp = (lcla/lclt)*100;
            rp = (rcla/rclt)*100;
            viewHolder.sub1.setProgress((int)lp);
            viewHolder.sub2.setProgress((int)rp);
            viewHolder.sub1.setBottomText(lca.get(i) + "/" + lct.get(i));
            viewHolder.sub2.setBottomText(rca.get(i) + "/" + rct.get(i));
            viewHolder.sub1_txt.setText(lsub.get(i));
            viewHolder.sub2_txt.setText(rsub.get(i));
            if(lp < 85){
                viewHolder.sub1.setFinishedStrokeColor(viewHolder.red);
                viewHolder.sub1.setTextColor(viewHolder.red);
                viewHolder.sub1_txt.setTextColor(mContext.getResources().getColor(R.color.red));
            }
            else{
                viewHolder.sub1.setFinishedStrokeColor(viewHolder.green);
                viewHolder.sub1.setTextColor(viewHolder.green);
                viewHolder.sub1_txt.setTextColor(mContext.getResources().getColor(R.color.green));
            }
            if(rp < 85){
                viewHolder.sub2.setTextColor(viewHolder.red);
                viewHolder.sub2.setFinishedStrokeColor(viewHolder.red);
                viewHolder.sub2_txt.setTextColor(mContext.getResources().getColor(R.color.red));
            }
            else {
                viewHolder.sub2.setTextColor(viewHolder.green);
                viewHolder.sub2.setFinishedStrokeColor(viewHolder.green);
                viewHolder.sub2_txt.setTextColor(mContext.getResources().getColor(R.color.green));
            }
        }

        @Override
        public int getItemCount() {
            return lsub.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            ArcProgress sub1, sub2;
            TextView sub1_txt, sub2_txt;
            int red, green;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                sub1 = itemView.findViewById(R.id.sub1);
                sub2 = itemView.findViewById(R.id.sub2);
                sub1_txt = itemView.findViewById(R.id.sub1_txt);
                sub2_txt = itemView.findViewById(R.id.sub2_txt);
                green = sub1.getFinishedStrokeColor();
                red = sub2.getFinishedStrokeColor();
            }
        }
    }
}
