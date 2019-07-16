package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.jaeger.library.StatusBarUtil;



public class login_Activity extends AppCompatActivity {
    private Button sign;
    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private SpinKitView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sign = (Button)findViewById(R.id.sign);
        loading = (SpinKitView)findViewById(R.id.spin_kit);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        StatusBarUtil.setTransparent(login_Activity.this);
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sign.setAlpha(0.7f);
                sign.setBackgroundColor(getResources().getColor(R.color.silver));
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        sign.setAlpha(1f);
                        sign.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                }, 50 );//time in milisecond
                loading.setVisibility(View.VISIBLE);
                signIn();
            }
        });
    }
    private void signIn() {
        mGoogleSignInClient.signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                loading.setVisibility(View.GONE);
                Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        try {
            String personGivenName = acct.getGivenName();
            String stud_usn = acct.getEmail().substring(acct.getEmail().indexOf(".") + 1, acct.getEmail().indexOf("@"));
            stud_usn = "4MW" + stud_usn.toUpperCase();
            final String personEmail = acct.getEmail();
            String personId = acct.getId();
            store(personGivenName, stud_usn, personEmail, personId);
            AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            loading.setVisibility(View.GONE);
                            if (task.isSuccessful()) {
                                try {
                                    String result = personEmail.substring(personEmail.indexOf("@") + 1, personEmail.indexOf(".in"));
                                    SharedPreferences sharedPreferences = login_Activity.this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
                                    sharedPreferences.edit().putString("login_Activity", "1").apply();
                                    Intent go = new Intent(login_Activity.this, User_profile_Activity.class);
                                    startActivity(go);
                                    finish();
                                    overridePendingTransition(R.anim.push_up_in, R.anim.stay);
                                } catch (Exception e) {
                                    Toast.makeText(login_Activity.this, "Please use sode-edu account to log in", Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(login_Activity.this, "Server error", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        catch (Exception e){
            Toast.makeText(this, "Please use sode-edu account to log in", Toast.LENGTH_SHORT).show();
        }
    }
    private void store(String givenName, String stud_usn, String email, String id){
        SharedPreferences sharedPreferences = this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
        sharedPreferences.edit().putString("givenName", givenName).apply();
        sharedPreferences.edit().putString("stud_usn", stud_usn).apply();
        sharedPreferences.edit().putString("Student Email", email).apply();
        sharedPreferences.edit().putString("id", id).apply();;
    }
}
