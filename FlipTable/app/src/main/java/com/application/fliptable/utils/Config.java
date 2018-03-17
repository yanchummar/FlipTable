package com.application.fliptable.utils;

/**
 * Created by yanchummar on 3/6/18.
 */

public class Config {

    // Notification
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String SHARED_PREF_FIREBASE = "ah_firebase";

    // BookingDetail
    public static final String RESERVATION_INFO_ITEM = "reservation_info_reservation_item";
    public static final String RES_INFO_BACK_INTENT = "reservation_info_onbackpress_mode";

    // MainActivity
    public final static int REQUEST_LOCATION = 199;
    public final static String PARCELABLE_SPOT = "parcel_restaurant";
    // permission callback
    public static final int PERMISSION_CALLBACK_CONSTANT = 200;
    public static final int REQUEST_PERMISSION_SETTING = 201;
    public final static String PARCEL_RESERVATION_KEY = "reservation_to_parcel";

    // ReservationsActivity
    public static final String RESERVATION_SP = "reservations_shared_preferences";
    public static final String RESERVATION_HASH_ID = "reservation_user_id_sp";

}
