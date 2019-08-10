package com.xoi.smvitm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Faculty_passsword_change extends AppCompatActivity {


    EditText newpass, confirmpass;
    Button pass_but;
    String newstr, constr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faculty_passsword_change);

        newpass = (EditText)findViewById(R.id.pass_edit);
        confirmpass = (EditText)findViewById(R.id.confirm_pass);
        pass_but = (Button)findViewById(R.id.change_pass);

        pass_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newstr = newpass.getText().toString();
                constr = confirmpass.getText().toString();
                if(newstr.equals(constr)){
                    if(newstr.length() < 5){
                        Toast.makeText(Faculty_passsword_change.this, "Password too short. It should contain atleast 5 characters", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        changePassword();
                    }
                }else{
                    Toast.makeText(Faculty_passsword_change.this, "Passwords don't match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void changePassword(){

    }
}
