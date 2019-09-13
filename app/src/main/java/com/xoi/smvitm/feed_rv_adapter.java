package com.xoi.smvitm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class feed_rv_adapter extends RecyclerView.Adapter<feed_rv_adapter.ViewHolder>{

    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<String> date = new ArrayList<String>();
    private ArrayList<String> desc = new ArrayList<String>();
    private ArrayList<String> usn = new ArrayList<String>();
    private Context mContext;

    public feed_rv_adapter(ArrayList<String> name, ArrayList<String> date, ArrayList<String> desc, ArrayList<String> usn, Context mContext) {
        this.name = name;
        this.date = date;
        this.desc = desc;
        this.usn = usn;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public feed_rv_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_layout_list, viewGroup, false);
        feed_rv_adapter.ViewHolder holder = new feed_rv_adapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final feed_rv_adapter.ViewHolder viewHolder, final int i) {
        viewHolder.name_txt.setText(name.get(i));
        viewHolder.desc_txt.setText(desc.get(i));
        viewHolder.usn_txt.setText(usn.get(i));
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name_txt, date_txt, usn_txt, desc_txt;
        ConstraintLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.name_txt);
            date_txt = itemView.findViewById(R.id.date_txt);
            usn_txt = itemView.findViewById(R.id.usn_txt);
            desc_txt = itemView.findViewById(R.id.desc_txt);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
