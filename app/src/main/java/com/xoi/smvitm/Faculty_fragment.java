package com.xoi.smvitm;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class Faculty_fragment extends Fragment {

    View view;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> designations = new ArrayList<>();
    private ArrayList<String> branch = new ArrayList<>();
    private ArrayList<String> mobile = new ArrayList<>();
    private ArrayList<String> email = new ArrayList<>();
    private ArrayList<String> photolink = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_faculty,container,false);

        return view;
    }
}
