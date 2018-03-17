package com.application.fliptable.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.application.fliptable.R;
import com.application.fliptable.adapters.ImagesAdapter;
import com.application.fliptable.adapters.MenuListRecyclerAdapter;
import com.application.fliptable.fragments.PhotoViewFragment;
import com.application.fliptable.interfaces.RecyclerItemClickListener;
import com.application.fliptable.models.InternetTimeResponse;
import com.application.fliptable.models.Menu;
import com.application.fliptable.models.Spot;
import com.application.fliptable.rest.ApiClient;
import com.application.fliptable.rest.ApiInterface;
import com.application.fliptable.utils.Config;
import com.application.fliptable.utils.RestaurantUtils;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantActivity extends AppCompatActivity {

    private Spot activeSpot;
    private String openingHour, closingHour;
    private LinearLayout reserveButton;
    private TextView reserveButtonText, openStatusTextView, workingHoursTextView;
    private Toolbar toolbar;
    private RestaurantUtils restaurantUtils;
    private int currentSection = 0;
    private String directionString;

    private ShimmerLayout shimmerLayout;
    private LinearLayout errorLayout;
    private NestedScrollView mainScrollView;
    private ImageView errorImageView;
    private TextView errorMessageTextView;
    // PhotoView
    private RelativeLayout photoViewSection;
    private ViewPager photoViewPager;
    private PhotoViewPagerAdapter photoViewPagerAdapter;
    // Menu
    private ArrayList<Menu> orderList = new ArrayList<>();
    MenuListRecyclerAdapter.OnItemClickListener menuOnItemClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        // Up Navigation
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().hide();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantActivity.super.onBackPressed();
            }
        });

        activeSpot = getIntent().getParcelableExtra(Config.PARCELABLE_SPOT);
        final String currentCity = getIntent().getExtras().getString("city_name");

        // Shimmer and other layouts
        getSupportActionBar().show();
        toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
        mainScrollView = (NestedScrollView) findViewById(R.id.restaurant_nested_scrollview);
        mainScrollView.setVisibility(View.GONE);
        shimmerLayout = (ShimmerLayout) findViewById(R.id.shimmer_layout_restaurant_activity);
        shimmerLayout.startShimmerAnimation();
        shimmerLayout.setVisibility(View.VISIBLE);
        errorLayout = (LinearLayout) findViewById(R.id.error_container);
        errorLayout.setVisibility(View.GONE);
        errorImageView = (ImageView) findViewById(R.id.error_imageview);
        errorMessageTextView = (TextView) findViewById(R.id.error_textview);
        CardView tryAgainButton = (CardView) findViewById(R.id.error_try_again_button);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                shimmerLayout.startShimmerAnimation();
                shimmerLayout.setVisibility(View.VISIBLE);
                mainScrollView.setVisibility(View.GONE);
                getSupportActionBar().show();
                toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
                fetchTimeAndUpdateUI();
            }
        });

        // PhotoView
        photoViewSection = (RelativeLayout) findViewById(R.id.photo_view_section);
        photoViewPager = (ViewPager) findViewById(R.id.image_view_pager);
        ImageView photoViewCloseButton = (ImageView) findViewById(R.id.photo_view_section_close_button);
        photoViewSection.setVisibility(View.GONE);
        photoViewPagerAdapter = new PhotoViewPagerAdapter(getSupportFragmentManager(), activeSpot.getPhotosList());
        photoViewCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoViewSection.setVisibility(View.GONE);
                currentSection = 0;
            }
        });

        // Initializing elements
        TextView spotNameTextView = (TextView) findViewById(R.id.restaurant_page_name);
        TextView spotLocationTextView = (TextView) findViewById(R.id.restaurant_page_location);
        RatingBar spotRatingBar = (RatingBar) findViewById(R.id.restaurant_page_ratingbar);
        TextView ratingTextView = (TextView) findViewById(R.id.restaurant_page_ratingtext);
        workingHoursTextView = (TextView) findViewById(R.id.restaurant_page_hours);
        openStatusTextView = (TextView) findViewById(R.id.restaurant_page_openstatus);
        TextView costTextView = (TextView) findViewById(R.id.restaurant_page_price);
        TextView cuisinesTextView = (TextView) findViewById(R.id.restaurant_page_cuisines);
        ImageView spotImageView = (ImageView) findViewById(R.id.restaurant_page_image);
        reserveButton = (LinearLayout) findViewById(R.id.call_to_action_button);
        reserveButtonText = (TextView) findViewById(R.id.call_to_action_button_textview);
        ImageView upNavCustomButtton = (ImageView) findViewById(R.id.up_custom_button);
        upNavCustomButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RestaurantActivity.super.onBackPressed();
            }
        });
        reserveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantActivity.this, BookingActivity.class);
                intent.putExtra("BOOKING_SPOT_PARCEL", activeSpot);
                intent.putExtra("city_name_book", currentCity);
                startActivity(intent);
            }
        });

        // Call & Direction Button
        LinearLayout callButton = (LinearLayout) findViewById(R.id.call_button_resact);
        LinearLayout directionButton = (LinearLayout) findViewById(R.id.direction_button_resact);
        directionString = activeSpot.getSpotName().concat(", ").concat(activeSpot.getAddress());
        directionString = directionString.replaceAll(" ","+");
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askPermissionToCall();
            }
        });
        directionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+directionString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });

        // Scrolling Listener
        mainScrollView.setOnScrollChangeListener(scrollChangeListener);

        // Updating text and images accordingly
        getSupportActionBar().setTitle(activeSpot.getSpotName());
        spotNameTextView.setText(activeSpot.getSpotName());
        spotLocationTextView.setText(activeSpot.getSpotLocation());
        spotRatingBar.setRating(Float.parseFloat(activeSpot.getSpotRating()));
        ratingTextView.setText(activeSpot.getSpotRating().concat(" FlipStars"));
        costTextView.setText(activeSpot.getCost());
        cuisinesTextView.setText(activeSpot.getCuisines());
        Glide.with(this).load(activeSpot.getSpotImageUrl()).into(spotImageView);

        // Images Section
        Log.i("ResActivity", "imagesList:" + activeSpot.getPhotosList());
        ImagesAdapter imagesAdapter = new ImagesAdapter(activeSpot.getPhotosList());
        RecyclerView imagesRecyclerView = (RecyclerView) findViewById(R.id.images_recycler_view);
        imagesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        imagesRecyclerView.setAdapter(imagesAdapter);
        imagesRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                photoViewSection.setVisibility(View.VISIBLE);
                photoViewPager.setAdapter(photoViewPagerAdapter);
                photoViewPager.setCurrentItem(position);
                currentSection = 1;
            }
        }));

        // Amenities Section
        if (activeSpot.getAmenitiesList().isEmpty()){
            (findViewById(R.id.amenities_section)).setVisibility(View.GONE);
        } else {
            AmenitiesAdapter amenitiesAdapter = new AmenitiesAdapter(activeSpot.getAmenitiesList());
            RecyclerView amenitiesRecyclerView = (RecyclerView) findViewById(R.id.amenities_recycler_view);
            amenitiesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            amenitiesRecyclerView.setItemAnimator(new DefaultItemAnimator());
            amenitiesRecyclerView.setAdapter(amenitiesAdapter);
        }

        // Set Rating
        CardView ratingCard = (CardView) findViewById(R.id.rating_card_res);
        ((TextView)findViewById(R.id.rating_card_text_res)).setText(activeSpot.getSpotRating());
        float rating = Float.parseFloat(activeSpot.getSpotRating());
        if (rating >= 4.5){
            ratingCard.setCardBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        }else if (rating >= 4){
            ratingCard.setCardBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }else if (rating >= 3){
            ratingCard.setCardBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        }else if (rating >= 2){
            ratingCard.setCardBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        }else if (rating >= 1){
            ratingCard.setCardBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }else{
            ratingCard.setCardBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }

        /** Menu Setup **/

        final LinearLayout menuSection = findViewById(R.id.res_menu_section);
        final LinearLayout infoSection = findViewById(R.id.res_info_section);
        final TextView subTotalTextView = findViewById(R.id.subtotal_textview_res);
        final TextView cgstTextView = findViewById(R.id.cgst_textview_res);
        final TextView sgstTextView = findViewById(R.id.sgst_textview_res);
        final TextView totalAmountTextView = findViewById(R.id.total_amount_textview_res);
        infoSection.setVisibility(View.GONE);

        // TabLayout
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addTab(tabLayout.newTab().setText("Menu"));
        tabLayout.addTab(tabLayout.newTab().setText("Info"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        menuSection.setVisibility(View.VISIBLE);
                        infoSection.setVisibility(View.GONE);
                        break;
                    case 1:
                        infoSection.setVisibility(View.VISIBLE);
                        menuSection.setVisibility(View.GONE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        menuOnItemClickListener = new MenuListRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onOrderChanged(Menu changedItem) {
                for (int i=0;i<orderList.size();i++){
                    Menu menuItem = orderList.get(i);
                    if (menuItem.getItemId() == changedItem.getItemId()){
                        if (changedItem.getOrderCount() == 0){
                            orderList.remove(i);
                        } else {
                            orderList.set(i, changedItem);
                        }
                    } else {
                        orderList.add(changedItem);
                    }
                }
                // Updating bill amount
                double subTotal = 0;
                double taxAmount;
                double totalAmount;
                for (int i=0;i<orderList.size();i++){
                    subTotal += orderList.get(i).getItemCost();
                }
                Log.i("ResAct", "subTotalAmount:"+orderList.size());
                // Assigning values
                taxAmount = (2.5/100)*subTotal;
                totalAmount = subTotal + taxAmount;
                // Updating UI
                subTotalTextView.setText(("₹").concat(String.valueOf(RestaurantUtils.round(subTotal, 2))));
                cgstTextView.setText(("₹").concat(String.valueOf(RestaurantUtils.round(taxAmount, 2))));
                sgstTextView.setText(("₹").concat(String.valueOf(RestaurantUtils.round(taxAmount, 2))));
                totalAmountTextView.setText(("₹").concat(String.valueOf(RestaurantUtils.round(totalAmount, 2))));

            }
        };

        final ArrayList<Menu> menuList = new ArrayList<>();
        menuList.add(new Menu(0,true,"Pepper Sandwich Veg", 250, "Pizza with fried pepper barbeque as a demo text chicken as toppings on the crust."));
        menuList.add(new Menu(1,false,"Pepper Barbeque Chicken", 250, "Pizza with fried pepper barbeque as a demo text chicken as toppings on the crust."));
        menuList.add(new Menu(2,false,"Pepper Barbeque Chicken", 250, "Pizza with fried pepper barbeque as a demo text chicken as toppings on the crust."));
        menuList.add(new Menu(3,true,"Pepper Veg Sandwich", 250, "Pizza with fried pepper barbeque as a demo text chicken as toppings on the crust."));
        menuList.add(new Menu(4,true,"Pepper Sandwich Veg", 250, "Pizza with fried pepper barbeque as a demo text chicken as toppings on the crust."));

        SectionedRecyclerViewAdapter sectionsAdapter = new SectionedRecyclerViewAdapter();
        sectionsAdapter.addSection(new MenuSection("Bestsellers", menuList));
        sectionsAdapter.addSection(new MenuSection("Desserts", menuList));
        sectionsAdapter.notifyDataSetChanged();

        RecyclerView menuRecyclerView = findViewById(R.id.menu_main_recyclerview);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        menuRecyclerView.setItemAnimator(new DefaultItemAnimator());
        menuRecyclerView.setAdapter(sectionsAdapter);

        /** Start Async Tasks */
        // Get InternetTime
        fetchTimeAndUpdateUI();

    }

    public String getWorkingHours() {
        // Working Hours
        try {
            SimpleDateFormat hrFormat = new SimpleDateFormat("H:mm", Locale.ENGLISH);
            SimpleDateFormat smallHrFormat = new SimpleDateFormat("h:mm:a", Locale.ENGLISH);
            Date startObj = hrFormat.parse(activeSpot.getOpeningTime());
            Date endObj = hrFormat.parse(activeSpot.getClosingTime());
            openingHour = smallHrFormat.format(startObj);
            closingHour = smallHrFormat.format(endObj);
        } catch (ParseException e) {
            // TODO Handle Exception
        }

        return RestaurantUtils.simplifyTime(openingHour).concat(" to ").concat(RestaurantUtils.simplifyTime(closingHour));
    }

    public String simplifyOpenStatus(String openStatus, TextView openStatusButton) {
        if (openStatus.matches(RestaurantUtils.OPEN_NOW) || openStatus.matches(RestaurantUtils.OPENING_SOON)) {
            openStatusButton.setTextColor(getResources().getColor(R.color.brightGreen));
            if (openStatus.matches(RestaurantUtils.OPEN_NOW)) {
                return "Open Now";
            } else {
                return openStatus;
            }
        } else if (openStatus.matches(RestaurantUtils.CLOSING_SOON)) {
            openStatusButton.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            return openStatus;
        } else {
            openStatusButton.setTextColor(getResources().getColor(R.color.brightRed));
            reserveButtonText.setText("Book For Later");
            return openStatus;
        }
    }

    NestedScrollView.OnScrollChangeListener scrollChangeListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if (scrollY <= 60) {
                toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
            } else {
                getSupportActionBar().show();
                toolbar.animate().translationY(0).setInterpolator(new DecelerateInterpolator()).start();
            }
        }
    };

    public void fetchTimeAndUpdateUI() {
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<InternetTimeResponse> call = apiService.getInternetTime();
        call.enqueue(new Callback<InternetTimeResponse>() {
            @Override
            public void onResponse(Call<InternetTimeResponse> call, Response<InternetTimeResponse> response) {
                restaurantUtils = new RestaurantUtils(response.body().getTime());
                // Got InternetTime
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String openStatus = restaurantUtils.determineOpenStatus(activeSpot.getOpenStatus(),
                                activeSpot.getOpeningTime(), activeSpot.getClosingTime());
                        openStatusTextView.setText(simplifyOpenStatus(openStatus, openStatusTextView));
                        // Closed determination
                        if (openStatus.toLowerCase().contains("closed on")){
                            workingHoursTextView.setText("Closed");
                        } else {
                            workingHoursTextView.setText(getWorkingHours());
                        }
                        shimmerLayout.setVisibility(View.GONE);
                        shimmerLayout.stopShimmerAnimation();
                        errorLayout.setVisibility(View.GONE);
                        mainScrollView.setVisibility(View.VISIBLE);
                        toolbar.animate().translationY(-toolbar.getBottom()).setInterpolator(new AccelerateInterpolator()).start();
                    }
                });
            }

            @Override
            public void onFailure(Call<InternetTimeResponse> call, Throwable t) {
                shimmerLayout.stopShimmerAnimation();
                shimmerLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (currentSection == 0) {
            super.onBackPressed();
        } else {
            photoViewSection.setVisibility(View.GONE);
        }
    }

    /* PhotoView Adapter */
    private class PhotoViewPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<String> imageList;

        public PhotoViewPagerAdapter(FragmentManager fm, ArrayList<String> imageList) {
            super(fm);
            this.imageList = imageList;
        }

        @Override
        public Fragment getItem(int position) {
            return PhotoViewFragment.newInstance(imageList.get(position));
        }

        @Override
        public int getCount() {
            return imageList.size();
        }
    }

    public void callPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(intent);
        }
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    /** Permission **/

    public void askPermissionToCall(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CALL_PHONE)) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        169);

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        169);
            }
        } else {
            callPhoneNumber(activeSpot.getPhone());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 169: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    callPhoneNumber(activeSpot.getPhone());

                } else {
                    dialPhoneNumber(activeSpot.getPhone());
                }
                return;
            }
        }
    }



    /** RecyclerView Adapter **/

    public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.MyViewHolder> {

        private ArrayList<String> amenitiesList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            ImageView doneImageView;
            TextView amenityTextView;

            public MyViewHolder(View view) {
                super(view);
                doneImageView = (ImageView) view.findViewById(R.id.done_imageview);
                amenityTextView = (TextView) view.findViewById(R.id.amenity_label_textview);
            }
        }

        public AmenitiesAdapter(ArrayList<String> amenitiesList) {
            this.amenitiesList = amenitiesList;
        }

        @Override
        public AmenitiesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.amenities_item, parent, false);

            return new AmenitiesAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final AmenitiesAdapter.MyViewHolder holder, int position) {
            holder.doneImageView.setColorFilter(holder.doneImageView.getContext().getResources().getColor(android.R.color.holo_green_dark));

            String amenityString = amenitiesList.get(position);
            if (amenityString.length() > 24){
                amenityString = (amenityString.substring(0, 20)).concat("...");
            }
            if (amenityString.startsWith(" ")){
                amenityString = amenityString.substring(1);
            }
            holder.amenityTextView.setText(amenityString);
        }

        @Override
        public int getItemCount() {
            return amenitiesList.size();
        }

    }

    /** Menu Sections **/

    private class MenuSection extends StatelessSection {

        String sectionName;
        ArrayList<Menu> menuList;

        MenuSection(String sectionName, ArrayList<Menu> menuList) {
            super(new SectionParameters.Builder(R.layout.menu_section_item_layout)
                    .headerResourceId(R.layout.menu_section_header_layout)
                    .build());

            this.sectionName = sectionName;
            this.menuList = menuList;
        }

        @Override
        public int getContentItemsTotal() {
            return 1;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new MyItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            Log.i("NF","In newsAdapter! HahahaBlah");
            final MyItemViewHolder itemHolder = (MyItemViewHolder) holder;

            MenuListRecyclerAdapter menuListRecyclerAdapter = new MenuListRecyclerAdapter(menuList, menuOnItemClickListener);
            itemHolder.menuListRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            itemHolder.menuListRecyclerView.setItemAnimator(new DefaultItemAnimator());
            itemHolder.menuListRecyclerView.setAdapter(menuListRecyclerAdapter);
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.sectionNameTextView.setText(sectionName);
            headerHolder.sectionItemCountTextView.setText(String.valueOf(menuList.size()).concat(" Items"));
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView sectionNameTextView, sectionItemCountTextView;

        HeaderViewHolder(View view) {
            super(view);
            sectionNameTextView = view.findViewById(R.id.section_name_textview);
            sectionItemCountTextView = view.findViewById(R.id.section_item_count_textview);
        }
    }

    private class MyItemViewHolder extends RecyclerView.ViewHolder {

        private final RecyclerView menuListRecyclerView;

        MyItemViewHolder(View view) {
            super(view);
            menuListRecyclerView = view.findViewById(R.id.menu_section_recyclerview);
        }
    }

}
