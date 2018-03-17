package com.application.fliptable.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.application.fliptable.utils.Config;

import com.application.fliptable.R;
import com.application.fliptable.models.Reservation;

public class ReceiptActivity extends AppCompatActivity {

    Reservation currentReservation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        currentReservation = getIntent().getParcelableExtra(Config.PARCEL_RESERVATION_KEY);

        // Init
        TextView spotNameTextView = (TextView) findViewById(R.id.res_name_rec);
        TextView spotLocationTextView = (TextView) findViewById(R.id.res_location_rec);
        TextView dateTextView = (TextView) findViewById(R.id.res_date_rec);
        TextView timeTextView = (TextView) findViewById(R.id.res_time_rec);
        TextView bookingTypeTextView = (TextView) findViewById(R.id.res_type_rec);
        TextView foodieCountTextView = (TextView) findViewById(R.id.res_people_rec);
        TextView costTextView = (TextView) findViewById(R.id.res_cost_rec);
        TextView yourNameTextView = (TextView) findViewById(R.id.your_name_rec);
        TextView yourEmailTextView = (TextView) findViewById(R.id.your_email_rec);
        TextView yourPhoneTextView = (TextView) findViewById(R.id.your_phone_rec);
        TextView resIdTop = (TextView) findViewById(R.id.res_id_top_rec);
        TextView mainResIdTextView = (TextView) findViewById(R.id.reservation_id_textview);
        ImageView qrCodeImageView = (ImageView) findViewById(R.id.qr_code_receipt);
        ImageView closeButton = (ImageView) findViewById(R.id.close_button);
        Button callButton = (Button) findViewById(R.id.res_call_rec);

        // Setting Values and Listeners
        spotNameTextView.setText(currentReservation.getSpotName());
        spotLocationTextView.setText(currentReservation.getSpotLocation());
        dateTextView.setText(String.valueOf(DateFormat.format("EEE, dd MMM yyyy", currentReservation.getBookingDate())));
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
        foodieCountTextView.setText(foodieCount);
        String cost;
        if (currentReservation.getCost() == 0){
            cost = "FREE";
        } else{
            cost = "₹"+currentReservation.getCost();
        }
        costTextView.setText(cost);
        yourNameTextView.setText(currentReservation.getName());
        yourEmailTextView.setText(currentReservation.getEmail());
        yourPhoneTextView.setText(String.valueOf(currentReservation.getPhone()));
        mainResIdTextView.setText(currentReservation.getReservationId());
        resIdTop.setText(("#").concat(currentReservation.getReservationId()));
        // QR code
        Bitmap qrBitmap = net.glxn.qrgen.android.QRCode.from(currentReservation.getReservationId()).bitmap();
        qrCodeImageView.setImageBitmap(qrBitmap);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiptActivity.super.onBackPressed();
            }
        });
    }
}
