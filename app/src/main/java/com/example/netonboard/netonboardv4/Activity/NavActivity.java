package com.example.netonboard.netonboardv4.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.example.netonboard.netonboardv4.Fragments.SupportMain;
import com.example.netonboard.netonboardv4.Fragments.SupportServerDown;
import com.example.netonboard.netonboardv4.Fragments.SupportServerHistory;
import com.example.netonboard.netonboardv4.Object.GlobalFileIO;
import com.example.netonboard.netonboardv4.R;
import com.example.netonboard.netonboardv4.Services.BackgroundService;
import com.example.netonboard.netonboardv4.Adapter.PagerAdapter;

import org.json.JSONException;
import org.json.JSONObject;

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //Drawer component
    TextView tv_drawer_title, tv_drawer_username;
    PagerAdapter pagerAdapter;
    ViewPager viewPager;
    GlobalFileIO fileIO;
    FrameLayout content_frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        tv_drawer_username = (TextView) headerView.findViewById(R.id.tv_drawer_username);
        String s_username = LoginActivity.sp_name.getString("key", "");
        tv_drawer_username.setText(s_username);

        fileIO = new GlobalFileIO(getBaseContext());

        content_frame = (FrameLayout) findViewById(R.id.nav_content_framelayout);
        LayoutInflater factory = LayoutInflater.from(getBaseContext());
        View homepageView = factory.inflate(R.layout.homepage, null);
        content_frame.addView(homepageView);

        if(content_frame.getChildCount() == 1){

            TextView tv_homepage_claim_data = (TextView) findViewById(R.id.tv_homepage_claim_data);
            TextView tv_homepage_leave_data = (TextView) findViewById(R.id.tv_homepage_leave_data);
            tv_homepage_claim_data.setText("RM50 claim");
            tv_homepage_leave_data.setText("50 Days");

            TextView tv_support_primary = (TextView) findViewById(R.id.tv_homepage_support_primary_data);
            TextView tv_support_secondary = (TextView) findViewById(R.id.tv_homepage_support_secondary_data);


            try{
                String body = fileIO.readFile(fileIO.getFILENAMESUPPORT());
                JSONObject jsonObject = new JSONObject(body);
                String primarySupport = jsonObject.getString("s_user_id_standby");
                String secondarySupport = jsonObject.getString("s_user_id_standby_backup");
                tv_support_primary.setText(primarySupport);
                tv_support_secondary.setText(secondarySupport);
            } catch (JSONException e) {
                e.printStackTrace();
                tv_support_primary.setText("Failed to load data");
                tv_support_secondary.setText("Failed to load data");
            }

            TextView tv_garbage_collector_date = (TextView) findViewById(R.id.tv_garbage_collector_date);
            TextView tv_garbage_collector = (TextView) findViewById(R.id.tv_garbage_collector_name);
            tv_garbage_collector_date.setText("(28-09-18):");
            tv_garbage_collector.setText("Chee Hong");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_setting) {
            startActivity(new Intent(NavActivity.this, SettingsActivity.class));
            onPause();
            return true;
        } else if(id == R.id.action_logout){
            logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar){
            Intent calendarIntent = new Intent(this, CalendarActivity.class);
            startActivity(calendarIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setupPagerAdapter(ViewPager viewPager){
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SupportMain(), "Home");
        adapter.addFragment(new SupportServerDown(), "Server Down");
        adapter.addFragment(new SupportServerHistory(), "Server History");
        viewPager.setAdapter(adapter);
    }

    public void logout() {
        LoginActivity.is_login = false;
        stopService(new Intent(this, BackgroundService.class));
        LoginActivity.sp_name = getSharedPreferences(LoginActivity.PREFS_NAME, MODE_PRIVATE);
        LoginActivity.sp_editor = LoginActivity.sp_name.edit();
        LoginActivity.sp_editor.clear();
        LoginActivity.sp_editor.commit();

        PasscodeActivity.sharedPreferences = getSharedPreferences(PasscodeActivity.PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = PasscodeActivity.sharedPreferences.edit();
        editor.clear();
        editor.commit();
        stopService(new Intent(NavActivity.this, BackgroundService.class));
        finish();
    }
}
