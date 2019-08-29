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
import android.widget.Toast;

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

public class exam_faculty_rv_adapter extends RecyclerView.Adapter<exam_faculty_rv_adapter.ViewHolder>{

    private ArrayList<String> date = new ArrayList<>();
    private ArrayList<String> time = new ArrayList<>();
    private ArrayList<String> name = new ArrayList<>();
    private ArrayList<String> portion = new ArrayList<>();

    public exam_faculty_rv_adapter(ArrayList<String> date, ArrayList<String> time, ArrayList<String> name, ArrayList<String> portion, String class_name, Context mContext) {
        this.date = date;
        this.time = time;
        this.name = name;
        this.portion = portion;
        this.class_name = class_name;
        this.mContext = mContext;
    }

    private String class_name;
    private Context mContext;
    String portion_submit;


    @NonNull
    @Override
    public exam_faculty_rv_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.exam_layout_list, viewGroup, false);
        exam_faculty_rv_adapter.ViewHolder holder = new exam_faculty_rv_adapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final exam_faculty_rv_adapter.ViewHolder viewHolder, final int i) {

        viewHolder.name_txt.setText(name.get(i));
        viewHolder.time_txt.setText(time.get(i));
        viewHolder.date_txt.setText(date.get(i));
        viewHolder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(name.get(i),portion.get(i),i, viewHolder);
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

    private void showDialog(final String name, final String portion_prev, final int index, final ViewHolder viewHolder){
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        final EditText edittext = new EditText(mContext);
        int paddingDp = 25;
        float density = mContext.getResources().getDisplayMetrics().density;
        int paddingPixel = (int)(paddingDp * density);
        edittext.setPadding(paddingPixel,paddingPixel,paddingPixel,paddingPixel);
        alert.setMessage("Enter the portion");
        alert.setTitle(name);
        alert.setView(edittext);
        edittext.setText(portion_prev);
        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                portion_submit = edittext.getText().toString();
                if(portion_submit.equals("")){
                    portion_submit = "-";
                }
                addPortion(name,index, viewHolder);
                Toast.makeText(mContext, "Please wait, applying changes", Toast.LENGTH_SHORT).show();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();

    }

    private void addPortion(final String name, final int index, final ViewHolder viewHolder){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbwwK8c4u6cSDuUIU5rhTphcRYlVpLRlF2B4UNLasBppgmO85F5r/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("Success")) {
                            Toast.makeText(mContext, "Changes have been applied", Toast.LENGTH_SHORT).show();
                            portion.set(index,portion_submit);
                            viewHolder.portion.setText(portion.get(index));
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

                parmas.put("action", "addPortion");
                parmas.put("exam_name", name);
                parmas.put("portion", portion_submit);
                parmas.put("class", class_name);
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
