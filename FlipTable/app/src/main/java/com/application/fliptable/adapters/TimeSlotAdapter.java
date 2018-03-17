package com.application.fliptable.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import com.application.fliptable.R;

/**
 * Created by yanchummar on 1/1/18.
 */

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder> {

    private ArrayList<String> timeSlots;
    private ArrayList<Boolean> timeSlotsSelected;
    private Activity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Button timeSlotButton;

        public MyViewHolder(View view) {
            super(view);
            timeSlotButton = (Button) view.findViewById(R.id.time_slot_item_button);
        }
    }

    public TimeSlotAdapter(Activity activity, ArrayList<String> timeSlots, ArrayList<Boolean> timeSlotsSelected) {
        this.activity = activity;
        this.timeSlots = timeSlots;
        this.timeSlotsSelected = timeSlotsSelected;
    }

    @Override
    public TimeSlotAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.time_slot_item, parent, false);

        return new TimeSlotAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TimeSlotAdapter.MyViewHolder holder, int position) {
        String currentTimeSlot = timeSlots.get(position);
        holder.timeSlotButton.setText(currentTimeSlot);

        Log.i("Adapter","TimeSlotsSelected.position:"+timeSlotsSelected.get(position));

        if (timeSlotsSelected.get(position)){
            Log.i("Adapter", "InTheTimeslotChecked");
            holder.timeSlotButton.setBackground(activity.getResources().getDrawable(R.drawable.button_bg_orange));
            holder.timeSlotButton.setTextColor(activity.getResources().getColor(android.R.color.white));
        }else{
            Log.i("Adapter", "InTheTimeslotNotCheck");
            holder.timeSlotButton.setBackground(activity.getResources().getDrawable(R.drawable.borderbutton_orange));
            holder.timeSlotButton.setTextColor(activity.getResources().getColor(android.R.color.holo_orange_dark));
        }
    }

    @Override
    public int getItemCount() {
        return timeSlots.size();
    }

}