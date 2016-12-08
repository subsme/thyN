package com.thyn.navigate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.thyn.R;
import com.thyn.collection.Filter;
import com.thyn.collection.MyTaskLab;
import com.thyn.collection.TaskLab;
import com.thyn.common.MyServerSettings;
import com.thyn.deleteMeInProd.AndroidDatabaseManager;
import com.thyn.graphics.MLRoundedImageView;
import com.thyn.intro.BasicProfileFragment;
import com.thyn.tab.DashboardFragment;
import com.thyn.tab.FilterFragment;
import com.thyn.tab.WelcomePageFragment;
import com.thyn.task.RandomTaskFragment;
import com.thyn.tasklist.my.MyTaskListFragment;

import java.io.File;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FilterFragment.OnFragmentInteractionListener {
    private static String TAG="NavigationActivity";
    private FloatingActionButton fab = null;
    private MyTaskLab manager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Context c = getApplicationContext();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewTask();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_dashboard);
        navigationView.getMenu().performIdentifierAction(R.id.nav_dashboard, 0);

        View hView = navigationView.getHeaderView(0);

        TextView nav_user = (TextView) hView.findViewById(R.id.nav_name);
        nav_user.setText(MyServerSettings.getUserName(c));

        TextView nav_address1 = (TextView) hView.findViewById(R.id.nav_address1);
        String address1 = MyServerSettings.getUserAddress(c);
        if (address1.indexOf(",") > 0) {
            nav_address1.setText(address1.substring(0, address1.indexOf(",")));
        }
        TextView nav_address2 = (TextView) hView.findViewById(R.id.nav_address2);
        nav_address2.setText(MyServerSettings.getUserCity(c));

        MLRoundedImageView nav_profile_image = (MLRoundedImageView) hView.findViewById(R.id.nav_profile_image);
        Picasso.with(c)
                .load(MyServerSettings.getUserProfileImageURL(c))
                .into(nav_profile_image);


        TextView nghbr_helped = (TextView) hView.findViewById(R.id.ngbrs_helped);
        nghbr_helped.setText(MyServerSettings.getNumNeighbrsIHelped(c)+"");

        TextView nghbr_points = (TextView) hView.findViewById(R.id.ngbrs_points);
        nghbr_points.setText(MyServerSettings.getPoints(c)+"");

        if (manager == null) manager = MyTaskLab.get(this);
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
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // Subu - I have commented this code 11/14
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        if(id == R.id.menu_filter){
            FilterFragment filterFragment = FilterFragment.newInstance("","");
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.navigation_fragment_container,
                    filterFragment,
                    filterFragment.getTag()).addToBackStack(null).commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            WelcomePageFragment welcomePageFragment = new WelcomePageFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.navigation_fragment_container,
                    welcomePageFragment,
                    welcomePageFragment.getTag()).commit();
            setTitle("Dashboard");

        } else if (id == R.id.nav_requests) {
            DashboardFragment dashboardFragment = new DashboardFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.navigation_fragment_container,
                    dashboardFragment,
                    dashboardFragment.getTag()).commit();
        }
        else if (id == R.id.nav_errands) {
            DashboardFragment dashboardFragment = DashboardFragment.newInstance(false,true);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.navigation_fragment_container,
                    dashboardFragment,
                    dashboardFragment.getTag()).commit();
        }
        else if (id == R.id.nav_myrequests) {
            MyTaskListFragment myTaskListFragment = MyTaskListFragment.newInstance(true);
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.navigation_fragment_container,
                    myTaskListFragment,
                    myTaskListFragment.getTag()).commit();
        }
        else if (id == R.id.nav_myinfo) {
            BasicProfileFragment basicProfileFragment = BasicProfileFragment.newInstance("", "");
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.navigation_fragment_container,
                    basicProfileFragment,
                    basicProfileFragment.getTag()).commit();
        }
       /* else if(id == R.id.nav_openlogfolder && MyServerSettings.LOCAL_DEBUG){

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"subusundaram@gmail.com"});
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Log file - thyNeighbr");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "body text");
            Log.d(TAG, "Opening " + MyServerSettings.getLocalFileLocation(getApplicationContext()));
            File file = new File(MyServerSettings.getLocalFileLocation(getApplicationContext()));
            if (!file.exists() || !file.canRead()) {
                Log.d(TAG, "cant open " + MyServerSettings.getLocalFileLocation(getApplicationContext()));;
            }
            Uri uri = Uri.fromFile(file);
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"));

        }*/
        /*else if(id == R.id.nav_opendatabase && MyServerSettings.LOCAL_DEBUG){
            Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
            startActivity(dbmanager);
        }*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showFloatingActionButton() {
        fab.show();
    }

    public void hideFloatingActionButton() {
        fab.hide();
    }
    private void createNewTask(){
        /*Intent i = new Intent(this, RandomTaskActivity.class);
        startActivity(i);*/
        RandomTaskFragment randomTaskFragment = new RandomTaskFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.navigation_fragment_container,
                randomTaskFragment,
                randomTaskFragment.getTag()).commit();

    }
    public void onFragmentInteraction(Filter filter){
        //To be implemeted eventually.

        Log.d(TAG,"Navigation onFramentinteraction....closestToMyHome set to " + filter.closestToMyHome);
        Log.d(TAG,"Navigation onFramentinteraction....mostrecent set to " + filter.mostRecent);
        Log.d(TAG,"Navigation onFramentinteraction....expiring soon set to " + filter.expiringSoon);
        MyServerSettings.initializeFilterSettings(getApplicationContext(), filter);
        DashboardFragment dashboardFragment = DashboardFragment.newInstance(true, false);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.navigation_fragment_container,
                dashboardFragment,
                dashboardFragment.getTag()).commit();


    }
}
