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

public class Display_QP_RV extends RecyclerView.Adapter<Display_QP_RV.ViewHolder>{

    private ArrayList<String> name = new ArrayList<String>();
    private ArrayList<String> type = new ArrayList<String>();
    private ArrayList<String> year = new ArrayList<String>();
    private ArrayList<String> link = new ArrayList<String>();
    private ArrayList<String> code = new ArrayList<String>();
    private Context mContext;

    public Display_QP_RV(ArrayList<String> name, ArrayList<String> type, ArrayList<String> year, ArrayList<String> link,ArrayList<String> code, Context mContext) {
        this.name = name;
        this.type = type;
        this.year = year;
        this.link = link;
        this.code = code;
        this.mContext = mContext;
    }



    @NonNull
    @Override
    public Display_QP_RV.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.qp_layoutlist, viewGroup, false);
        Display_QP_RV.ViewHolder holder = new Display_QP_RV.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final Display_QP_RV.ViewHolder viewHolder, final int i) {
        viewHolder.name_txt.setText(name.get(i));
        viewHolder.type_txt.setText(type.get(i));
        viewHolder.year_txt.setText(year.get(i));
        viewHolder.code_txt.setText(code.get(i));
        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(link.get(i));
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                viewHolder.downloadManager.enqueue(request);
                Toast.makeText(mContext, "Downloading", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name_txt, type_txt, year_txt, code_txt;
        ConstraintLayout parent_layout;
        Button downloadBtn;
        DownloadManager downloadManager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            type_txt = itemView.findViewById(R.id.type_txt);
            name_txt = itemView.findViewById(R.id.name_txt);
            year_txt = itemView.findViewById(R.id.year_txt);
            code_txt = itemView.findViewById(R.id.code_txt);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            downloadBtn = itemView.findViewById(R.id.download_but);
            downloadManager = (DownloadManager)itemView.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        }
    }
}
