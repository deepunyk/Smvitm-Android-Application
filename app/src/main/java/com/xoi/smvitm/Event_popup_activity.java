package com.xoi.smvitm;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.w3c.dom.Text;

public class Event_popup_activity extends AppCompatActivity {

    TextView title, conduct, date, register;
    ExpandableTextView details;
    Button brochure;
    ImageView img;
    DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_popup_activity);
        img = (ImageView)findViewById(R.id.pop_img);
        title = (TextView)findViewById(R.id.pop_title);
        conduct = (TextView)findViewById(R.id.pop_conduct);
        register = (TextView)findViewById(R.id.pop_register);
        date = (TextView)findViewById(R.id.pop_date);
        brochure = (Button) findViewById(R.id.pop_brochure);
        details = (ExpandableTextView) findViewById(R.id.pop_details);
        downloadManager = (DownloadManager)this.getSystemService(Context.DOWNLOAD_SERVICE);


        Intent intent = getIntent();
        String title_txt = intent.getExtras().getString("title");
        String conduct_txt = intent.getExtras().getString("conduct");
        String date_txt = intent.getExtras().getString("date");
        final String brochure_txt = intent.getExtras().getString("brochure");
        String details_txt = intent.getExtras().getString("detail");
        String img_txt = intent.getExtras().getString("image");
        final String register_txt = intent.getExtras().getString("register");

        Glide.with(this).load(img_txt).into(img);
        title.setText(title_txt);
        conduct.setText(conduct_txt);
        date.setText(date_txt);
        details.setText(details_txt);

        if(details_txt.equals("-")){
            details.setVisibility(View.GONE);
        }

        brochure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                brochure.animate().alpha(0.5f).setDuration(100);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //viewHolder.downloadBtn.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                        brochure.animate().alpha(1).setDuration(100);
                    }
                }, 150);
                Uri uri = Uri.parse(brochure_txt);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setVisibleInDownloadsUi(false);
                downloadManager.enqueue(request);
                Toast.makeText(Event_popup_activity.this, "Downloading", Toast.LENGTH_SHORT).show();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(register_txt));
                startActivity(browserIntent);
            }
        });

        if(register_txt.equals("-")){
            register.setVisibility(View.GONE);
        }
    }
}
