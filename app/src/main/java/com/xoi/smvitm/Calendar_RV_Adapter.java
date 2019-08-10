package com.xoi.smvitm;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Calendar_RV_Adapter extends RecyclerView.Adapter<Calendar_RV_Adapter.ViewHolder>{

    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();
    private ArrayList<String> day = new ArrayList<String>();
    private ArrayList<String> type = new ArrayList<String>();
    private Context mContext;

    public Calendar_RV_Adapter(ArrayList<String> name, ArrayList<String> date, ArrayList<String> day, ArrayList<String> type, Context mContext) {
        this.name = name;
        this.date = date;
        this.day = day;
        this.type = type;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public Calendar_RV_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.calendar_layout_list, viewGroup, false);
        Calendar_RV_Adapter.ViewHolder holder = new Calendar_RV_Adapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Calendar_RV_Adapter.ViewHolder viewHolder, final int i) {
        viewHolder.name_txt.setText(name.get(i));
        viewHolder.day_txt.setText(day.get(i));
        viewHolder.date_txt.setText(date.get(i));
        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "" + type.get(i), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name_txt, date_txt, day_txt;
        ConstraintLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.name);
            date_txt = itemView.findViewById(R.id.date_txt);
            day_txt = itemView.findViewById(R.id.day_txt);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
