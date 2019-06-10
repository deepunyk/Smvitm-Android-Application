package com.xoi.smvitm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Home3_fragment extends Fragment {

    private ArrayList<String> descriptions = new ArrayList<>();
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_3,container,false);
        initImageBitmaps();
        return view;
    }
    private void initImageBitmaps(){

        descriptions.add("Havasu Falls");

        descriptions.add("Trondheim");

        descriptions.add("Portugal");

        descriptions.add("Rocky Mountain National Park");

        descriptions.add("Mahahual");

        descriptions.add("Frozen Lake");

        descriptions.add("White Sands Desert");

        descriptions.add("Austrailia");

        descriptions.add("Washington");

        initRecyclerView();
    }

    private void initRecyclerView(){
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(descriptions,getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
