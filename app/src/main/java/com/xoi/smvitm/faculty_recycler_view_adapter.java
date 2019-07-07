package com.xoi.smvitm;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class faculty_recycler_view_adapter extends RecyclerView.Adapter<faculty_recycler_view_adapter.ViewHolder>{
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> designations = new ArrayList<>();
    private ArrayList<String> branches = new ArrayList<>();
    private ArrayList<String> mobiles = new ArrayList<>();
    private ArrayList<String> emails = new ArrayList<>();
    private ArrayList<String> photolinks = new ArrayList<>();
    private Context mContext;

    public faculty_recycler_view_adapter(ArrayList<String> names, ArrayList<String> designations, ArrayList<String> branches, ArrayList<String> mobiles, ArrayList<String> emails, ArrayList<String> photolinks, Context mContext) {
        this.names = names;
        this.designations = designations;
        this.branches = branches;
        this.mobiles = mobiles;
        this.emails = emails;
        this.photolinks = photolinks;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public faculty_recycler_view_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.faculty_layout_list_item, viewGroup, false);
        faculty_recycler_view_adapter.ViewHolder holder = new faculty_recycler_view_adapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.name.setText(names.get(i));
        viewHolder.designation.setText(designations.get(i));
        viewHolder.branch.setText(branches.get(i));
        viewHolder.mobile.setText(mobiles.get(i));
        viewHolder.email.setText(emails.get(i));
        Glide.with(mContext).load(photolinks.get(i)).placeholder(R.drawable.user_profile_icon).error(R.drawable.college_logo).into(viewHolder.imgFaculty);
    }

    @Override
    public int getItemCount() {
        return designations.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout parent_layout;
        TextView name, designation, branch, mobile, email;
        ImageView imgFaculty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            branch = itemView.findViewById(R.id.branch);
            designation = itemView.findViewById(R.id.designation);
            mobile = itemView.findViewById(R.id.mobile);
            email = itemView.findViewById(R.id.email);
            imgFaculty = itemView.findViewById(R.id.imgFaculty);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
