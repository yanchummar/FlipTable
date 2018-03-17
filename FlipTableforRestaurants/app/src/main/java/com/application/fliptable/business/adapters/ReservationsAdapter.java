package com.application.fliptable.business.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.application.fliptable.business.models.Reservation;

import java.util.ArrayList;

import com.application.fliptable.business.R;

/**
 * Created by yanchummar on 12/21/17.
 */

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.MyViewHolder> {

    private ArrayList<Reservation> reservationList;
    private Reservation currentReservation;
    private int currentSection;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position, boolean cancel);
        void onMainContentClick(int position);
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView id, name, peepCount, date, time, status;
        LinearLayout acceptButton, declineButton, actionButtonBar, mainContent, loadingBar;
        TextView declineButtonText, acceptButttonText;
        ImageView acceptImageView, declineImageView;

        public MyViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.reservation_id_text);
            name = (TextView) view.findViewById(R.id.user_name_resact);
            peepCount = (TextView) view.findViewById(R.id.peep_count_resact);
            date = (TextView) view.findViewById(R.id.date_resact);
            time = (TextView) view.findViewById(R.id.time_resact);
            status = (TextView) view.findViewById(R.id.reservation_status);
            acceptButton = (LinearLayout) view.findViewById(R.id.accept_res_layout);
            declineButton = (LinearLayout) view.findViewById(R.id.decline_res_layout);
            declineButtonText = (TextView) view.findViewById(R.id.decline_textview);
            actionButtonBar = (LinearLayout) view.findViewById(R.id.action_section);
            mainContent = (LinearLayout) view.findViewById(R.id.main_content);
            loadingBar = (LinearLayout) view.findViewById(R.id.action_progress_bar);
            acceptButttonText = (TextView) view.findViewById(R.id.accept_textview);
            acceptImageView = (ImageView) view.findViewById(R.id.accept_imageview);
            declineImageView = (ImageView) view.findViewById(R.id.decline_imageview);
        }
    }


    public ReservationsAdapter(ArrayList<Reservation> reservationList, int currentSection, OnItemClickListener listener) {
        this.reservationList = reservationList;
        this.currentSection = currentSection;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reservation_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        currentReservation = reservationList.get(position);
        String peepCount;

        holder.id.setText("Reservation ID: " + currentReservation.getReservationId());
        holder.name.setText(currentReservation.getName());
        if (currentReservation.getFoodieCount() == 1){
            peepCount = currentReservation.getFoodieCount() + " Person";
        }else{
            peepCount = currentReservation.getFoodieCount() + " People";
        }
        holder.peepCount.setText(peepCount);
        holder.time.setText(currentReservation.getTimeSlot());
        // Date
        String dateFormatted = String.valueOf(DateFormat.format("EEE, d MMM yyyy", currentReservation.getBookingDate()));
        holder.date.setText(dateFormatted);

        // Res Status
        if (currentReservation.getStatus().toLowerCase().contains("pending")){
            holder.status.setTextColor(holder.status.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        } else if (currentReservation.getStatus().toLowerCase().contains("confirmed") || currentReservation.getStatus().toLowerCase().contains("seated")){
            holder.status.setTextColor(holder.status.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.status.setTextColor(holder.status.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
        holder.status.setText(currentReservation.getStatus());
        holder.declineImageView.setColorFilter(holder.declineImageView.getContext().getResources().getColor(android.R.color.holo_red_dark));
        holder.acceptImageView.setColorFilter(holder.acceptImageView.getContext().getResources().getColor(android.R.color.holo_green_dark));

        if (currentSection == 1){
            holder.acceptButttonText.setText("Arrived");
            holder.acceptImageView.setImageResource(R.drawable.ic_done_all_black_24dp);
            holder.declineButtonText.setText("Cancel");
        } else if (currentSection == 2) {
            holder.actionButtonBar.setVisibility(View.GONE);
        }

        // OnClick Listeners
        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position, false);
            }
        });
        holder.declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(position, true);
            }
        });
        holder.mainContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMainContentClick(position);
            }
        });
        // Loading
        if (currentReservation.isLoading()){
            holder.loadingBar.setVisibility(View.VISIBLE);
        } else {
            holder.loadingBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reservationList.size();
    }

}
