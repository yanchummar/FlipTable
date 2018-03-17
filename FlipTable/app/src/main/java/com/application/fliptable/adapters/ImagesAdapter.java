package com.application.fliptable.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.application.fliptable.R;

/**
 * Created by yanchummar on 12/31/17.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.MyViewHolder> {

    private ArrayList<String> imagesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.image_item_image);
        }
    }

    public ImagesAdapter(ArrayList<String> imagesList) {
        this.imagesList = imagesList;
    }

    @Override
    public ImagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item_card, parent, false);

        return new ImagesAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ImagesAdapter.MyViewHolder holder, int position) {
        Glide.with(holder.imageView.getContext()).load(imagesList.get(position)).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imagesList.size();
    }

}
