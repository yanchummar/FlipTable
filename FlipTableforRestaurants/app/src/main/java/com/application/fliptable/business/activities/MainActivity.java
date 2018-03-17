package com.application.fliptable.business.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import com.application.fliptable.business.R;
import com.application.fliptable.business.adapters.ReservationsAdapter;
import com.application.fliptable.business.models.Reservation;
import com.application.fliptable.business.models.ReservationResponse;
import com.application.fliptable.business.rest.ApiClient;
import com.application.fliptable.business.rest.ApiInterface;
import com.application.fliptable.business.utils.InternetTime;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.application.fliptable.business.activities.DetailActivity.PARCELABLE_CURRENT_RES;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ReservationsAdapter reservationsAdapter;
    private RecyclerView reservationsRecyclerView;
    private ArrayList<Reservation> allReservationList, pendingList, upcomingList, recentList, changedList;
    private int currentSection = 0;
    private ReservationsAdapter.OnItemClickListener listener, mainContentListener;
    private ApiInterface apiInterface;
    private LinearLayout errrorLayout;
    private TextView errorText;
    private ImageView errorImageView;
    private CardView errorTryAgain;
    private int spotId;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;

    static final String SPOT_ID_KEY = "spot_id_key", LOGIN_SHARED_PREF = "login_shared_preferences_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        sharedPreferences = getSharedPreferences(LOGIN_SHARED_PREF,MODE_PRIVATE);
        if (sharedPreferences.getInt(SPOT_ID_KEY, -1) != -1){
            spotId = sharedPreferences.getInt(SPOT_ID_KEY, -1);
        } else {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            return;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Error Layout
        errrorLayout = findViewById(R.id.error_layout_main);
        errorImageView = findViewById(R.id.error_imageview);
        errorText = findViewById(R.id.error_textview);
        errorTryAgain = findViewById(R.id.error_try_again_button);

        listener = new ReservationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position, boolean cancel) {
                updateLists(position, cancel);
            }

            @Override
            public void onMainContentClick(int position) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Reservation currentRes;
                if (currentSection == 0){
                    currentRes = pendingList.get(position);
                } else if (currentSection == 1){
                    currentRes = upcomingList.get(position);
                } else {
                    currentRes = recentList.get(position);
                }
                intent.putExtra(PARCELABLE_CURRENT_RES, currentRes);
                startActivity(intent);
            }
        };

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        tabLayout.addTab(tabLayout.newTab().setText("Upcoming"));
        tabLayout.addTab(tabLayout.newTab().setText("Recent"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ArrayList<Reservation> currentList;
                if (tab.getText().equals("Pending")){
                    currentSection = 0;
                    currentList = pendingList;
                } else if (tab.getText().equals("Upcoming")){
                    currentSection = 1;
                    currentList = upcomingList;
                } else {
                    currentSection = 2;
                    currentList = recentList;
                }
                if (currentList != null){
                    reservationsAdapter = new ReservationsAdapter(currentList, currentSection, listener);
                    reservationsRecyclerView.setAdapter(reservationsAdapter);
                    if (currentList.isEmpty()){
                        errrorLayout.setVisibility(View.VISIBLE);
                        errorImageView.setImageResource(R.drawable.other_error);
                        errorText.setText("This section is Empty now!");
                        errorTryAgain.setVisibility(View.GONE);
                    } else{
                        errrorLayout.setVisibility(View.GONE);
                    }
                } else {
                    errrorLayout.setVisibility(View.VISIBLE);
                    errorImageView.setImageResource(R.drawable.other_error);
                    errorText.setText("This section is Empty now!");
                    errorTryAgain.setVisibility(View.GONE);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Dummy Data
        Calendar calendar = Calendar.getInstance();
        long millis = calendar.getTimeInMillis();
        ArrayList<Reservation> reservationsList = new ArrayList<>();
        reservationsList.add(new Reservation("108","John Doe",4,millis,"12:00PM"));
        reservationsList.add(new Reservation("200","Alan Doe",1,millis,"3:00PM"));

        reservationsRecyclerView = (RecyclerView) findViewById(R.id.reservation_recyclerview);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        reservationsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        reservationsRecyclerView.setAdapter(reservationsAdapter);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (spotId != -1) {
                    errrorLayout.setVisibility(View.GONE);
                    fetchAndLoadData(spotId);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        /* Loading Data */
        if (spotId != -1) {
            fetchAndLoadData(spotId);
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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_logout) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setMessage("Proceed to cancelling the reservation?");
            alertBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sharedPreferences.edit().remove(SPOT_ID_KEY).apply();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            });
            alertBuilder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            alertBuilder.create().show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** Fetching Data **/

    public void updateReservationStatus(int resId, final String status, final int action, final int position){
        Call<String> updateCall = apiInterface.updateReservation(resId, status);
        updateCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    if (!response.body().toString().contains("Error")){
                        if (action == 0){
                            pendingList.get(position).setStatus(status);
                            pendingList.get(position).setLoading(false);
                            upcomingList.add(pendingList.get(position));
                            pendingList.remove(position);
                            changedList = pendingList;
                        } else if (action == 1){
                            pendingList.get(position).setStatus(status);
                            pendingList.get(position).setLoading(false);
                            recentList.add(0, pendingList.get(position));
                            pendingList.remove(position);
                            changedList = pendingList;
                        } else if (action == 2 || action == 3){
                            upcomingList.get(position).setStatus(status);
                            upcomingList.get(position).setLoading(false);
                            recentList.add(0, upcomingList.get(position));
                            upcomingList.remove(position);
                            changedList = upcomingList;
                        }

                        if (action == 0){
                            // Sorting
                            ArrayList<Reservation> upcomingSortList = upcomingList;
                            upcomingList = new ArrayList<>();
                            if (!upcomingSortList.isEmpty()) {
                                long leastTimeStamp = upcomingSortList.get(0).getBookingDate();
                                for (int i = 0; i < upcomingSortList.size(); i++) {
                                    if (upcomingSortList.get(i).getBookingDate() <= leastTimeStamp) {
                                        upcomingList.add(0, upcomingSortList.get(i));
                                        leastTimeStamp = upcomingSortList.get(i).getBookingDate();
                                    } else {
                                        upcomingList.add(upcomingSortList.get(i));
                                    }
                                }
                            }
                        }
                        reservationsAdapter = new ReservationsAdapter(changedList, currentSection, listener);
                        reservationsRecyclerView.setAdapter(reservationsAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if (action == 2){
                    upcomingList.get(position).setLoading(false);
                    reservationsAdapter.notifyItemChanged(position);
                } else {
                    pendingList.get(position).setLoading(false);
                    reservationsAdapter.notifyItemChanged(position);
                }
            }
        });
    }

    public void fetchAndLoadData(int spotId){
        Log.i("MainActivity","InTheFetchAndLoadData:"+spotId);
        retrofit2.Call<ReservationResponse> call = apiInterface.getReservations(spotId);
        call.enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call, Response<ReservationResponse> response) {
                if (response.isSuccessful()) {
                    allReservationList = new ArrayList<>();
                    pendingList = new ArrayList<>();
                    upcomingList = new ArrayList<>();
                    recentList = new ArrayList<>();
                    errrorLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    allReservationList = response.body().getReservationList();
                    classifyData(allReservationList, response.body().getTime());
                    Log.i("MainActivity","allReservationsListItemOne:"+allReservationList.get(0));
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable t) {
                t.printStackTrace();
                errrorLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                errorImageView.setImageResource(R.drawable.network_error);
                errorText.setText("Error connecting to server! Please check your connection.");
                errorTryAgain.setVisibility(View.GONE);
            }
        });
    }

    public void classifyData(ArrayList<Reservation> allList, String dateString){
        InternetTime internetTime = new InternetTime(dateString);
        Calendar internetCalendar = Calendar.getInstance();
        internetCalendar.setTime(internetTime.getDate());
        ArrayList<Reservation> nonRecentList = new ArrayList<>();
        // Recent Classification
        for (int i=0;i<allList.size();i++){
            if (allList.get(i).getBookingDate() <= internetCalendar.getTimeInMillis()){
                recentList.add(allList.get(i));
            } else {
                nonRecentList.add(allList.get(i));
            }
        }
        for (int i=0;i<nonRecentList.size();i++){
            if (nonRecentList.get(i).getStatus().toLowerCase().contains("pending")){
                pendingList.add(nonRecentList.get(i));
            } else if (nonRecentList.get(i).getStatus().toLowerCase().contains("confirmed")){
                upcomingList.add(nonRecentList.get(i));
            } else {
                recentList.add(nonRecentList.get(i));
            }
        }
        // Sorting
        ArrayList<Reservation> upcomingSortList = upcomingList;
        upcomingList = new ArrayList<>();
        if (!upcomingSortList.isEmpty()) {
            long leastTimeStamp = upcomingSortList.get(0).getBookingDate();
            for (int i = 0; i < upcomingSortList.size(); i++) {
                if (upcomingSortList.get(i).getBookingDate() <= leastTimeStamp) {
                    upcomingList.add(0, upcomingSortList.get(i));
                    leastTimeStamp = upcomingSortList.get(i).getBookingDate();
                } else {
                    upcomingList.add(upcomingSortList.get(i));
                }
            }
        }
        // Loading
        ArrayList<Reservation> currentList;
        if (currentSection == 0){
            currentList = pendingList;
        } else if (currentSection == 1){
            currentList = upcomingList;
        } else {
            currentList = recentList;
        }
        reservationsAdapter = new ReservationsAdapter(currentList, currentSection, listener);
        reservationsRecyclerView.setAdapter(reservationsAdapter);
        if (currentList.isEmpty()){
            errrorLayout.setVisibility(View.VISIBLE);
            errorImageView.setImageResource(R.drawable.other_error);
            errorText.setText("This section is Empty now!");
            errorTryAgain.setVisibility(View.GONE);
        }
    }

    public void updateLists(final int position, boolean cancel){
        changedList = new ArrayList<>();
        if (!cancel){
            if (currentSection == 0) {
                if (!pendingList.isEmpty()) {
                    Reservation currentRes = pendingList.get(position);
                    int resId = Integer.parseInt(currentRes.getReservationId());
                    String status = "Reservation Confirmed";
                    // Set Loading
                    pendingList.get(position).setLoading(true);
                    reservationsAdapter.notifyItemChanged(position);
                    updateReservationStatus(resId, status, 0, position);
                }
            } else if (currentSection == 1){
                if (!upcomingList.isEmpty()){
                    Reservation currentRes = upcomingList.get(position);
                    int resId = Integer.parseInt(currentRes.getReservationId());
                    String status = "Diner Seated";
                    // Set Loading
                    upcomingList.get(position).setLoading(true);
                    reservationsAdapter.notifyItemChanged(position);
                    updateReservationStatus(resId, status, 3, position);
                }
            }
        } else {
            // Alert Dialog
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(MainActivity.this);
            alertBuilder.setMessage("Proceed to cancelling the reservation?");
            alertBuilder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (currentSection == 0) {
                        if (!pendingList.isEmpty()) {
                            Reservation currentRes = pendingList.get(position);
                            int resId = Integer.parseInt(currentRes.getReservationId());
                            String status = "Reservation Cancelled";
                            // Set Loading
                            pendingList.get(position).setLoading(true);
                            reservationsAdapter.notifyItemChanged(position);
                            updateReservationStatus(resId, status, 1, position);
                        }
                    } else if (currentSection == 1){
                        if (!upcomingList.isEmpty()){
                            Reservation currentRes = upcomingList.get(position);
                            int resId = Integer.parseInt(currentRes.getReservationId());
                            String status = "Reservation Cancelled";
                            // Set Loading
                            upcomingList.get(position).setLoading(true);
                            reservationsAdapter.notifyItemChanged(position);
                            updateReservationStatus(resId, status, 2, position);
                        }
                    }
                }
            });
            alertBuilder.setPositiveButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });
            alertBuilder.create().show();
        }
    }

}
