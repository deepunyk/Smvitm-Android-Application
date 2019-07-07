package com.xoi.smvitm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    SharedPreferences sharedPreferences;
    CardView timetable_home, event_home, circular_home, more_home;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        try {
            img_urls = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Main carousel", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {

        }
        carouselView = view.findViewById(R.id.carouselView);
        carouselView.setPageCount(img_urls.size());
        carouselView.setImageListener(imageListener);

        return view;
    }

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            Glide.with(getContext()).load(img_urls.get(position)).into(imageView);
        }
    };


}
