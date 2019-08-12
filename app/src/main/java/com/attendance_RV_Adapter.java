package com;

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

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.xoi.smvitm.R;

import java.util.ArrayList;

public class attendance_RV_Adapter extends RecyclerView.Adapter<attendance_RV_Adapter.ViewHolder> {

    ArrayList<String> lsub = new ArrayList<>();
    ArrayList<String> rsub = new ArrayList<>();
    ArrayList<String> lca = new ArrayList<>();
    ArrayList<String> rca = new ArrayList<>();
    ArrayList<String> lct = new ArrayList<>();
    ArrayList<String> rct = new ArrayList<>();
    private Context mContext;

    public attendance_RV_Adapter(ArrayList<String> lsub, ArrayList<String> rsub, ArrayList<String> lca, ArrayList<String> rca, ArrayList<String> lct, ArrayList<String> rct, Context mContext) {
        this.lsub = lsub;
        this.rsub = rsub;
        this.lca = lca;
        this.rca = rca;
        this.lct = lct;
        this.rct = rct;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public attendance_RV_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attendance_layout_list, viewGroup, false);
        attendance_RV_Adapter.ViewHolder holder = new attendance_RV_Adapter.ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final attendance_RV_Adapter.ViewHolder viewHolder, final int i) {

        float lp, rp;
        float lcla = Integer.parseInt(lca.get(i)), rcla = Integer.parseInt(rca.get(i)), lclt = Integer.parseInt(lct.get(i)), rclt = Integer.parseInt(rct.get(i));
        lp = (lcla/lclt)*100;
        rp = (rcla/rclt)*100;
        viewHolder.sub1.setProgress((int)lp);
        viewHolder.sub2.setProgress((int)rp);
        viewHolder.sub1.setBottomText(lca.get(i) + "/" + lct.get(i));
        viewHolder.sub2.setBottomText(rca.get(i) + "/" + rct.get(i));
        viewHolder.sub1_txt.setText(lsub.get(i));
        viewHolder.sub2_txt.setText(rsub.get(i));
        if(lp < 85){
            viewHolder.sub1.setFinishedStrokeColor(viewHolder.red);
            viewHolder.sub1.setTextColor(viewHolder.red);
            viewHolder.sub1_txt.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        else{
            viewHolder.sub1.setFinishedStrokeColor(viewHolder.green);
            viewHolder.sub1.setTextColor(viewHolder.green);
            viewHolder.sub1_txt.setTextColor(mContext.getResources().getColor(R.color.green));
        }
        if(rp < 85){
            viewHolder.sub2.setTextColor(viewHolder.red);
            viewHolder.sub2.setFinishedStrokeColor(viewHolder.red);
            viewHolder.sub2_txt.setTextColor(mContext.getResources().getColor(R.color.red));
        }
        else {
            viewHolder.sub2.setTextColor(viewHolder.green);
            viewHolder.sub2.setFinishedStrokeColor(viewHolder.green);
            viewHolder.sub2_txt.setTextColor(mContext.getResources().getColor(R.color.green));
        }
    }

    @Override
    public int getItemCount() {
        return lsub.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ArcProgress sub1, sub2;
        TextView sub1_txt, sub2_txt;
        int red, green;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            sub1 = itemView.findViewById(R.id.sub1);
            sub2 = itemView.findViewById(R.id.sub2);
            sub1_txt = itemView.findViewById(R.id.sub1_txt);
            sub2_txt = itemView.findViewById(R.id.sub2_txt);
            green = sub1.getFinishedStrokeColor();
            red = sub2.getFinishedStrokeColor();
        }
    }
}
