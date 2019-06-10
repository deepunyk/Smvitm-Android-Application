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

public class Recycler_View_Adapter extends RecyclerView.Adapter<Recycler_View_Adapter.ViewHolder>{

    private ArrayList<String> descriptions = new ArrayList<String>();
    private Context mContext;

    public Recycler_View_Adapter(ArrayList<String> descriptions, Context mContext) {
        this.descriptions = descriptions;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        viewHolder.description.setText(descriptions.get(i));
        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, descriptions.get(i), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView description;
        ConstraintLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            parent_layout = itemView.findViewById(R.id.parent_layout);

        }
    }
}
