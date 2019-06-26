package com.xoi.smvitm;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import org.w3c.dom.Text;

public class Event_popup_activity extends AppCompatActivity {

    TextView title, conduct, date;
    ExpandableTextView details;
    Button brochure;
    ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_popup_activity);
        img = (ImageView)findViewById(R.id.pop_img);
        title = (TextView)findViewById(R.id.pop_title);
        conduct = (TextView)findViewById(R.id.pop_conduct);
        date = (TextView)findViewById(R.id.pop_date);
        brochure = (Button) findViewById(R.id.pop_brochure);
        details = (ExpandableTextView) findViewById(R.id.pop_details);

        Intent intent = getIntent();
        String title_txt = intent.getExtras().getString("title");
        String conduct_txt = intent.getExtras().getString("conduct");
        String date_txt = intent.getExtras().getString("date");
        final String brochure_txt = intent.getExtras().getString("brochure");
        String details_txt = intent.getExtras().getString("detail");
        String img_txt = intent.getExtras().getString("image");

        Glide.with(this).load(img_txt).into(img);
        title.setText(title_txt);
        conduct.setText(conduct_txt);
        date.setText(date_txt);
        details.setText(details_txt);

        brochure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(brochure_txt);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


    }
}
