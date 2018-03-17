package com.application.fliptable.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.fliptable.R;
import com.application.fliptable.adapters.TimeSlotAdapter;
import com.application.fliptable.interfaces.RecyclerItemClickListener;
import com.application.fliptable.models.InternetTimeResponse;
import com.application.fliptable.models.Reservation;
import com.application.fliptable.models.Spot;
import com.application.fliptable.rest.ApiClient;
import com.application.fliptable.rest.ApiInterface;
import com.application.fliptable.utils.Config;
import com.application.fliptable.utils.InternetTime;
import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.application.fliptable.utils.RestaurantUtils.hideSoftKeyboard;

public class BookingActivity extends AppCompatActivity{

    private int foodieCount = 2;
    private int alreadySelectedIndex = -1;
    boolean freeBookingSelected = true, preBookingSelected = false;
    private int cost = 0, totalCost = 0;
    private int tipAmount = 0;
    private int currentSection = 0;
    private String selectedTimeSlot;
    private boolean timeSlotPicked = false;
    private boolean timeSlotToday = true;
    private Reservation currentBooking;
    private TimeSlotAdapter timeSlotAdapter;
    private ArrayList<String> timeSlots;
    private ArrayList<Boolean> timeSlotsSelected;
    private RecyclerView timeSlotRecyclerView;
    private String currentCity;

    private Calendar currentCalendar, bookedDateCalendar;
    private InternetTime internetTime;
    private String presentTime, validationTime;
    private Spot bookingSpot;
    private DatePickerDialog datePickerDialog;

    private TextView bookingCurrentPageTextView, currentDateTextView;
    private ImageView bookingPageTopImage;
    //End
    private LinearLayout bookingInfoSection, paymentInfoSection, errorLayout;
    private ShimmerLayout shimmerLayout;
    private ImageView errorImageView;
    private TextView errorMessageTextView;
    private CardView errorTryAgainButton;
    private View.OnClickListener normalTryAgainListener, validateTryAgainListener;
    // api
    private ApiInterface apiService;
    private SharedPreferences reservationSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        // Call the function callInstamojo to start payment here

        apiService = ApiClient.getClient().create(ApiInterface.class);

        // Shared Preferences
        reservationSharedPreferences = getSharedPreferences(Config.RESERVATION_SP, MODE_PRIVATE);

        // Getting Parceled Stuff
        bookingSpot = getIntent().getParcelableExtra("BOOKING_SPOT_PARCEL");
        currentCity = getIntent().getParcelableExtra("city_name_book");
        currentBooking = new Reservation();
        currentBooking.setSpotId(bookingSpot.getSpotId());
        currentBooking.setSpotName(bookingSpot.getSpotName());
        currentBooking.setSpotLocation(bookingSpot.getSpotLocation());

        ImageView closeButton = (ImageView) findViewById(R.id.close_booking_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookingActivity.super.onBackPressed();
            }
        });

        // Top Bar
        bookingCurrentPageTextView = (TextView) findViewById(R.id.booking_current_page_text);
        bookingPageTopImage = (ImageView) findViewById(R.id.booking_page_top_icon);
        final TextView restaurantNameTextView = (TextView) findViewById(R.id.booking_current_page_restuarant_name);
        restaurantNameTextView.setText(bookingSpot.getSpotName());
        bookingCurrentPageTextView.setText("Book a Table");
        Glide.with(this).load(bookingSpot.getSpotImageUrl()).into(bookingPageTopImage);
        //End
        bookingInfoSection = (LinearLayout) findViewById(R.id.booking_info_section);
        paymentInfoSection = (LinearLayout) findViewById(R.id.payment_info_section);
        errorLayout = (LinearLayout) findViewById(R.id.error_container);
        errorLayout.setVisibility(View.GONE);
        bookingInfoSection.setVisibility(View.GONE);
        paymentInfoSection.setVisibility(View.GONE);
        // Shimmer
        shimmerLayout = (ShimmerLayout) findViewById(R.id.shimmer_layout);
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();
        // Error Try Again
        errorImageView = (ImageView) findViewById(R.id.error_imageview);
        errorMessageTextView = (TextView) findViewById(R.id.error_textview);
        errorTryAgainButton = (CardView) findViewById(R.id.error_try_again_button);
        normalTryAgainListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                errorLayout.setVisibility(View.GONE);
                shimmerLayout.startShimmerAnimation();
                shimmerLayout.setVisibility(View.VISIBLE);
                bookingCurrentPageTextView.setText("Book a Table");
                Glide.with(BookingActivity.this).load(bookingSpot.getSpotImageUrl()).into(bookingPageTopImage);
                fetchTimeAndUpdateUI(false);
            }
        };
        validateTryAgainListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookingActivity.this, BookingActivity.class);
                intent.putExtra("BOOKING_SPOT_PARCEL", bookingSpot);
                startActivity(intent);
            }
        };
        errorTryAgainButton.setOnClickListener(normalTryAgainListener);

        /** Booking Info Section **/
        CardView addFoodie = (CardView) findViewById(R.id.add_foodie_button);
        CardView minusFoodie = (CardView) findViewById(R.id.minus_foodie_button);
        final TextView foodieCountTextView = (TextView) findViewById(R.id.foodie_count);
        final EditText nameEditText = (EditText) findViewById(R.id.input_name);
        final EditText emailEditText = (EditText) findViewById(R.id.input_email);
        final EditText phoneEditText = (EditText) findViewById(R.id.input_phone);
        final TextView costExplanationTextView = (TextView) findViewById(R.id.cost_explanation_text);
        final TextView costTextView = (TextView) findViewById(R.id.cost_textview);
        // Setting prefix on phone edittext
        phoneEditText.setText("+91 ");
        Selection.setSelection(phoneEditText.getText(), phoneEditText.getText().length());
        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(!s.toString().startsWith("+91 ")){
                    phoneEditText.setText("+91 ");
                    Selection.setSelection(phoneEditText.getText(), phoneEditText.getText().length());

                }

            }
        });

        addFoodie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodieCount < 10) { // TODO: Change max count
                    foodieCount++;
                    foodieCountTextView.setText(String.valueOf(foodieCount).concat(" Foodies"));
                    currentBooking.setFoodieCount(foodieCount);
                }
            }
        });
        minusFoodie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (foodieCount != 1) {
                    foodieCount--;
                    foodieCountTextView.setText(String.valueOf(foodieCount).concat(" Foodies"));
                }
                if (foodieCount == 1){
                    foodieCountTextView.setText(String.valueOf(foodieCount).concat(" Foodie"));
                }
                currentBooking.setFoodieCount(foodieCount);
            }
        });


        timeSlotRecyclerView = (RecyclerView) findViewById(R.id.time_slot_recyclerview);
        timeSlotRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        timeSlotRecyclerView.setItemAnimator(new DefaultItemAnimator());
        timeSlotRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                timeSlotPicked = true;
                selectedTimeSlot = timeSlots.get(position);
                boolean alreadySelected = timeSlotsSelected.contains(true);

                if (alreadySelected && alreadySelectedIndex != -1 && alreadySelectedIndex != position){
                    View alreadySelectedView = timeSlotRecyclerView.getLayoutManager().findViewByPosition(alreadySelectedIndex);
                    if (alreadySelectedView != null) {
                        Button oldSlotButton = (Button) alreadySelectedView.findViewById(R.id.time_slot_item_button);
                        oldSlotButton.setBackground(getResources().getDrawable(R.drawable.borderbutton_orange));
                        oldSlotButton.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                        timeSlotsSelected.set(alreadySelectedIndex, false);
                    }else{
                        timeSlotsSelected.set(alreadySelectedIndex, false);
                        timeSlotAdapter.notifyItemChanged(alreadySelectedIndex);
                    }
                }

                Button timeSlotButton = (Button) view.findViewById(R.id.time_slot_item_button);
                if (!timeSlotsSelected.get(position)) {
                    timeSlotButton.setBackground(getResources().getDrawable(R.drawable.button_bg_orange));
                    timeSlotButton.setTextColor(getResources().getColor(android.R.color.white));
                    timeSlotsSelected.set(position, true);
                    alreadySelectedIndex = position;
                }
                currentBooking.setTimeSlot(timeSlots.get(position));
                // Parsing timeSlot to millisecond
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    Date date = sdf.parse(timeSlots.get(position));
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    bookedDateCalendar.set(Calendar.HOUR_OF_DAY,cal.get(Calendar.HOUR_OF_DAY));
                    bookedDateCalendar.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
                    currentBooking.setBookingDate(bookedDateCalendar.getTimeInMillis());
                } catch (ParseException e){
                    e.printStackTrace();
                }

            }
        }));

        currentDateTextView = (TextView) findViewById(R.id.current_date_textview);
        String myFormat = "EEE, d MMM"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        if (currentCalendar != null) {
            currentDateTextView.setText(sdf.format(currentCalendar.getTime()));
        }

        Button pickDateButton = (Button) findViewById(R.id.pick_date_button);
        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datePickerDialog != null){
                    datePickerDialog.show();
                }
            }
        });

        // Next Button
        CardView bookingInfoCompleteButton = (CardView) findViewById(R.id.booking_info_complete_button);
        bookingInfoCompleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean infoCompleted = true;
                // Info Filled Checking
                if (nameEditText.getText().toString().trim().isEmpty()){
                    infoCompleted = false;
                    nameEditText.setError("Enter your Name.");
                }else{
                    currentBooking.setName(nameEditText.getText().toString());

                }
                if (emailEditText.getText().toString().trim().isEmpty()){
                    infoCompleted = false;
                    emailEditText.setError("Enter you Email");
                } else if (!(emailEditText.getText().toString().contains("@") || emailEditText.getText().toString().contains("."))){
                    infoCompleted = false;
                    emailEditText.setError("Enter a valid Email ID");
                } else {
                    currentBooking.setEmail(emailEditText.getText().toString());
                }
                if (phoneEditText.getText().toString().trim().isEmpty()){
                    infoCompleted = false;
                    phoneEditText.setError("Enter your Phone No.");
                } else if (phoneEditText.getText().toString().length() != 14){
                   infoCompleted = false;
                   phoneEditText.setError("Please enter a valid Phone No.");
                } else{
                    currentBooking.setPhone(phoneEditText.getText().toString().trim());
                }
                // TimeSlot etc Check
                if (timeSlotPicked){
                    currentBooking.setTimeSlot(selectedTimeSlot);
                    findViewById(R.id.time_slot_errortext).setVisibility(View.GONE);
                } else{
                    infoCompleted = false;
                    findViewById(R.id.time_slot_errortext).setVisibility(View.VISIBLE);
                }

                currentBooking.setFoodieCount(foodieCount);
                //currentBooking.setBookingDate();

                if (infoCompleted) {
                    paymentInfoSection.setVisibility(View.VISIBLE);
                    bookingInfoSection.setVisibility(View.GONE);
                    shimmerLayout.setVisibility(View.GONE);
                    errorLayout.setVisibility(View.GONE);
                    // Loading
                    (findViewById(R.id.validation_loading)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.main_loading)).setVisibility(View.GONE);
                    shimmerLayout.setVisibility(View.VISIBLE);
                    shimmerLayout.startShimmerAnimation();
                    paymentInfoSection.setVisibility(View.GONE);
                    bookingPageTopImage.setImageDrawable(getResources().getDrawable(R.drawable.loading));
                    bookingCurrentPageTextView.setText("Verifying...");
                    // Logical
                    if (timeSlotToday){
                        fetchTimeAndUpdateUI(true);
                    } else {
                        proceedToConfirmation();
                    }
                    currentSection = 1;
                }
                hideSoftKeyboard(BookingActivity.this, phoneEditText);
            }
        });

        /** END */

        /** Payment Options Section **/


        // Proceed Button
        CardView paymentProceedButton = (CardView) findViewById(R.id.payment_proceed_button);
        paymentProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Loading
                (findViewById(R.id.validation_loading)).setVisibility(View.VISIBLE);
                (findViewById(R.id.main_loading)).setVisibility(View.GONE);
                shimmerLayout.setVisibility(View.VISIBLE);
                shimmerLayout.startShimmerAnimation();
                paymentInfoSection.setVisibility(View.GONE);
                bookingPageTopImage.setImageDrawable(getResources().getDrawable(R.drawable.loading));
                bookingCurrentPageTextView.setText("Verifying...");
                // Logical
                if (timeSlotToday){
                    fetchTimeAndUpdateUI(true);
                } else {
                    proceedToConfirmation();
                }
            }
        });

        /** END */

        /** Start AsyncTask */
        // Internet Time from Api
        fetchTimeAndUpdateUI(false);
    }

    public ArrayList<String> getTimeSlots(String presentTime, String openingTime, String closingTime){
        timeSlotsSelected = new ArrayList<>();

        try {
            int startTime, endTime;
            SimpleDateFormat hrFormat = new SimpleDateFormat("H:mm", Locale.ENGLISH);
            SimpleDateFormat smallHrFormat = new SimpleDateFormat("Hmm", Locale.ENGLISH);
            Date dateObj = hrFormat.parse(openingTime);
            Date dateObjEnd = hrFormat.parse(closingTime);
            startTime = Integer.parseInt(smallHrFormat.format(dateObj));
            endTime = Integer.parseInt(smallHrFormat.format(dateObjEnd));
            Log.i("BookingActivity", "startTime and EndTime: " + startTime + " end: " + endTime);

            /** TimeSlots Generation **/

            ArrayList<String> timeSlotsList = new ArrayList<>();
            String timeSlotString;
            int i = 0;
            boolean condition;
            if (presentTime != null){
                i = Integer.parseInt(presentTime);
            }
            if (i<startTime || presentTime == null){
                i = startTime;
                condition = (i--)>=(startTime--);
            }else{
                condition = i>startTime;
            }
            // Check if closed
            if (endTime < 1000 && !(i > 2359)){
                endTime += 2400;
            }

            // No Slots Available Check
            int bookingsClosedDecisionTime = Integer.parseInt(String.valueOf(endTime).substring(2));
            if (bookingsClosedDecisionTime >= 20){
                bookingsClosedDecisionTime = endTime - 20;
            } else {
                bookingsClosedDecisionTime = endTime - 60;
            }

            if (i >= bookingsClosedDecisionTime){
                currentCalendar.add(Calendar.DATE, 1);
                currentDateTextView.setText("Tomorrow");
                i = startTime;
                condition = (i--)>=(startTime--);
            }else{
                currentDateTextView.setText("Today");
            }

            while (condition){
                int hoursInInt = Integer.parseInt(Integer.toString(i).substring(0, 2));
                int minutesInInt = Integer.parseInt(Integer.toString(i).substring(2));
                // Slot Generation
                if (minutesInInt >= 41){
                    hoursInInt++;
                    timeSlotString = String.valueOf(hoursInInt).concat("00");
                    i = Integer.parseInt(timeSlotString);
                } else if (minutesInInt >= 26){
                    timeSlotString = String.valueOf(hoursInInt).concat("45");
                    i = Integer.parseInt(timeSlotString);
                } else if (minutesInInt >= 11){
                    timeSlotString = String.valueOf(hoursInInt).concat("30");
                    i = Integer.parseInt(timeSlotString);
                }else{
                    timeSlotString = String.valueOf(hoursInInt).concat("15");
                    i = Integer.parseInt(timeSlotString);
                }

                String formattedTimeString = timeSlotString.substring(0, 2).concat(":").concat(timeSlotString.substring(2));

                SimpleDateFormat normalHrSdf = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
                Date timeObjToFormat = hrFormat.parse(formattedTimeString);
                String timeToAdd = normalHrSdf.format(timeObjToFormat);
                // EndTime check
                Date endTimeObj = hrFormat.parse(closingTime);
                String endTimeFormatted = normalHrSdf.format(endTimeObj);

                if (timeToAdd.matches(endTimeFormatted)){
                    break;
                } else{
                    // Adding
                    timeSlotsList.add(timeToAdd);
                    timeSlotsSelected.add(false);
                }
            }

            return timeSlotsList;

        } catch (ParseException e) {
            //Exception Handling
            Log.i("BA","parseErrorInTheplaceyouknow");
            return null;
        }

    }

    @Override
    public void onBackPressed() {
        if (currentSection == 1){
            paymentInfoSection.setVisibility(View.GONE);
            bookingInfoSection.setVisibility(View.VISIBLE);
            bookingCurrentPageTextView.setText("Book a Table");
            Glide.with(this).load(bookingSpot.getSpotImageUrl()).into(bookingPageTopImage);
            currentSection = 0;
        } else {
            super.onBackPressed();
        }
    }

    public DatePickerDialog generateDatePickerDialog(Calendar calendar){
        if (calendar != null) {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            bookedDateCalendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(BookingActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    bookedDateCalendar.setTimeInMillis(calendar.getTimeInMillis());
                    long milliseconds = calendar.getTimeInMillis();
                    Log.i("BookingActivity","timeInMillis:"+milliseconds);
                    String myFormat = "EEE, d MMM"; //In which you need put here
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
                    String currentTimeString = sdf.format(calendar.getTime());
                    Calendar currentCal = Calendar.getInstance();
                    currentCal.setTime(internetTime.getDate());
                    String todayTimeString = sdf.format(currentCal.getTime());
                    currentCal.add(Calendar.DATE, 1);
                    String tmrwTimeString = sdf.format(currentCal.getTime());
                    String displayTime;
                    if (currentTimeString.matches(todayTimeString)){
                        displayTime = "Today";
                        timeSlotToday = true;
                        timeSlots = getTimeSlots(presentTime, bookingSpot.getOpeningTime(), bookingSpot.getClosingTime());
                        timeSlotAdapter = new TimeSlotAdapter(BookingActivity.this, timeSlots, timeSlotsSelected);
                        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
                    } else if (currentTimeString.matches(tmrwTimeString)){
                        displayTime = "Tomorrow";
                        timeSlotToday = false;
                        timeSlots = getTimeSlots(null, bookingSpot.getOpeningTime(), bookingSpot.getClosingTime());
                        timeSlotAdapter = new TimeSlotAdapter(BookingActivity.this, timeSlots, timeSlotsSelected);
                        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
                    } else {
                        displayTime = currentTimeString;
                        timeSlotToday = false;
                        timeSlots = getTimeSlots(null, bookingSpot.getOpeningTime(), bookingSpot.getClosingTime());
                        timeSlotAdapter = new TimeSlotAdapter(BookingActivity.this, timeSlots, timeSlotsSelected);
                        timeSlotRecyclerView.setAdapter(timeSlotAdapter);
                    }
                    Log.i("BookingActivity", "stringsFormattedByDay:"+ todayTimeString+" tmrw:"+tmrwTimeString+" current:"+currentTimeString);
                    currentDateTextView.setText(displayTime);
                    currentBooking.setBookingDate(milliseconds);
                    timeSlotPicked = false;
                }
            }, year, month, day);
            Calendar dummyCalendar = Calendar.getInstance();
            datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
            Log.i("BookingActivity","currentCalendarBeforeAdding:"+currentCalendar.getTime());
            dummyCalendar.setTime(calendar.getTime());
            dummyCalendar.add(Calendar.DATE, 9);
            Log.i("BookingActivity","currentCalendarAfterAdding:"+currentCalendar.getTime());
            datePickerDialog.getDatePicker().setMaxDate(dummyCalendar.getTimeInMillis());
            datePickerDialog.updateDate(year, month, day);
            // Return
            return datePickerDialog;
        }else{
            return null;
        }
    }

    public void fetchTimeAndUpdateUI(final boolean validateTime){
        Call<InternetTimeResponse> call = apiService.getInternetTime();
        call.enqueue(new Callback<InternetTimeResponse>() {
            @Override
            public void onResponse(Call<InternetTimeResponse> call, Response<InternetTimeResponse> response) {
                internetTime = new InternetTime(response.body().getTime());
                // Got InternetTime
                if (internetTime.getMinute() < 10){
                    presentTime = String.valueOf(internetTime.getHour()).concat("0").concat(String.valueOf(internetTime.getMinute()));
                } else {
                    presentTime = String.valueOf(internetTime.getHour()).concat(String.valueOf(internetTime.getMinute()));
                }
                currentCalendar = Calendar.getInstance();
                currentCalendar.setTime(internetTime.getDate());
                if (validateTime){
                    timeSlots = getTimeSlots(presentTime, bookingSpot.getOpeningTime(), bookingSpot.getClosingTime());
                    if (!timeSlots.contains(currentBooking.getTimeSlot())){
                        errorLayout.setVisibility(View.VISIBLE);
                        shimmerLayout.setVisibility(View.GONE);
                        shimmerLayout.stopShimmerAnimation();
                        bookingPageTopImage.setImageDrawable(getResources().getDrawable(R.drawable.booking_error_icon));
                        bookingCurrentPageTextView.setText("Booking Error");
                        errorTryAgainButton.setOnClickListener(validateTryAgainListener);
                        errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.oh_snap));
                        errorMessageTextView.setText("The Time Slot you selected has been expired, please choose another one and try again.");
                    }else{
                        proceedToConfirmation();
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timeSlots = getTimeSlots(presentTime, bookingSpot.getOpeningTime(), bookingSpot.getClosingTime());
                            if (timeSlots != null) {
                                timeSlotAdapter = new TimeSlotAdapter(BookingActivity.this, timeSlots, timeSlotsSelected);
                                timeSlotRecyclerView.setAdapter(timeSlotAdapter);
                                shimmerLayout.setVisibility(View.GONE);
                                shimmerLayout.stopShimmerAnimation();
                                bookingInfoSection.setVisibility(View.VISIBLE);
                            }
                            currentBooking.setBookingDate(currentCalendar.getTimeInMillis());
                            datePickerDialog = generateDatePickerDialog(currentCalendar);
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<InternetTimeResponse> call, Throwable t) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                bookingPageTopImage.setImageDrawable(getResources().getDrawable(R.drawable.booking_error_icon));
                bookingCurrentPageTextView.setText("Booking Error");
                errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.offline_error));
                errorMessageTextView.setText("Oops! Some error connecting to our servers! Check your connection and Try Again.");
                errorLayout.setVisibility(View.VISIBLE);
                errorTryAgainButton.setOnClickListener(normalTryAgainListener);
            }
        });
    }

    private void changeStatusBarColor(){
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.success_blue));
        }
    }

    private void proceedToConfirmation(){
        currentSection = 2;
        // Reservation Obj
        currentBooking.setFreeBooking(freeBookingSelected);
        currentBooking.setTipAmount(tipAmount);
        currentBooking.setCost(totalCost);
        currentBooking.setStatus("Pending Confirmation");
        // Adding Reservation

        /* Add User */
        if (reservationSharedPreferences.getString(Config.RESERVATION_HASH_ID, null) == null) {
            Call<String> addUserCall = apiService.addUser();
            addUserCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        if (!response.body().contains("Error")) {
                            SharedPreferences.Editor editor = reservationSharedPreferences.edit();
                            editor.putString(Config.RESERVATION_HASH_ID, response.body());
                            editor.apply();
                            currentBooking.setHash(response.body());
                            addReservationToServer();
                        } else {
                            shimmerLayout.setVisibility(View.GONE);
                            shimmerLayout.stopShimmerAnimation();
                            bookingPageTopImage.setImageDrawable(getResources().getDrawable(R.drawable.booking_error_icon));
                            bookingCurrentPageTextView.setText("Booking Error");
                            errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.oh_snap));
                            errorMessageTextView.setText("Seems like a technical error! Try again later.");
                            errorLayout.setVisibility(View.VISIBLE);
                            errorTryAgainButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    errorLayout.setVisibility(View.GONE);
                                    paymentInfoSection.setVisibility(View.GONE);
                                    shimmerLayout.startShimmerAnimation();
                                    shimmerLayout.setVisibility(View.VISIBLE);
                                    proceedToConfirmation();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    t.printStackTrace();
                    shimmerLayout.setVisibility(View.GONE);
                    shimmerLayout.stopShimmerAnimation();
                    bookingPageTopImage.setImageDrawable(getResources().getDrawable(R.drawable.booking_error_icon));
                    bookingCurrentPageTextView.setText("Booking Error");
                    errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.offline_error));
                    errorMessageTextView.setText("You appear to be offline, please reconnect and try again.");
                    errorLayout.setVisibility(View.VISIBLE);
                    errorTryAgainButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            errorLayout.setVisibility(View.GONE);
                            paymentInfoSection.setVisibility(View.GONE);
                            shimmerLayout.startShimmerAnimation();
                            shimmerLayout.setVisibility(View.VISIBLE);
                            proceedToConfirmation();
                        }
                    });
                }
            });
        } else {
            currentBooking.setHash(reservationSharedPreferences.getString(Config.RESERVATION_HASH_ID, null));
            addReservationToServer();
        }
        /* End */

    }

    public void addReservationToServer(){
        Call<String> addReservationCall = apiService.addReservation(currentBooking.getHash(), currentBooking.getSpotId(),currentBooking.getSpotName(),currentBooking.getSpotLocation(),
                currentBooking.getName(),currentBooking.getEmail(),currentBooking.getPhone(),currentBooking.getFoodieCount(),currentBooking.getTimeSlot(),
                currentBooking.getBookingDate(),currentBooking.isFreeBooking(),currentBooking.getCost(),currentBooking.getTipAmount());
        addReservationCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call call, Response response) {
                String reservationId = response.body().toString();
                Log.i("BookingActivity","addReservation.php Responsed:"+reservationId);
                if (!response.isSuccessful() || response.body().toString().contains("Error")){
                    shimmerLayout.setVisibility(View.GONE);
                    shimmerLayout.stopShimmerAnimation();
                    bookingPageTopImage.setImageDrawable(getResources().getDrawable(R.drawable.booking_error_icon));
                    bookingCurrentPageTextView.setText("Booking Error");
                    errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.offline_error));
                    errorMessageTextView.setText("Some error occurred while making your reservation! Try Again.");
                    errorLayout.setVisibility(View.VISIBLE);
                    errorTryAgainButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            errorLayout.setVisibility(View.GONE);
                            shimmerLayout.startShimmerAnimation();
                            shimmerLayout.setVisibility(View.VISIBLE);
                            proceedToConfirmation();
                        }
                    });
                } else {
                    currentBooking.setReservationId(reservationId);
                    Intent intent = new Intent(BookingActivity.this, BookingDetail.class);
                    intent.putExtra(Config.RESERVATION_INFO_ITEM, currentBooking);
                    intent.putExtra(Config.RES_INFO_BACK_INTENT, 0);
                    intent.putExtra("city_name_fromres", currentCity);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                bookingPageTopImage.setImageDrawable(getResources().getDrawable(R.drawable.booking_error_icon));
                bookingCurrentPageTextView.setText("Booking Error");
                errorImageView.setImageDrawable(getResources().getDrawable(R.drawable.offline_error));
                errorMessageTextView.setText("You appear to be offline, please reconnect and try again.");
                errorLayout.setVisibility(View.VISIBLE);
                errorTryAgainButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        errorLayout.setVisibility(View.GONE);
                        paymentInfoSection.setVisibility(View.GONE);
                        shimmerLayout.startShimmerAnimation();
                        shimmerLayout.setVisibility(View.VISIBLE);
                        proceedToConfirmation();
                    }
                });
                t.printStackTrace();
                Log.e("BookingActivity","OnFailure");
            }
        });
    }

}
