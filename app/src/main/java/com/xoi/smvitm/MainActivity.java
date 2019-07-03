package com.xoi.smvitm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.jaeger.library.StatusBarUtil;
import com.tomer.fadingtextview.FadingTextView;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    ActionBarDrawerToggle actionBarDrawerToggle;
    DrawerLayout drawerLayout;
    TextView header_name;
    FadingTextView header_branch;
    SharedPreferences sharedPreferences;
    String student_branch;
    long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = this.getSharedPreferences("com.xoi.smvitm",MODE_PRIVATE);

        String student_name = sharedPreferences.getString("Student name", "");
        String student_sem = sharedPreferences.getString("Student sem","");
        student_branch = sharedPreferences.getString("Student branch", "");
        String student_section = sharedPreferences.getString("Student section","");
        student_sem = student_sem + " " + student_section;
        String student_usn = sharedPreferences.getString("Student usn", "");
        String[] array_txt = {student_branch, student_sem,student_usn};

        drawerLayout = (DrawerLayout)findViewById(R.id.nav_drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.Open,R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        loadFragment(new Home_fragment());



        final NavigationView navigationView = (NavigationView) findViewById(R.id.navView);
        View hView =  navigationView.getHeaderView(0);
        header_name = (TextView)hView.findViewById(R.id.header_name);
        header_branch = (FadingTextView) hView.findViewById(R.id.header_branch);
        header_branch.setTexts(array_txt);
        header_name.setText(student_name);

        navigationView.setCheckedItem(R.id.home_nav);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                Fragment fragment = null;
                menuItem.setChecked(true);
                switch (id){
                    case R.id.home_nav:
                        fragment = new Home_fragment();
                        toolbar.setTitle("SMVITM");
                        break;
                    case R.id.timetable_nav:
                        fragment = new Timetable_fragment();
                        toolbar.setTitle("Timetable");
                        break;
                    case R.id.circular_nav:
                        fragment = new Circular_fragment();
                        toolbar.setTitle("Circulars");
                        break;
                    case R.id.faculty_nav:
                        fragment = new Faculty_fragment();
                        toolbar.setTitle("Faculty");
                        break;
                    case R.id.events_nav:
                        fragment = new Event_fragment();
                        toolbar.setTitle("Events");
                        break;
                    case R.id.sign_out_nav:
                        SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("com.xoi.smvitm", Context.MODE_PRIVATE);
                        sharedPreferences.edit().clear().apply();
                        FirebaseAuth.getInstance().signOut();
                        Intent go1 = new Intent(MainActivity.this, login_Activity.class);
                        startActivity(go1);
                        finish();
                        overridePendingTransition(R.anim.push_up_in, R.anim.stay);
                    default:
                        toolbar.setTitle("SMVITM");
                        return false;
                }
                drawerLayout.closeDrawers();
                loadFragment(fragment);
                return true;
            }
        });

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
                sharedPreferences.edit().clear().apply();
                FirebaseAuth.getInstance().signOut();
                Intent go = new Intent(MainActivity.this, login_Activity.class);
                startActivity(go);
                finish();
                overridePendingTransition(R.anim.push_up_in, R.anim.stay);
                break;
            default:
                return false;
        }
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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

    public void navigation_profile(View view){
        Intent go = new Intent(MainActivity.this, User_profile_Activity.class);
        startActivity(go);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.stay);

    }

    @Override
    public void onBackPressed() {
        if(backPressedTime + 2000 > System.currentTimeMillis()){
            super.onBackPressed();
        }
        else{
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}
