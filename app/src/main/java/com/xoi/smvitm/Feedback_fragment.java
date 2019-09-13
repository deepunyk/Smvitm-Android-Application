package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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

import java.util.HashMap;
import java.util.Map;

public class Feedback_fragment extends Fragment {

    View view;
    EditText desc_txt;
    ActionProcessButton submit_but;
    String desc, version, device, model, email;
    SharedPreferences sharedPreferences;
    LottieAnimationView animationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_feedback, container, false);

        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);

        desc_txt = (EditText) view.findViewById(R.id.desc_txt);
        submit_but = (ActionProcessButton) view.findViewById(R.id.submit);
        animationView = (LottieAnimationView)view.findViewById(R.id.animation_view);

        submit_but.setMode(ActionProcessButton.Mode.ENDLESS);

        submit_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit_but.setProgress(1);
                submit_but.setText("Submitting your response");
                desc = desc_txt.getText().toString();
                version = Build.VERSION.RELEASE;
                device = Build.MANUFACTURER;
                model = Build.MODEL;
                if(sharedPreferences.contains("Student Email")) {
                    email = sharedPreferences.getString("Student Email", "");
                }
                else{
                    email = sharedPreferences.getString("Faculty email", "");
                }
                putFeedback();
            }
        });

        return view;
    }

    private void putFeedback(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwoo8EiFlBxI5dzlZh1Vcg8SLkMS6b6sAXc0kHIM7s0ufet9rc/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Success")) {
                            submit_but.setProgress(100);
                            submit_but.setText("Success");
                            Toast.makeText(getActivity(), "Thank you for your response", Toast.LENGTH_SHORT).show();
                            animationView.setAnimation("done.json");
                            animationView.playAnimation();
                            animationView.loop(false);
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

                parmas.put("action", "addFeedback");
                parmas.put("email", email);
                parmas.put("version", version);
                parmas.put("desc", desc);
                parmas.put("device", device);
                parmas.put("model", model);
                return parmas;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }
}
