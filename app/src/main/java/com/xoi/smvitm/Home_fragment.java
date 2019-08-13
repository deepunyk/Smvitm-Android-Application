package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Home_fragment extends Fragment {
    View view;
    CarouselView carouselView;
    int[] sampleImages = {R.drawable.college_pic, R.drawable.main_bk, R.drawable.college_pic, R.drawable.main_bk};
    private ArrayList<String> img_urls = new ArrayList<>();
    private ArrayList<String> car_head = new ArrayList<>();
    private ArrayList<String> car_sub = new ArrayList<>();
    SharedPreferences sharedPreferences;
    CardView timetable_home, event_home, circular_home, attendance_home;
    TextView carHead,carSub;
    Toolbar toolbar;
    String profile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        try {
            img_urls = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Main carousel link", ObjectSerializer.serialize(new ArrayList<String>())));
            car_head = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Main carousel head", ObjectSerializer.serialize(new ArrayList<String>())));
            car_sub = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Main carousel sub", ObjectSerializer.serialize(new ArrayList<String>())));

            toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            carHead = (TextView)view.findViewById(R.id.carHead);
            carSub = (TextView)view.findViewById(R.id.carSub);
            carouselView = view.findViewById(R.id.carouselView);
            carouselView.setPageCount(img_urls.size());
            carouselView.setImageListener(imageListener);
            carHead.setText(car_head.get(0));
            carSub.setText(car_sub.get(0));
        } catch (Exception e) {
            Intent i = new Intent(getActivity(),splash_Activiy.class);
            startActivity(i);
            getActivity().finish();
        }

        profile = sharedPreferences.getString("login_Activity","");
        carouselView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                carHead.setAlpha(1-v);
                carSub.setAlpha(1-v);
            }

            @Override
            public void onPageSelected(int i) {
                carHead.setText(car_head.get(i));
                carSub.setText(car_sub.get(i));
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        timetable_home = (CardView)view.findViewById(R.id.timetable_home);
        circular_home = (CardView)view.findViewById(R.id.circular_home);
        event_home = (CardView)view.findViewById(R.id.event_home);
        attendance_home = (CardView)view.findViewById(R.id.attendance_home);

        timetable_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Timetable_fragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                MainActivity.navigationView.setCheckedItem(R.id.timetable_nav);
                toolbar.setTitle("Timetable");
            }
        });
        circular_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Circular_fragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                MainActivity.navigationView.setCheckedItem(R.id.circular_nav);
                toolbar.setTitle("Circulars");
            }
        });
        event_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Event_fragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                MainActivity.navigationView.setCheckedItem(R.id.events_nav);
                toolbar.setTitle("Events");
            }
        });
        attendance_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profile.equals("1")) {
                    Fragment fragment = new Attendance_fragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.contentContainer, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    MainActivity.navigationView.setCheckedItem(R.id.calendar_nav);
                    toolbar.setTitle("Academic Calendar");
                }
                else{
                    Toast.makeText(getActivity(), "This feature is currently not available for faculties", Toast.LENGTH_SHORT).show();
                }

            }
        });
        return view;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            try {
                Glide.with(getContext()).load(img_urls.get(position)).placeholder(R.drawable.loading_carousel).into(imageView);
            }
            catch (Exception e){
                Intent i = new Intent(getActivity(),splash_Activiy.class);
                startActivity(i);
                getActivity().finish();
            }
        }
    };



}
