package com.xoi.smvitm;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.webianks.library.scroll_choice.ScrollChoice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class qp_select_fragment extends Fragment {

    View view;
    ScrollChoice yearScroll, branchScroll;

    ConstraintLayout yearDetails_layout,branchDetails_layout;
    Button submit_but_year, submit_but_branch;
    String[] semList = {"1","2","3","4","5","6","7","8"};
    String[] branchList = {"CSE","ECE","MECH","CIV"};
    List<String> sem_data = new ArrayList<>();
    List<String> branch_data = new ArrayList<>();
    String selectedClass;
    SharedPreferences sp;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_qp_select, container, false);

        yearScroll = (ScrollChoice) view.findViewById(R.id.scroll_choice);
        branchScroll = (ScrollChoice) view.findViewById(R.id.scroll_choice_branch);
        submit_but_year = (Button)view.findViewById(R.id.submit_but);
        submit_but_branch = (Button)view.findViewById(R.id.submit_but_branch);
        yearDetails_layout = (ConstraintLayout)view.findViewById(R.id.yearDetails_layout);
        branchDetails_layout = (ConstraintLayout)view.findViewById(R.id.branchDetails_layout);

        sem_data = Arrays.asList(semList);
        branch_data = Arrays.asList(branchList);

        yearScroll.addItems(sem_data,3);
        branchScroll.addItems(branch_data,1);

        sp = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);

        submit_but_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedClass = yearScroll.getCurrentSelection();
                yearDetails_layout.setVisibility(View.GONE);
                branchDetails_layout.setVisibility(View.VISIBLE);
            }
        });

        submit_but_branch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedClass = branchScroll.getCurrentSelection() + "" + selectedClass;
                Toast.makeText(getActivity(), "Collecting " + selectedClass +" question papers, please wait", Toast.LENGTH_LONG).show();
                sp.edit().putString("tempClass",selectedClass).apply();
                Fragment fragment = new DisplayQP_fragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.contentContainer, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                MainActivity.navigationView.setCheckedItem(R.id.home_nav);
            }
        });
        return view;
    }



}
