package com.xoi.smvitm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.VerticalStepView;
import com.dd.processbutton.iml.ActionProcessButton;
import com.dinuscxj.refresh.IRefreshStatus;
import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class Timetable_fragment extends Fragment implements IRefreshStatus {

    String today_day;
    String[] day_list = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    MaterialSpinner day_select;
    View view;
    SharedPreferences sharedPreferences;
    ArrayList<String> monday_tt, tuesday_tt, wednesday_tt, thursday_tt, friday_tt, saturday_tt, time_tt;
    RecyclerView recyclerView;
    RecyclerRefreshLayout refreshLayout;
    ProgressDialog loader;
    Button btnClass;
    public static TextView branch_txt, sem_txt, section_txt;

    public static int branch, sem, section;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_timebtable, container, false);

        sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        monday_tt = new ArrayList<>();
        tuesday_tt = new ArrayList<>();
        wednesday_tt = new ArrayList<>();
        thursday_tt = new ArrayList<>();
        friday_tt = new ArrayList<>();
        saturday_tt = new ArrayList<>();
        time_tt = new ArrayList<>();

        branch_txt = (TextView)view.findViewById(R.id.branch_txt);
        sem_txt = (TextView)view.findViewById(R.id.sem_txt);
        section_txt = (TextView)view.findViewById(R.id.section_txt);

        refreshLayout = (RecyclerRefreshLayout) view.findViewById(R.id.main_swipe);
        btnClass = (Button)view.findViewById(R.id.btnClass);
        loader = new ProgressDialog(getActivity());

        String table_download = sharedPreferences.getString("table download", "");

        day_select = (MaterialSpinner) view.findViewById(R.id.day_select);
        day_select.setItems(day_list);

        if(sharedPreferences.contains("table download")){
            ArrayList<String> today_tt = new ArrayList<>();
            try {
                today_tt = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString(today_day, ObjectSerializer.serialize(new ArrayList<String>())));
                time_tt = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Time", ObjectSerializer.serialize(new ArrayList<String>())));
                setText(today_tt, time_tt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            refresh();
        }

        day_select.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                today_day = day_list[position];
                if (sharedPreferences.contains("table download")) {
                    ArrayList<String> today_tt = new ArrayList<>();
                    try {
                        today_tt = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString(today_day, ObjectSerializer.serialize(new ArrayList<String>())));
                        time_tt = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Time", ObjectSerializer.serialize(new ArrayList<String>())));
                        setText(today_tt, time_tt);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    getData();
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("table download", "1").apply();
                }
            }
        });

        getDay();

        refreshLayout.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        btnClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(getContext(), Popup_timetable_activity.class);
                startActivity(go);
            }
        });
        return view;
    }

    private void refresh(){
        time_tt.removeAll(time_tt);
        monday_tt.removeAll(monday_tt);
        tuesday_tt.removeAll(tuesday_tt);
        wednesday_tt.removeAll(wednesday_tt);
        thursday_tt.removeAll(thursday_tt);
        friday_tt.removeAll(friday_tt);
        saturday_tt.removeAll(saturday_tt);
        getData();
        refreshing();
    }
    @Override
    public void reset() {

    }

    @Override
    public void refreshing() {
        loader.setTitle("Updating timetable");
        loader.setMessage("Please wait...");
        loader.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loader.setCancelable(false);
        loader.show();
    }

    @Override
    public void refreshComplete() {
        loader.dismiss();
    }

    @Override
    public void pullToRefresh() {

    }

    @Override
    public void releaseToRefresh() {

    }

    @Override
    public void pullProgress(float pullDistance, float pullProgress) {

    }


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                int count = 0;
                int i;
                while (true) {
                    JSONObject jsonObject = new JSONObject(s);
                    String userInfo = jsonObject.getString("cseFirst");
                    JSONArray arr = new JSONArray(userInfo);
                    JSONObject jsonPart = arr.getJSONObject(0);
                    String cur_day = day_list[count];
                    count++;
                    for (i = 0; i < arr.length(); i++) {
                        jsonPart = arr.getJSONObject(i);
                        String day_str = jsonPart.getString("day");
                        if (day_str.equals("Time")) {
                            time_tt.add(jsonPart.getString("1"));
                            time_tt.add(jsonPart.getString("2"));
                            time_tt.add(jsonPart.getString("3"));
                            time_tt.add(jsonPart.getString("4"));
                            time_tt.add(jsonPart.getString("5"));
                            time_tt.add(jsonPart.getString("6"));
                            time_tt.add(jsonPart.getString("7"));
                            try {
                                sharedPreferences.edit().putString("Time", ObjectSerializer.serialize(time_tt)).apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        if (day_str.equals(cur_day)) {
                            break;
                        }
                    }
                    if (cur_day.equals("Monday")) {
                        monday_tt.add(jsonPart.getString("1"));
                        monday_tt.add(jsonPart.getString("2"));
                        monday_tt.add(jsonPart.getString("3"));
                        monday_tt.add(jsonPart.getString("4"));
                        monday_tt.add(jsonPart.getString("5"));
                        monday_tt.add(jsonPart.getString("6"));
                        monday_tt.add(jsonPart.getString("7"));
                        try {
                            sharedPreferences.edit().putString("Monday", ObjectSerializer.serialize(monday_tt)).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (cur_day.equals("Tuesday")) {
                        tuesday_tt.add(jsonPart.getString("1"));
                        tuesday_tt.add(jsonPart.getString("2"));
                        tuesday_tt.add(jsonPart.getString("3"));
                        tuesday_tt.add(jsonPart.getString("4"));
                        tuesday_tt.add(jsonPart.getString("5"));
                        tuesday_tt.add(jsonPart.getString("6"));
                        tuesday_tt.add(jsonPart.getString("7"));
                        try {
                            sharedPreferences.edit().putString("Tuesday", ObjectSerializer.serialize(tuesday_tt)).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (cur_day.equals("Wednesday")) {
                        wednesday_tt.add(jsonPart.getString("1"));
                        wednesday_tt.add(jsonPart.getString("2"));
                        wednesday_tt.add(jsonPart.getString("3"));
                        wednesday_tt.add(jsonPart.getString("4"));
                        wednesday_tt.add(jsonPart.getString("5"));
                        wednesday_tt.add(jsonPart.getString("6"));
                        wednesday_tt.add(jsonPart.getString("7"));
                        try {
                            sharedPreferences.edit().putString("Wednesday", ObjectSerializer.serialize(wednesday_tt)).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (cur_day.equals("Thursday")) {
                        thursday_tt.add(jsonPart.getString("1"));
                        thursday_tt.add(jsonPart.getString("2"));
                        thursday_tt.add(jsonPart.getString("3"));
                        thursday_tt.add(jsonPart.getString("4"));
                        thursday_tt.add(jsonPart.getString("5"));
                        thursday_tt.add(jsonPart.getString("6"));
                        thursday_tt.add(jsonPart.getString("7"));
                        try {
                            sharedPreferences.edit().putString("Thursday", ObjectSerializer.serialize(thursday_tt)).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (cur_day.equals("Friday")) {
                        friday_tt.add(jsonPart.getString("1"));
                        friday_tt.add(jsonPart.getString("2"));
                        friday_tt.add(jsonPart.getString("3"));
                        friday_tt.add(jsonPart.getString("4"));
                        friday_tt.add(jsonPart.getString("5"));
                        friday_tt.add(jsonPart.getString("6"));
                        friday_tt.add(jsonPart.getString("7"));
                        try {
                            sharedPreferences.edit().putString("Friday", ObjectSerializer.serialize(friday_tt)).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (cur_day.equals("Saturday")) {
                        saturday_tt.add(jsonPart.getString("1"));
                        saturday_tt.add(jsonPart.getString("2"));
                        saturday_tt.add(jsonPart.getString("3"));
                        saturday_tt.add(jsonPart.getString("4"));
                        saturday_tt.add(jsonPart.getString("5"));
                        saturday_tt.add(jsonPart.getString("6"));
                        saturday_tt.add(jsonPart.getString("7"));
                        try {
                            sharedPreferences.edit().putString("Saturday", ObjectSerializer.serialize(saturday_tt)).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    } else {
                        monday_tt.add(jsonPart.getString("1"));
                        monday_tt.add(jsonPart.getString("2"));
                        monday_tt.add(jsonPart.getString("3"));
                        monday_tt.add(jsonPart.getString("4"));
                        monday_tt.add(jsonPart.getString("5"));
                        monday_tt.add(jsonPart.getString("6"));
                        monday_tt.add(jsonPart.getString("7"));
                        try {
                            sharedPreferences.edit().putString("Monday", ObjectSerializer.serialize(monday_tt)).apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                ArrayList<String> today_tt = new ArrayList<>();
                try {
                    today_tt = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString(today_day, ObjectSerializer.serialize(new ArrayList<String>())));
                    time_tt = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Time", ObjectSerializer.serialize(new ArrayList<String>())));
                    setText(today_tt, time_tt);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void getDay() {
        int weekDay = 1;

        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);

        if (Calendar.MONDAY == dayOfWeek) {
            weekDay = 1;
        } else if (Calendar.TUESDAY == dayOfWeek) {
            weekDay = 2;
        } else if (Calendar.WEDNESDAY == dayOfWeek) {
            weekDay = 3;
        } else if (Calendar.THURSDAY == dayOfWeek) {
            weekDay = 4;
        } else if (Calendar.FRIDAY == dayOfWeek) {
            weekDay = 5;
        } else if (Calendar.SATURDAY == dayOfWeek) {
            weekDay = 6;
        } else if (Calendar.SUNDAY == dayOfWeek) {
            weekDay = 7;
        }
        switch (weekDay) {
            case 1:
                today_day = "Monday";
                day_select.setSelectedIndex(0);
                break;
            case 2:
                today_day = "Tuesday";
                day_select.setSelectedIndex(1);
                break;
            case 3:
                today_day = "Wednesday";
                day_select.setSelectedIndex(2);
                break;
            case 4:
                today_day = "Thursday";
                day_select.setSelectedIndex(3);
                break;
            case 5:
                today_day = "Friday";
                day_select.setSelectedIndex(4);

                break;
            case 6:
                today_day = "Saturday";
                day_select.setSelectedIndex(5);
                break;
            default:
                today_day = "Monday";
                day_select.setSelectedIndex(1);
        }
        ArrayList<String> today_tt = new ArrayList<>();
        try {
            today_tt = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString(today_day, ObjectSerializer.serialize(new ArrayList<String>())));
            time_tt = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("Time", ObjectSerializer.serialize(new ArrayList<String>())));
            setText(today_tt, time_tt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setText(ArrayList<String> classes, ArrayList<String> timings) {
        recyclerView = view.findViewById(R.id.recyclerview);
        Timetable_Recycler_ViewAdapter adapter = new Timetable_Recycler_ViewAdapter(classes, timings, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        refreshLayout.setRefreshing(false);
        refreshComplete();
    }

    private void getData() {
        Timetable_fragment.DownloadTask task = new Timetable_fragment.DownloadTask();
        task.execute("https://script.google.com/macros/s/AKfycbyHjV637lf3NmMpuk4mOtCHBGZLGSgqY3nlfh0uAAdnW00ke02x/exec");
    }

}
