package com.application.fliptable.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.fliptable.models.ReservationResponse;

import com.application.fliptable.R;
import com.application.fliptable.models.Reservation;
import com.application.fliptable.rest.ApiClient;
import com.application.fliptable.rest.ApiInterface;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.application.fliptable.utils.Config.RESERVATION_INFO_ITEM;
import static com.application.fliptable.utils.Config.RES_INFO_BACK_INTENT;

public class BookingDetail extends AppCompatActivity {

    private TextView topNameTextView,topLocationTextView,resIdTextView,spotNameTextView,resDateTextView,resTimeTextView,resFoodieCount,resCost,resStatusText;
    private CircleImageView confirmationTaskImg,doneTaskImg,resStatusImg;
    private LinearLayout doneTaskBar, resStatusBar, errorLayout;
    private ImageView closeImageView;
    private LinearLayout progressBar, mainHolder;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Reservation currentReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_detail);

        currentReservation = getIntent().getExtras().getParcelable(RESERVATION_INFO_ITEM);
        final String currentCity = getIntent().getParcelableExtra("city_name_fromres");

        topNameTextView = (TextView) findViewById(R.id.top_spot_name);
        topLocationTextView = (TextView) findViewById(R.id.top_spot_location);
        resIdTextView = (TextView) findViewById(R.id.reservation_id_info);
        spotNameTextView = (TextView) findViewById(R.id.res_spot_name_info);
        resDateTextView = (TextView) findViewById(R.id.res_date_info);
        resTimeTextView = (TextView) findViewById(R.id.res_time_info);
        resFoodieCount = (TextView) findViewById(R.id.res_foodiecount_info);
        resCost = (TextView) findViewById(R.id.res_cost_info);
        confirmationTaskImg = (CircleImageView) findViewById(R.id.task_two_img);
        doneTaskImg = (CircleImageView) findViewById(R.id.task_three_img);
        doneTaskBar = (LinearLayout) findViewById(R.id.task_two_bar);
        closeImageView = (ImageView) findViewById(R.id.close_button_info);
        resStatusBar = (LinearLayout) findViewById(R.id.res_status_info_bar);
        resStatusImg = (CircleImageView) findViewById(R.id.res_status_info_img);
        resStatusText = (TextView) findViewById(R.id.res_status_info_text);
        final int backOption = getIntent().getExtras().getInt(RES_INFO_BACK_INTENT);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (backOption == 0){
                    Intent intent = new Intent(BookingDetail.this, MainActivity.class);
                    //intent.putExtra(MAIN_CITY_NAME_KEY, currentCity);
                    startActivity(intent);
                } else if (backOption == 1){
                    startActivity(new Intent(BookingDetail.this, ReservationsActivity.class));
                } else {
                    BookingDetail.super.onBackPressed();
                }
            }
        });

        // Error Layout
        mainHolder = (LinearLayout) findViewById(R.id.main_container_info);
        progressBar = (LinearLayout) findViewById(R.id.progress_bar_info);
        errorLayout = (LinearLayout) findViewById(R.id.error_container);
        CardView tryAgainButton = (CardView) findViewById(R.id.error_try_again_button);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                fetchDataAndUpdateUI();
            }
        });
        mainHolder.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDataAndUpdateUI();
            }
        });

        if (backOption == 1){
            fetchDataAndUpdateUI();
        } else {
            mainHolder.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            errorLayout.setVisibility(View.GONE);
            updateViewWithData();
        }
    }

    private void updateViewWithData(){
        resIdTextView.setText(currentReservation.getReservationId());
        topNameTextView.setText(currentReservation.getSpotName());
        topLocationTextView.setText(currentReservation.getSpotLocation());
        spotNameTextView.setText(currentReservation.getSpotName());
        resDateTextView.setText(DateFormat.format("dd MMMM yyyy", currentReservation.getBookingDate()));
        resTimeTextView.setText(currentReservation.getTimeSlot());
        if (currentReservation.getFoodieCount() == 1){
            resFoodieCount.setText("1 Person");
        } else {
            resFoodieCount.setText(currentReservation.getFoodieCount() + " People");
        }
        String cost;
        if (currentReservation.getCost() == 0){
            cost = "FREE";
        } else{
            cost = "₹"+currentReservation.getCost();
        }
        resCost.setText(cost);

        String reservationStatus = currentReservation.getStatus().toLowerCase();
        if (reservationStatus.contains("pending")){
            confirmationTaskImg.setImageResource(R.drawable.pending);
            doneTaskBar.setBackgroundColor(Color.parseColor("#aaaaaa"));
            doneTaskImg.setCircleBackgroundColor(Color.parseColor("#aaaaaa"));
            // Updating ResBar
            resStatusBar.setBackground(getResources().getDrawable(R.drawable.borderinfo_orange));
            resStatusImg.setImageResource(R.drawable.pending);
            resStatusText.setText(currentReservation.getStatus());
            resStatusText.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
        } else if (reservationStatus.contains("confirmed")){
            doneTaskImg.setImageResource(R.drawable.done);
            confirmationTaskImg.setImageResource(R.drawable.done);
            doneTaskBar.setBackgroundColor(getResources().getColor(R.color.success_blue));
            // Updating ResBar
            resStatusBar.setBackground(getResources().getDrawable(R.drawable.borderinfo_blue));
            resStatusImg.setImageResource(R.drawable.done);
            resStatusText.setText(currentReservation.getStatus());
            resStatusText.setTextColor(getResources().getColor(R.color.success_blue));
        } else {
            confirmationTaskImg.setImageResource(R.drawable.cancelled);
            // Updating ResBar
            resStatusBar.setBackground(getResources().getDrawable(R.drawable.borderinfo_red));
            resStatusImg.setImageResource(R.drawable.cancelled);
            resStatusText.setText("Sorry! The Restaurant was unable to accomodate your request");
            resStatusText.setTextColor(getResources().getColor(R.color.error_red_info));
        }
    }

    public void fetchDataAndUpdateUI(){
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        retrofit2.Call<ReservationResponse> call = apiService.getReservationById(Integer.parseInt(currentReservation.getReservationId()));
        call.enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call, Response<ReservationResponse> response) {
                if (response.body() != null) {
                    currentReservation = response.body().getReservationList().get(0);
                    updateViewWithData();
                    mainHolder.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable t) {
                errorLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                mainHolder.setVisibility(View.GONE);
            }
        });
    }

}
