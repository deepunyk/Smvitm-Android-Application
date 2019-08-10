package com.xoi.smvitm;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Attendance_fragment extends Fragment {

    View view;
    TextView attendance_txt;
    EditText usn_txt;
    Button submit;
    String usn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_attendance, container, false);

        attendance_txt = view.findViewById(R.id.attendance_txt);
        usn_txt = view.findViewById(R.id.usn_txt);
        submit = view.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usn = usn_txt.getText().toString();
                getAttendance();
            }
        });
        return view;
    }

    public void getAttendance(){

    }
}
