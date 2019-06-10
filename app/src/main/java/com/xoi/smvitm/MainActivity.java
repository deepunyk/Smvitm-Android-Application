package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.jaeger.library.StatusBarUtil;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView txt = (TextView)findViewById(R.id.txt);
        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        loadFragment(new Timetable_fragment());
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                Fragment fragment = null;
               switch (tabId){
                   case R.id.tab_home:
                       toolbar.setTitle("Home");
                       StatusBarUtil.setColor(MainActivity.this,getResources().getColor(R.color.main_menu1) );
                       toolbar.setBackgroundColor(getResources().getColor(R.color.main_menu1));
                       fragment = new Home_fragment();
                       break;
                   case R.id.tab_timetable:
                       toolbar.setTitle("Timetable");
                       StatusBarUtil.setColor(MainActivity.this,getResources().getColor(R.color.colorPrimary) );
                       toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                       fragment = new Timetable_fragment();
                       break;
                   case R.id.tab_circular:
                       toolbar.setTitle("Circulars");
                       StatusBarUtil.setColor(MainActivity.this,getResources().getColor(R.color.main_menu3) );
                       toolbar.setBackgroundColor(getResources().getColor(R.color.main_menu3));
                       fragment = new Home3_fragment();
                       break;
                   default:
                       fragment = new Home_fragment();
                       break;
               }
                loadFragment(fragment);
            }
        });

    }
    private boolean loadFragment(Fragment fragment){
        if(fragment!=null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contentContainer,fragment)
                    .commit();
            return true;
        }
        else
            return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.menu_signOut:
                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("login_Activity", "0").apply();
                FirebaseAuth.getInstance().signOut();
                Intent go = new Intent(MainActivity.this, login_Activity.class);
                startActivity(go);
                finish();
                overridePendingTransition(R.anim.push_up_in, R.anim.stay);
                break;
            case R.id.user_profile:
                Intent go1 = new Intent(MainActivity.this, User_profile_Activity.class);
                startActivity(go1);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.stay);
                break;
            default:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
