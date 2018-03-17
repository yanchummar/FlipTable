package com.application.fliptable.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.application.fliptable.R;
import com.application.fliptable.models.Menu;

/**
 * Created by yanchummar on 3/11/18.
 */

public class MenuListRecyclerAdapter extends RecyclerView.Adapter<MenuListRecyclerAdapter.MyViewHolder> {

    public interface OnItemClickListener {
        void onOrderChanged(Menu ChangedItem);
    }
    private final OnItemClickListener listener;

    private ArrayList<Menu> menuList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView menuItemNameTextView, menuItemCostTextView, menuItemDescriptionTextView, orderCountTextView;
        ImageView menuItemDietSign, menuAddIcon, menuMinusIcon;
        LinearLayout menuItemPlusButton, menuItemMinusButton;
        CardView menuItemAddCard;

        public MyViewHolder(View view) {
            super(view);
            menuItemNameTextView = view.findViewById(R.id.menu_item_name_textview);
            menuItemCostTextView = view.findViewById(R.id.menu_item_cost_textview);
            menuItemDescriptionTextView = view.findViewById(R.id.menu_item_description_textview);
            menuItemDietSign = view.findViewById(R.id.menu_item_diet_icon_imageview);
            menuItemAddCard = view.findViewById(R.id.menu_item_add_button);
            menuItemPlusButton = view.findViewById(R.id.menu_item_plus_button);
            menuItemMinusButton = view.findViewById(R.id.menu_item_minus_button);
            menuAddIcon = view.findViewById(R.id.menu_add_icon);
            menuMinusIcon = view.findViewById(R.id.menu_minus_icon);
            orderCountTextView = view.findViewById(R.id.menu_list_order_count);
        }
    }


    public MenuListRecyclerAdapter(ArrayList<Menu> menuList, OnItemClickListener listener) {
        this.menuList = menuList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Menu currentMenuItem = menuList.get(position);
        // Setting values
        holder.menuItemNameTextView.setText(currentMenuItem.getItemName());
        holder.menuItemCostTextView.setText(("₹").concat(String.valueOf(currentMenuItem.getItemCost())));
        holder.menuItemDescriptionTextView.setText(currentMenuItem.getItemDescription());
        if (currentMenuItem.isVeg()){
            holder.menuItemDietSign.setImageResource(R.drawable.veg_sign);
        } else {
            holder.menuItemDietSign.setImageResource(R.drawable.nonveg_sign);
            holder.menuItemDietSign.setColorFilter(holder.menuItemDietSign.getContext().getResources().getColor(android.R.color.holo_red_dark));
        }
        if (currentMenuItem.getOrderCount() == 0){
            holder.orderCountTextView.setText("Add");
        } else {
            holder.orderCountTextView.setText(String.valueOf(currentMenuItem.getOrderCount()));
        }
        // Setting colorFilter
        holder.menuAddIcon.setColorFilter(holder.menuAddIcon.getContext().getResources().getColor(R.color.brightGreen));
        holder.menuMinusIcon.setColorFilter(holder.menuMinusIcon.getContext().getResources().getColor(R.color.brightGreen));

        // OnClickListeners
        holder.menuItemPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMenuItem.getOrderCount() < 42) {
                    if (currentMenuItem.getOrderCount() == 0) {
                        holder.menuItemMinusButton.setVisibility(View.VISIBLE);
                    }
                    currentMenuItem.setOrderCount(currentMenuItem.getOrderCount() + 1);
                    holder.orderCountTextView.setText(String.valueOf(currentMenuItem.getOrderCount()));
                    listener.onOrderChanged(currentMenuItem);
                }
            }
        });
        holder.menuItemMinusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMenuItem.getOrderCount() != 0){
                    currentMenuItem.setOrderCount(currentMenuItem.getOrderCount()-1);
                    holder.orderCountTextView.setText(String.valueOf(currentMenuItem.getOrderCount()));
                    if (currentMenuItem.getOrderCount() == 0){
                        holder.orderCountTextView.setText("Add");
                        holder.menuItemMinusButton.setVisibility(View.GONE);
                    }
                    listener.onOrderChanged(currentMenuItem);
                }
            }
        });
        holder.menuItemAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMenuItem.getOrderCount() == 0) {
                    currentMenuItem.setOrderCount(currentMenuItem.getOrderCount()+1);
                    holder.menuItemMinusButton.setVisibility(View.VISIBLE);
                    holder.orderCountTextView.setText(String.valueOf(currentMenuItem.getOrderCount()));
                    listener.onOrderChanged(currentMenuItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

}
