package com.xoi.smvitm;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
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

import at.markushi.ui.CircleButton;

public class Circular_Recycler_View_Adapter extends RecyclerView.Adapter<Circular_Recycler_View_Adapter.ViewHolder>{

    private ArrayList<String> descriptions = new ArrayList<String>();
    private ArrayList<String> links = new ArrayList<String>();
    private ArrayList<String> dates = new ArrayList<String>();
    private Context mContext;

    public Circular_Recycler_View_Adapter(ArrayList<String> descriptions, ArrayList<String> links, ArrayList<String> dates, Context mContext) {
        this.descriptions = descriptions;
        this.links = links;
        this.dates = dates;
        this.mContext = mContext;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.circular_layout_list_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        viewHolder.description.setText(descriptions.get(i));
        viewHolder.date.setText(dates.get(i));
        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Uri uri = Uri.parse(links.get(i));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                mContext.startActivity(intent);*/
                //viewHolder.downloadBtn.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                viewHolder.downloadBtn.animate().alpha(0.5f).setDuration(100);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //viewHolder.downloadBtn.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                        viewHolder.downloadBtn.animate().alpha(1).setDuration(100);
                    }
                }, 150);
                Uri uri = Uri.parse(links.get(i));
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
        return descriptions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView description, date;
        ConstraintLayout parent_layout;
        Button downloadBtn;
        DownloadManager downloadManager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            description = itemView.findViewById(R.id.description);
            date = itemView.findViewById(R.id.date);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            downloadBtn = itemView.findViewById(R.id.downloadBtn);
            downloadManager = (DownloadManager)itemView.getContext().getSystemService(Context.DOWNLOAD_SERVICE);

        }
    }
}
