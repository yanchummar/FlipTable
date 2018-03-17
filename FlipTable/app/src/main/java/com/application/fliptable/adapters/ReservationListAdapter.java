package com.application.fliptable.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.application.fliptable.R;
import com.application.fliptable.models.Reservation;

/**
 * Created by yanchummar on 12/21/17.
 */

public class ReservationListAdapter extends RecyclerView.Adapter<ReservationListAdapter.MyViewHolder> {

    private ArrayList<Reservation> reservationList;
    private Reservation currentReservation;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, peepCount, date, time, reservationStatus;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.restaurant_name_resact);
            peepCount = (TextView) view.findViewById(R.id.peep_count_resact);
            date = (TextView) view.findViewById(R.id.date_resact);
            time = (TextView) view.findViewById(R.id.time_resact);
            reservationStatus = (TextView) view.findViewById(R.id.status_resact);
        }
    }


    public ReservationListAdapter(ArrayList<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reservation_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        currentReservation = reservationList.get(position);
        String peepCount;

        holder.name.setText(currentReservation.getSpotName());
        if (currentReservation.getFoodieCount() == 1){
            peepCount = currentReservation.getFoodieCount() + " Person";
        }else{
            peepCount = currentReservation.getFoodieCount() + " People";
        }
        holder.peepCount.setText(peepCount);
        holder.time.setText(currentReservation.getTimeSlot());
        holder.reservationStatus.setText(currentReservation.getStatus());
        Context resStatContext = holder.reservationStatus.getContext();
        if (currentReservation.getStatus().toLowerCase().contains("pending")){
            holder.reservationStatus.setTextColor(resStatContext.getResources().getColor(android.R.color.holo_orange_dark));
        } else if (currentReservation.getStatus().toLowerCase().contains("confirmed") || currentReservation.getStatus().toLowerCase().contains("seated")){
            holder.reservationStatus.setTextColor(resStatContext.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.reservationStatus.setTextColor(resStatContext.getResources().getColor(android.R.color.holo_red_dark));
        }
        // Date
        String dateFormatted = String.valueOf(DateFormat.format("EEE, d MMM yyyy", currentReservation.getBookingDate()));
        holder.date.setText(dateFormatted);
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

}
