package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import at.markushi.ui.CircleButton;

public class Timetable_Recycler_ViewAdapter extends RecyclerView.Adapter<Timetable_Recycler_ViewAdapter.ViewHolder> {

    private ArrayList<String> classes = new ArrayList<String>();
    private ArrayList<String> timings = new ArrayList<String>();
    private Context mContext;

    public Timetable_Recycler_ViewAdapter(ArrayList<String> classes, ArrayList<String> timings, Context mContext) {
        this.classes = classes;
        this.timings = timings;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.timetable_layout_list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.class_name.setText(classes.get(i));
        viewHolder.time.setText(timings.get(i));

        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView class_name, time;
        ConstraintLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            class_name = itemView.findViewById(R.id.class_name);
            time = itemView.findViewById(R.id.time);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}