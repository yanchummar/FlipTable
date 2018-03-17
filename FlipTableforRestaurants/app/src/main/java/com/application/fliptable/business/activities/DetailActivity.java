package com.application.fliptable.business.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.fliptable.business.R;
import com.application.fliptable.business.models.Reservation;

public class DetailActivity extends AppCompatActivity {

    private Reservation currentReservation;
    public static final String PARCELABLE_CURRENT_RES = "parcelable_current_reservation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        currentReservation = getIntent().getParcelableExtra(PARCELABLE_CURRENT_RES);

        // Initializing Views
        TextView topResId = findViewById(R.id.res_id_top_rec);
        TextView mainResId = findViewById(R.id.reservation_id_textview);
        TextView nameTextView = findViewById(R.id.your_name_rec);
        TextView emailTextView = findViewById(R.id.your_email_rec);
        TextView phoneTextView = findViewById(R.id.your_phone_rec);
        TextView dateTextView = findViewById(R.id.res_date_rec);
        TextView timeTextView = findViewById(R.id.res_time_rec);
        TextView peepCountTextView = findViewById(R.id.res_people_rec);
        TextView bookingTypeTextView = findViewById(R.id.res_type_rec);
        TextView amountPaidTextView = findViewById(R.id.res_cost_rec);
        Button callButton = findViewById(R.id.user_call_rec);
        ImageView closeButton = findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.super.onBackPressed();
            }
        });

        topResId.setText("#"+currentReservation.getReservationId());
        mainResId.setText(currentReservation.getReservationId());
        nameTextView.setText(currentReservation.getName());
        emailTextView.setText(currentReservation.getEmail());
        phoneTextView.setText(currentReservation.getPhone());
        dateTextView.setText(DateFormat.format("EEE, dd MMM yyyy",currentReservation.getBookingDate()));
        timeTextView.setText(currentReservation.getTimeSlot());
        String bookingType;
        if (currentReservation.isFreeBooking()){
            bookingType = "Free Booking";
        } else {
            bookingType = "Pre-Booking";
        }
        bookingTypeTextView.setText(bookingType);
        String foodieCount;
        if (currentReservation.getFoodieCount() == 1){
            foodieCount = "1 Person";
        } else {
            foodieCount = currentReservation.getFoodieCount() + " People";
        }
        peepCountTextView.setText(foodieCount);
        String cost;
        if (currentReservation.getCost() == 0){
            cost = "FREE";
        } else{
            cost = "₹"+currentReservation.getCost();
        }
        amountPaidTextView.setText(cost);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialPhoneNumber(currentReservation.getPhone());
            }
        });

    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

}
