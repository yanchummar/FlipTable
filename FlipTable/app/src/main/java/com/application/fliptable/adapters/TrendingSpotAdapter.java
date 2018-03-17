package com.application.fliptable.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.application.fliptable.models.Spot;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.application.fliptable.R;

import com.application.fliptable.utils.RestaurantUtils;

import static com.application.fliptable.utils.RestaurantUtils.BOOKINGS_CLOSED;
import static com.application.fliptable.utils.RestaurantUtils.CLOSED_NOW;
import static com.application.fliptable.utils.RestaurantUtils.CLOSING_SOON;

/**
 * Created by yanchummar on 12/21/17.
 */

public class TrendingSpotAdapter extends RecyclerView.Adapter<TrendingSpotAdapter.MyViewHolder> {

    private ArrayList<Spot> trendingSpotList;
    private Spot currentSpot;
    private RestaurantUtils restaurantUtils;
    private int startTime, endTime;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, cuisines, openStatus, rating, priceLevel;
        ImageView spotImage;
        RatingBar ratingBar;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.trendingspot_item_name);
            cuisines = (TextView) view.findViewById(R.id.trendingspot_item_cuisines);
            openStatus = (TextView) view.findViewById(R.id.trendingspot_item_openstatus);
            rating = (TextView) view.findViewById(R.id.trendingspot_item_ratingtext);
            spotImage = (ImageView) view.findViewById(R.id.trendingspot_item_image);
            ratingBar = (RatingBar) view.findViewById(R.id.trendingspot_item_ratingbar);
            priceLevel = (TextView) view.findViewById(R.id.trendingspot_item_pricelevel);
        }
    }


    public TrendingSpotAdapter(ArrayList<Spot> trendingSpotList, RestaurantUtils restaurantUtils) {
        this.trendingSpotList = trendingSpotList;
        this.restaurantUtils = restaurantUtils;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trending_spot_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        currentSpot = trendingSpotList.get(position);

        String priceLevel = "";
        String openStatus = restaurantUtils.determineOpenStatus(currentSpot.getOpenStatus(), currentSpot.getOpeningTime(),currentSpot.getClosingTime());
        float rating = Float.parseFloat(currentSpot.getSpotRating());

        holder.name.setText(currentSpot.getSpotName());
        holder.cuisines.setText(currentSpot.getCuisines());
        holder.rating.setText(currentSpot.getSpotRating());
        holder.ratingBar.setRating(rating);
        holder.openStatus.setText(openStatus);
        Glide.with(holder.spotImage.getContext()).load(currentSpot.getSpotImageUrl()).into(holder.spotImage);

        Log.i("TSAdapter","currentSpot.getPriceLevel:"+currentSpot.getPriceLevel());
        for (int i = 0; i < currentSpot.getPriceLevel(); i++) {
            priceLevel = priceLevel.concat("₹");
        }
        if (priceLevel.length() < 7) {
            holder.priceLevel.setText(priceLevel);
        }

        if(openStatus.matches(CLOSED_NOW) || openStatus.matches(BOOKINGS_CLOSED) || openStatus.toLowerCase().contains("closed")){
            holder.openStatus.setTextColor(holder.openStatus.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }else if(openStatus.matches(CLOSING_SOON)){
            holder.openStatus.setTextColor(holder.openStatus.getContext().getResources().getColor(android.R.color.holo_orange_dark));
        }

    }

    @Override
    public int getItemCount() {
        return trendingSpotList.size();
    }

}
