package com.xoi.smvitm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class exam_rv_adapter extends RecyclerView.Adapter<exam_rv_adapter.ViewHolder>{

    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> portion = new ArrayList<>();
    private Context mContext;

    public exam_rv_adapter(ArrayList<String> date, ArrayList<String> time, ArrayList<String> name, ArrayList<String> portion, Context mContext) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.portion = portion;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public exam_rv_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exam_layout_list, viewGroup, false);
        exam_rv_adapter.ViewHolder holder = new exam_rv_adapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final exam_rv_adapter.ViewHolder viewHolder, final int i) {

        viewHolder.name_txt.setText(name.get(i));
        viewHolder.time_txt.setText(time.get(i));
        viewHolder.date_txt.setText(date.get(i));
        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        if(portion.get(i).equals("-")){
            viewHolder.portion_def.setVisibility(View.GONE);
            viewHolder.portion.setVisibility(View.GONE);
            viewHolder.underline.setVisibility(View.GONE);
        }
        else{
            viewHolder.portion.setText(portion.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return name.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date_txt, time_txt,name_txt, portion_def;
        ConstraintLayout parent_layout;
        ExpandableTextView portion;
        View underline;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date_txt = itemView.findViewById(R.id.date_txt);
            time_txt = itemView.findViewById(R.id.time_txt);
            name_txt = itemView.findViewById(R.id.exam_name_txt);
            portion = itemView.findViewById(R.id.portion);
            portion_def = itemView.findViewById(R.id.textView38);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            underline = itemView.findViewById(R.id.view2);
        }
    }

}
