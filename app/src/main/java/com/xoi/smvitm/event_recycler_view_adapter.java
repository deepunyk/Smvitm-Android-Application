package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class event_recycler_view_adapter extends RecyclerView.Adapter<event_recycler_view_adapter.ViewHolder>{

    private ArrayList<String> titles = new ArrayList<String>();
    private ArrayList<String> descriptions = new ArrayList<String>();
    private ArrayList<String> imgLinks = new ArrayList<String>();
    private ArrayList<String> conducts = new ArrayList<String>();
    private ArrayList<String> details = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();
    private ArrayList<String> brochures = new ArrayList<String>();
    private Context mContext;

    public event_recycler_view_adapter(ArrayList<String> titles, ArrayList<String> descriptions, ArrayList<String> imgLinks, ArrayList<String> conducts, ArrayList<String> details, ArrayList<String> dates, ArrayList<String> brochures, Context mContext) {
        this.titles = titles;
        this.descriptions = descriptions;
        this.imgLinks = imgLinks;
        this.conducts = conducts;
        this.details = details;
        this.dates = dates;
        this.brochures = brochures;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public event_recycler_view_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.events_layout_list_item, viewGroup, false);
        event_recycler_view_adapter.ViewHolder holder = new event_recycler_view_adapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final event_recycler_view_adapter.ViewHolder viewHolder, final int i) {
        viewHolder.description.setText(descriptions.get(i));
        viewHolder.title.setText(titles.get(i));
        Glide.with(mContext).load(imgLinks.get(i)).into(viewHolder.img);
        viewHolder.details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(mContext, Event_popup_activity.class);
                go.putExtra("description", descriptions.get(i).toString());
                go.putExtra("title", titles.get(i).toString());
                go.putExtra("conduct", conducts.get(i).toString());
                go.putExtra("detail", details.get(i).toString());
                go.putExtra("date", dates.get(i).toString());
                go.putExtra("brochure", brochures.get(i).toString());
                go.putExtra("image", imgLinks.get(i).toString());
                mContext.startActivity(go);
            }
        });
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView description, title;
        ImageView img;
        ConstraintLayout parent_layout;
        Button details_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.img);
            details_btn = itemView.findViewById(R.id.details_btn);
        }
    }
}
