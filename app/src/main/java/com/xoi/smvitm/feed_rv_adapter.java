package com.xoi.smvitm;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
        if(viewHolder.stud_usn.equals(usn.get(i))){
            viewHolder.delete_but.setVisibility(View.VISIBLE);
        }
        else{
            viewHolder.delete_but.setVisibility(View.GONE);
        }
        viewHolder.delete_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Deleting", Toast.LENGTH_SHORT).show();
                deletePost(name.get(i),desc.get(i),usn.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return name.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView name_txt, date_txt, usn_txt, desc_txt;
        ImageButton delete_but;
        ConstraintLayout parent_layout;
        SharedPreferences sp;
        String stud_usn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.name_txt);
            date_txt = itemView.findViewById(R.id.date_txt);
            usn_txt = itemView.findViewById(R.id.usn_txt);
            desc_txt = itemView.findViewById(R.id.desc_txt);
            parent_layout = itemView.findViewById(R.id.parent_layout);
            delete_but = itemView.findViewById(R.id.delete_but);
            sp = mContext.getSharedPreferences("com.xoi.smvitm",Context.MODE_PRIVATE);
            stud_usn = sp.getString("stud_usn","");
        }
    }

    private void deletePost(final String n,final String d,final String u){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbyKEtZhACQek_7Lc6ht7OML1H6eLRsmLPVxndAKxVU9Azz7o20V/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Success")) {
                            Toast.makeText(mContext, "Post Deleted. Refresh to apply the changes.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                parmas.put("action", "deletePost");
                parmas.put("desc", d);
                parmas.put("name", n);
                parmas.put("usn", u);
                return parmas;
            }
        };
        int socketTimeOut = 50000;
        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(mContext);
        queue.add(stringRequest);
    }
}
