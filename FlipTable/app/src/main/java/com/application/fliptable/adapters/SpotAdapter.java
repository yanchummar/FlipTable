package com.application.fliptable.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.MyViewHolder> {

    private ArrayList<Spot> restaurantList;
    private Spot currentRestaurant;
    private RestaurantUtils restaurantUtils;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, place, cuisines, openStatus, ratingText, cost, cuisinesTitle, costTitle, ratingBarText, nearbyDistanceTextView;
        ImageView spotImage, openStatusIcon, nearbyImageView;
        CardView ratingCard;
        LinearLayout verifiedSection;
        ImageView rating1, rating2, rating3, rating4, rating5;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.spot_item_name);
            cuisines = (TextView) view.findViewById(R.id.spot_item_cuisines);
            openStatus = (TextView) view.findViewById(R.id.spot_item_openstatus);
            ratingText = (TextView) view.findViewById(R.id.spot_item_ratingtextview);
            cost = (TextView) view.findViewById(R.id.spot_item_cost);
            spotImage = (ImageView) view.findViewById(R.id.spot_item_image);
            place = (TextView) view.findViewById(R.id.spot_item_place);
            ratingCard = (CardView) view.findViewById(R.id.spot_item_ratingcard);
            cuisinesTitle = (TextView) view.findViewById(R.id.spot_item_cuisines_title);
            costTitle = (TextView) view.findViewById(R.id.spot_item_cost_title);
            openStatusIcon = (ImageView) view.findViewById(R.id.openstatus_imageview_icon);
            verifiedSection = (LinearLayout) view.findViewById(R.id.verified_text_section);
            ratingBarText = (TextView) view.findViewById(R.id.spot_item_ratingbar_text);
            nearbyImageView = (ImageView) view.findViewById(R.id.nearby_imageview_icon);
            nearbyDistanceTextView = (TextView) view.findViewById(R.id.spot_item_nearby_distance);
            // RatingBar
            rating1 = (ImageView) view.findViewById(R.id.custom_star_1);
            rating2 = (ImageView) view.findViewById(R.id.custom_star_2);
            rating3 = (ImageView) view.findViewById(R.id.custom_star_3);
            rating4 = (ImageView) view.findViewById(R.id.custom_star_4);
            rating5 = (ImageView) view.findViewById(R.id.custom_star_5);
        }
    }


    public SpotAdapter(Context context, ArrayList<Spot> restaurantList, RestaurantUtils restaurantUtils) {
        this.restaurantList = restaurantList;
        this.restaurantUtils = restaurantUtils;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.spot_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Current Restaurant
        currentRestaurant = restaurantList.get(position);

        String openStatus = restaurantUtils.determineOpenStatus(currentRestaurant.getOpenStatus(), currentRestaurant.getOpeningTime(),currentRestaurant.getClosingTime());
        float rating = Float.parseFloat(currentRestaurant.getSpotRating());

        holder.name.setText(currentRestaurant.getSpotName());
        holder.place.setText(currentRestaurant.getSpotLocation());
        holder.cuisines.setText(currentRestaurant.getCuisines());
        holder.openStatus.setText(openStatus);
        holder.cost.setText(currentRestaurant.getCost());
        holder.ratingText.setText(String.valueOf(rating));
        Glide.with(holder.spotImage.getContext()).load(currentRestaurant.getSpotImageUrl()).into(holder.spotImage);
        holder.openStatus.setText(openStatus);
        holder.ratingBarText.setText(String.valueOf(rating) + " FlipStars");
        holder.nearbyImageView.setColorFilter(Color.parseColor("#757575"));
        holder.nearbyDistanceTextView.setText(currentRestaurant.getDistance());

        // OpenStatus color change
        int openStatusColor;
        if(openStatus.matches(CLOSED_NOW) || openStatus.matches(BOOKINGS_CLOSED) || openStatus.toLowerCase().contains("closed")){
            openStatusColor = context.getResources().getColor(R.color.brightRed);
            holder.openStatus.setTextColor(openStatusColor);
        } else if(openStatus.matches(CLOSING_SOON)){
            openStatusColor = context.getResources().getColor(android.R.color.holo_orange_dark);
            holder.openStatus.setTextColor(openStatusColor);
        } else {
            openStatusColor = context.getResources().getColor(R.color.brightGreen);
            holder.openStatus.setTextColor(openStatusColor);
        }
        holder.openStatusIcon.setColorFilter(openStatusColor);

        if (rating >= 4.5){
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_green_dark));
            setCustomRating(5, holder);
        }else if (rating >= 4){
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_green_light));
            setCustomRating(4, holder);
        }else if (rating >= 3){
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_orange_light));
            setCustomRating(3, holder);
        }else if (rating >= 2){
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_orange_dark));
            setCustomRating(2, holder);
        }else if (rating >= 1){
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_red_light));
            setCustomRating(1, holder);
        }else{
            holder.ratingCard.setCardBackgroundColor(holder.ratingCard.getContext().getResources().getColor(android.R.color.holo_red_light));
            setCustomRating(0, holder);
        }


    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public void setCustomRating(int stars, MyViewHolder holder){
        int color = context.getResources().getColor(R.color.colorAccent);
        int colorDefault = Color.parseColor("#bdbdbd");
        holder.rating1.setColorFilter(colorDefault);
        holder.rating2.setColorFilter(colorDefault);
        holder.rating3.setColorFilter(colorDefault);
        holder.rating4.setColorFilter(colorDefault);
        holder.rating5.setColorFilter(colorDefault);
        if (stars >= 1){
            holder.rating1.setColorFilter(color);
        }
        if (stars >= 2){
            holder.rating2.setColorFilter(color);
        }
        if (stars >= 3){
            holder.rating3.setColorFilter(color);
        }
        if (stars >= 4){
            holder.rating4.setColorFilter(color);
        }
        if (stars >= 5){
            holder.rating5.setColorFilter(color);
        }
    }

}
