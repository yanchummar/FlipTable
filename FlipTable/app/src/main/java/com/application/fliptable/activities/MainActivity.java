package com.application.fliptable.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.application.fliptable.models.SpotResponse;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.application.fliptable.R;
import com.application.fliptable.adapters.SpotAdapter;
import com.application.fliptable.adapters.TimeSlotAdapter;
import com.application.fliptable.adapters.TrendingSpotAdapter;
import com.application.fliptable.interfaces.RecyclerItemClickListener;
import com.application.fliptable.models.Spot;
import com.application.fliptable.rest.ApiClient;
import com.application.fliptable.rest.ApiInterface;
import com.application.fliptable.utils.Config;
import com.application.fliptable.utils.NotificationUtils;
import com.application.fliptable.utils.RestaurantUtils;
import io.fabric.sdk.android.Fabric;
import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.application.fliptable.utils.Config.PARCELABLE_SPOT;
import static com.application.fliptable.utils.Config.PERMISSION_CALLBACK_CONSTANT;
import static com.application.fliptable.utils.Config.REQUEST_LOCATION;
import static com.application.fliptable.utils.Config.REQUEST_PERMISSION_SETTING;
import static com.application.fliptable.utils.Config.RESERVATION_HASH_ID;
import static com.application.fliptable.utils.Config.RESERVATION_SP;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, android.location.LocationListener {

    private RecyclerView trendingRecyclerView, restaurantReyclerView;
    private ArrayList<Spot> trendingSpotList, restaurantList, filteredList;
    private TrendingSpotAdapter trendingSpotAdapter;
    private SpotAdapter spotAdapter;
    private RestaurantUtils restaurantUtils;
    private ArrayList<Boolean> cuisineFilterSelected, priceLevelFilterSelected;
    private ArrayList<String> cuisineFilter, priceLevelFilter;
    private boolean filterApplied = false;
    private TimeSlotAdapter cuisineAdapter, priceLevelAdapter;

    // Location-related
    private TextView locationTextView;
    private LocationManager locationManager;
    private String provider;
    private String place;
    private Location location;
    private LocationRequest locationRequest;
    private LocationSettingsRequest.Builder locationSettingsRequest;
    private PendingResult<LocationSettingsResult> pendingResult;

    private GoogleApiClient mGoogleApiClient;
    static String currentCity;
    static double latitude, longitude;

    // Runtime Permissions
    String[] permissionsRequired = new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION};
    private SharedPreferences permissionStatus;

    private ShimmerLayout shimmerLayout;
    private LinearLayout mainContentHolder, errorLayout, trendingContentHolder;
    private ImageView errorImageView;
    private TextView errorMessageTextView;
    private EditText searchEditText;
    private CardView errorTryAgainButton;

    // SharedPreferences
    SharedPreferences sharedPreferences;

    // Api
    private ApiInterface apiService;
    private FirebaseAnalytics mFirebaseAnalytics;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        if (currentCity == null){
            currentCity = "Pick a Location";
        }
        if (currentCity.matches("Pick a Location")){
            latitude = -1;
            longitude = -1;
        }

        /* Notification */
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
        Log.e("MainActivity", "FCM token: "+FirebaseInstanceId.getInstance().getToken());
        /* Notification */

        apiService = ApiClient.getClient().create(ApiInterface.class);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        locationTextView = (TextView) findViewById(R.id.location_textview);

        // sharedPreferences
        sharedPreferences = getSharedPreferences(RESERVATION_SP, MODE_PRIVATE);
        /* Add User */
        boolean tempBool = false;
        if (sharedPreferences.getString(RESERVATION_HASH_ID, null) != null) {
            tempBool = sharedPreferences.getString(RESERVATION_HASH_ID, null).length() < 4;
        }
        if (sharedPreferences.getString(RESERVATION_HASH_ID, null) == null || tempBool) {
            Call<String> addUserCall = apiService.addUser();
            addUserCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        if (!response.body().contains("Error")) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(RESERVATION_HASH_ID, response.body());
                            editor.apply();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        /* End */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Filter Lists
        final ArrayList<String> cuisineList = new ArrayList<>(), priceLevelList = new ArrayList<>();
        cuisineList.add("American");cuisineList.add("Italian");cuisineList.add("Continental");cuisineList.add("Spicy");
        priceLevelList.add("₹");priceLevelList.add("₹₹");priceLevelList.add("₹₹₹");priceLevelList.add("₹₹₹₹");

        // Shimmer & Holder
        trendingContentHolder = (LinearLayout) findViewById(R.id.trending_container);
        mainContentHolder = (LinearLayout) findViewById(R.id.main_content_holder);
        shimmerLayout = (ShimmerLayout) findViewById(R.id.shimmer_layout_mainactivity);
        shimmerLayout.startShimmerAnimation();
        shimmerLayout.setVisibility(View.VISIBLE);
        mainContentHolder.setVisibility(View.GONE);
        // Error
        errorLayout = (LinearLayout) findViewById(R.id.error_container);
        errorImageView = (ImageView) findViewById(R.id.error_imageview);
        errorMessageTextView = (TextView) findViewById(R.id.error_textview);
        errorTryAgainButton = (CardView) findViewById(R.id.error_try_again_button);
        errorLayout.setVisibility(View.GONE);
        errorTryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                errorImageView.setVisibility(View.VISIBLE);
                shimmerLayout.startShimmerAnimation();
                shimmerLayout.setVisibility(View.VISIBLE);
                Log.i("MainActivity","currentCityOnReloadClick:"+currentCity);
                fetchData(String.valueOf(latitude), String.valueOf(longitude));
            }
        });

        trendingRecyclerView = (RecyclerView) findViewById(R.id.trendingspot_recyclerview);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        trendingRecyclerView.setLayoutManager(layoutManager);
        trendingRecyclerView.setItemAnimator(new DefaultItemAnimator());
        trendingRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent restaurantIntent = new Intent(MainActivity.this, RestaurantActivity.class);
                restaurantIntent.putExtra(PARCELABLE_SPOT, trendingSpotList.get(position));
                restaurantIntent.putExtra("city_name",currentCity);
                startActivity(restaurantIntent);
            }
        }));

        restaurantReyclerView = (RecyclerView) findViewById(R.id.restaurants_recyclerview);
        LinearLayoutManager layoutManagerRestaurants = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
        restaurantReyclerView.setLayoutManager(layoutManagerRestaurants);
        restaurantReyclerView.setItemAnimator(new DefaultItemAnimator());
        restaurantReyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent restaurantIntent = new Intent(MainActivity.this, RestaurantActivity.class);
                restaurantIntent.putExtra(PARCELABLE_SPOT, restaurantList.get(position));
                restaurantIntent.putExtra("city_name",currentCity);
                startActivity(restaurantIntent);
            }
        }));

        /* Search */

        searchEditText = (EditText) findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String searchQuery = s.toString();
                if (!searchQuery.trim().isEmpty()) {
                    if (restaurantList != null) {
                        ArrayList<Spot> resultList = new ArrayList<>();
                        ArrayList<Spot> listToSearch;
                        if (filterApplied){
                            listToSearch = filteredList;
                        } else {
                            listToSearch = restaurantList;
                        }
                        for (int i = 0; i < listToSearch.size(); i++) {
                            if (listToSearch.get(i).getSpotName().toLowerCase().contains(searchQuery.toLowerCase())) {
                                resultList.add(listToSearch.get(i));
                            }
                        }
                        if (resultList.isEmpty()){
                            shimmerLayout.setVisibility(View.GONE);
                            shimmerLayout.stopShimmerAnimation();
                            mainContentHolder.setVisibility(View.GONE);
                            errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.oh_snap));
                            errorMessageTextView.setText("No restaurants found for your search query. Try a different one!");
                            errorLayout.setVisibility(View.VISIBLE);
                            errorImageView.setVisibility(View.GONE);
                            errorTryAgainButton.setVisibility(View.GONE);
                        } else {
                            errorLayout.setVisibility(View.GONE);
                            mainContentHolder.setVisibility(View.VISIBLE);
                        }
                        SpotAdapter resultAdapter = new SpotAdapter(MainActivity.this, resultList, restaurantUtils);
                        restaurantReyclerView.setAdapter(resultAdapter);
                        trendingContentHolder.setVisibility(View.GONE);
                    } else {
                        searchEditText.getText().clear();
                    }
                } else {
                    if (restaurantList != null) {
                        if (filterApplied) {
                            errorLayout.setVisibility(View.GONE);
                            SpotAdapter spotAdapter = new SpotAdapter(MainActivity.this, filteredList, restaurantUtils);
                            restaurantReyclerView.setAdapter(spotAdapter);
                            mainContentHolder.setVisibility(View.VISIBLE);
                        } else {
                            errorLayout.setVisibility(View.GONE);
                            SpotAdapter spotAdapter = new SpotAdapter(MainActivity.this, restaurantList, restaurantUtils);
                            restaurantReyclerView.setAdapter(spotAdapter);
                            mainContentHolder.setVisibility(View.VISIBLE);
                            trendingContentHolder.setVisibility(View.VISIBLE);
                        }
                        if (restaurantList.isEmpty()){
                            errorLayout.setVisibility(View.VISIBLE);
                            mainContentHolder.setVisibility(View.GONE);
                        }
                    } else {
                        searchEditText.getText().clear();
                    }
                }
            }
        });

        /* Filter */

        final DialogPlus filterDialog = DialogPlus.newDialog(this)
                .setContentHolder(new ViewHolder(R.layout.filter_dialog_layout))
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()) {
                            case R.id.filter_done_button:
                                Log.i("MainActivity","cuisineListFilter:"+cuisineFilter);
                                Log.i("MainActivity","priceLevelListFilter:"+priceLevelFilter);
                                if (cuisineFilter.isEmpty() && priceLevelFilter.isEmpty()){
                                    SpotAdapter spotAdapter = new SpotAdapter(MainActivity.this, restaurantList, restaurantUtils);
                                    setRestaurantRecyclerViewAdapter(spotAdapter, false);
                                    filterApplied = false;
                                } else {
                                    if (restaurantList != null) {
                                        final ArrayList<Spot> resultList = new ArrayList<>();
                                        for (int i = 0; i < restaurantList.size(); i++) {
                                            boolean matchesCuisine = false, matchesPriceLevel = false;
                                            for (int j = 0; j < cuisineFilter.size(); j++) {
                                                if (restaurantList.get(i).getCuisines().contains(cuisineFilter.get(j))) {
                                                    matchesCuisine = true;
                                                }
                                            }
                                            for (int k = 0; k < priceLevelFilter.size(); k++) {
                                                if (restaurantList.get(i).getPriceLevel() == priceLevelFilter.get(k).length()) {
                                                    matchesPriceLevel = true;
                                                }
                                            }
                                            if (cuisineFilter.isEmpty()){
                                                matchesCuisine = true;
                                            } else if (priceLevelFilter.isEmpty()){
                                                matchesPriceLevel = true;
                                            }
                                            // Filtering
                                            if (matchesCuisine && matchesPriceLevel) {
                                                resultList.add(restaurantList.get(i));
                                            }
                                        }
                                        filteredList = resultList;
                                        if (!resultList.isEmpty()) {
                                            SpotAdapter filteredSpotAdapter = new SpotAdapter(MainActivity.this, resultList, restaurantUtils);
                                            setRestaurantRecyclerViewAdapter(filteredSpotAdapter, true);
                                            filterApplied = true;
                                        } else {
                                            SpotAdapter mainAdapter = new SpotAdapter(MainActivity.this, restaurantList, restaurantUtils);
                                            setRestaurantRecyclerViewAdapter(mainAdapter, false);
                                            Toast.makeText(MainActivity.this, "No restaurants found for this filter", Toast.LENGTH_LONG).show();
                                            filterApplied = false;
                                        }
                                        if (restaurantList.isEmpty()){
                                            errorLayout.setVisibility(View.VISIBLE);
                                            mainContentHolder.setVisibility(View.GONE);
                                        }else {
                                            errorLayout.setVisibility(View.GONE);
                                            mainContentHolder.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                                dialog.dismiss();
                        }
                    }
                })
                .setExpanded(false)
                .create();

        // View Holder Setup
        cuisineFilterSelected = new ArrayList<>();
        priceLevelFilterSelected = new ArrayList<>();
        cuisineFilter = new ArrayList<>();priceLevelFilter = new ArrayList<>();
        View filterView = filterDialog.getHolderView();
        final RecyclerView cuisineRecyclerView = (RecyclerView) filterView.findViewById(R.id.cuisine_filter_recyclerview); // TODO: Fix error
        final RecyclerView priceLevelRecyclerView = (RecyclerView) filterView.findViewById(R.id.pricelevel_filter_recyclerview);
        cuisineRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        cuisineRecyclerView.setItemAnimator(new DefaultItemAnimator());
        priceLevelRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        priceLevelRecyclerView.setItemAnimator(new DefaultItemAnimator());

        for (int i=0;i<cuisineList.size();i++){
            cuisineFilterSelected.add(false);
        }
        for (int i=0;i<priceLevelList.size();i++){
            priceLevelFilterSelected.add(false);
        }
        cuisineAdapter = new TimeSlotAdapter(MainActivity.this, cuisineList, cuisineFilterSelected);
        priceLevelAdapter = new TimeSlotAdapter(MainActivity.this, priceLevelList, priceLevelFilterSelected);
        cuisineRecyclerView.setAdapter(cuisineAdapter);
        priceLevelRecyclerView.setAdapter(priceLevelAdapter);
        // ItemClick Listener
        cuisineRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (cuisineFilterSelected.get(position)){
                    cuisineFilterSelected.set(position, false);
                    for (int i=0;i<cuisineFilter.size();i++){
                        if (cuisineFilter.get(i).matches(cuisineList.get(position))){
                            cuisineFilter.remove(i);
                        }
                    }
                    //Log.i("Adapter","CuisineSelected.position:"+cuisineFilterSelected.get(position));
                    cuisineAdapter.notifyDataSetChanged();
                } else {
                    cuisineFilterSelected.set(position, true);
                    Log.i("Adapter","CuisineSelected.position:"+cuisineFilterSelected.get(position));
                    cuisineFilter.add(cuisineList.get(position));
                    cuisineAdapter = new TimeSlotAdapter(MainActivity.this, cuisineList, cuisineFilterSelected);
                    cuisineRecyclerView.setAdapter(cuisineAdapter);
                }
            }
        }));
        priceLevelRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (priceLevelFilterSelected.get(position)){
                    priceLevelFilterSelected.set(position, false);
                    for (int i=0;i<priceLevelFilter.size();i++){
                        if (priceLevelFilter.get(i).matches(priceLevelList.get(position))){
                            priceLevelFilter.remove(i);
                        }
                    }
                    priceLevelAdapter.notifyDataSetChanged();
                } else {
                    priceLevelFilterSelected.set(position, true);
                    priceLevelFilter.add(priceLevelList.get(position));
                    priceLevelAdapter = new TimeSlotAdapter(MainActivity.this, priceLevelList, priceLevelFilterSelected);
                    priceLevelRecyclerView.setAdapter(priceLevelAdapter);
                }
            }
        }));

        CardView filterButton = (CardView) findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDialog.show();
            }
        });


        /** Location **/

        // Runtime Permissions
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);
        askRequiredPermissions();

        if (place != null){
            locationTextView.setText(place);
        } else {
            locationTextView.setText(currentCity);
        }
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                .build();
        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i("MainActivity", "Place: " + place.getName());
                currentCity = place.getName().toString();
                locationTextView.setText(currentCity);
                locationManager.removeUpdates(MainActivity.this);
                // Filter and Search Reset
                searchEditText.getText().clear();
                cuisineFilter = new ArrayList<>();
                priceLevelFilter = new ArrayList<>();
                cuisineFilterSelected = new ArrayList<>();
                priceLevelFilterSelected = new ArrayList<>();
                for (int i=0;i<cuisineList.size();i++){
                    cuisineFilterSelected.add(false);
                }
                for (int i=0;i<priceLevelList.size();i++){
                    priceLevelFilterSelected.add(false);
                }
                // Logging event
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "selected_city");
                bundle.putString(FirebaseAnalytics.Param.VALUE, currentCity);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                // Assigning latlong
                latitude = place.getLatLng().latitude;
                longitude = place.getLatLng().longitude;
                fetchData(String.valueOf(latitude), String.valueOf(longitude));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MainActivity", "An error occurred: " + status);
            }
        });

        /** Starting Async Tasks */
        // Getting Data and Time
        if (latitude != -1 & longitude != -1){
            fetchData(String.valueOf(latitude), String.valueOf(longitude));
        } else {
            shimmerLayout.setVisibility(View.GONE);
            shimmerLayout.stopShimmerAnimation();
            mainContentHolder.setVisibility(View.GONE);
            errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.oh_snap));
            errorMessageTextView.setText("Please select a Location to discover restaurants around you!");
            errorLayout.setVisibility(View.VISIBLE);
            errorImageView.setVisibility(View.VISIBLE);
            errorTryAgainButton.setVisibility(View.GONE);
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

    /*@Override
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
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_reservations) {
            startActivity(new Intent(this, ReservationsActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /** Permissions */

    public void askRequiredPermissions() {
        if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])) {
                //Request Permissions
                ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
                enableLocation();
            }
            getLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
                    enableLocation();
                }
                getLocation();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(this, permissionsRequired[1])) {
                // Request Permissions
                ActivityCompat.requestPermissions(MainActivity.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && hasGPSDevice(this)) {
                    enableLocation();
                }
                getLocation();
            }
        }

        if (requestCode == REQUEST_LOCATION){
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    getLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Toast.makeText(this, "Please turn on Location Services to detect your current location", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }

    /** End **/

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if (currentCity == null){
            geodecodeCity(latitude, longitude);
        } else {
            if (currentCity.matches("Pick a Location")) {
                geodecodeCity(latitude, longitude);
            }
        }

        Log.i("MainActivity","locationLastKnown:"+location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    public void getLocation() {
        if (!(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            String provider = locationManager.getBestProvider(new Criteria(), false);
            location = locationManager.getLastKnownLocation(provider);
            Log.i("MainActivity","isLocationNullingetLocation:"+location);
            if (location != null && currentCity == null){
                geodecodeCity(location.getLatitude(), location.getLongitude());
            }else {
                if (location != null && currentCity.matches("Pick a Location")) {
                    geodecodeCity(location.getLatitude(), location.getLongitude());
                }
            }
        }
    }

    public String geodecodeCity(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder();
                place = address.getLocality();
                currentCity = place;
                if (locationTextView != null) {
                    locationTextView.setText(currentCity);
                    // Logging Event
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "selected_city");
                    bundle.putString(FirebaseAnalytics.Param.VALUE, currentCity);
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    // assigning latlong
                    this.latitude = latitude;
                    this.longitude = longitude;
                    fetchData(String.valueOf(this.latitude), String.valueOf(this.longitude));
                }
                locationManager.removeUpdates(this);
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Unable connect to Geocoder", e);
            //geodecodeCity(latitude,longitude);
        }
        return null;
    }

    /* Enabling Location */

    private boolean hasGPSDevice(Context context) {
        final LocationManager mgr = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        if (mgr == null)
            return false;
        final List<String> providers = mgr.getAllProviders();
        if (providers == null)
            return false;
        return providers.contains(LocationManager.GPS_PROVIDER);
    }

    public void enableLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {}
                    @Override
                    public void onConnectionSuspended(int i) {}
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}
                })
                .build();
        mGoogleApiClient.connect();
        mLocationSetting();
    }

    public void mLocationSetting() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(1 * 1000);
        locationRequest.setFastestInterval(1 * 1000);

        locationSettingsRequest = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        mResult();

    }

    public void mResult() {
        pendingResult = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequest.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                Status status = locationSettingsResult.getStatus();


                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {

                            status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {

                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.


                        break;
                }
            }

        });
    }

    public void setRestaurantRecyclerViewAdapter(SpotAdapter spotAdapter, boolean hide){
        searchEditText.getText().clear();
        restaurantReyclerView.setAdapter(spotAdapter);
        if (hide) {
            trendingContentHolder.setVisibility(View.GONE);
        } else {
            trendingContentHolder.setVisibility(View.VISIBLE);
        }
    }

    /** Fetching Spots From Server **/

    private void fetchData(String lat, String lng){
        mainContentHolder.setVisibility(View.GONE);
        shimmerLayout.startShimmerAnimation();
        shimmerLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        Call<SpotResponse> call = apiService.getNearbySpots(lat, lng);
        call.enqueue(new Callback<SpotResponse>() {
            @Override
            public void onResponse(Call<SpotResponse> call, Response<SpotResponse> response) {
                if (response.isSuccessful()) {
                    restaurantList = new ArrayList<>();
                    trendingSpotList = new ArrayList<>();
                    restaurantList = response.body().getSpotList();
                    String dateToFormat = response.body().getTime();
                    restaurantUtils = new RestaurantUtils(dateToFormat);
                    // Trending Spots
                    if (restaurantList != null) {
                        for (int i = 0; i < restaurantList.size(); i++) {
                            if (restaurantList.get(i).isTrending()) {
                                trendingSpotList.add(restaurantList.get(i));
                            }
                        }
                        trendingSpotAdapter = new TrendingSpotAdapter(trendingSpotList, restaurantUtils);
                        spotAdapter = new SpotAdapter(MainActivity.this, restaurantList, restaurantUtils);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                trendingRecyclerView.setAdapter(trendingSpotAdapter);
                                restaurantReyclerView.setAdapter(spotAdapter);
                                shimmerLayout.setVisibility(View.GONE);
                                shimmerLayout.stopShimmerAnimation();
                                errorLayout.setVisibility(View.GONE);
                                mainContentHolder.setVisibility(View.VISIBLE);
                            }
                        });

                        // Error Validation
                        if (restaurantList.isEmpty()) {
                            shimmerLayout.setVisibility(View.GONE);
                            shimmerLayout.stopShimmerAnimation();
                            mainContentHolder.setVisibility(View.GONE);
                            errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.oh_snap));
                            errorMessageTextView.setText("No restaurants found in this location, check back soon.");
                            errorLayout.setVisibility(View.VISIBLE);
                            errorImageView.setVisibility(View.VISIBLE);
                            errorTryAgainButton.setVisibility(View.GONE);
                        }

                    } else {
                        shimmerLayout.setVisibility(View.GONE);
                        shimmerLayout.stopShimmerAnimation();
                        mainContentHolder.setVisibility(View.GONE);
                        errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.oh_snap));
                        errorMessageTextView.setText("No restaurants found in this location, check back soon.");
                        errorLayout.setVisibility(View.VISIBLE);
                        errorImageView.setVisibility(View.VISIBLE);
                        errorTryAgainButton.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onFailure(Call<SpotResponse> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        shimmerLayout.setVisibility(View.GONE);
                        shimmerLayout.stopShimmerAnimation();
                        mainContentHolder.setVisibility(View.GONE);
                        errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.offline_error));
                        errorMessageTextView.setText("You appear to be offline, please reconnect and try again.");
                        errorLayout.setVisibility(View.VISIBLE);
                        errorImageView.setVisibility(View.VISIBLE);
                        errorTryAgainButton.setVisibility(View.VISIBLE);
                    }
                });
                Log.e("MainActivity","Error Fetchin Spots from DO:" + t);
            }
        });
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF_FIREBASE, 0);
        String regId = pref.getString("regId", null);

        Log.e("MainActivity", "Firebase reg id: " + regId);
    }

    /** LifeCycle functions **/

    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (!(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            Log.i("MainActivity","isPlaceNullorNot:"+place);
            String provider = locationManager.getBestProvider(new Criteria(),false);
            if (place == null) {
                locationManager.requestLocationUpdates(provider, 400, 1, this);
            }
        }

        /* Notifications */

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        // Location
        locationManager.removeUpdates(this);
        super.onPause();
    }

}