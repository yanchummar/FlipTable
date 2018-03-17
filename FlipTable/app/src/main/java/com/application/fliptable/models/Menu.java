package com.application.fliptable.models;

/**
 * Created by yanchummar on 3/11/18.
 */

public class Menu {

    private int itemId;
    private String itemName;
    private int itemCost;
    private String itemDescription;
    private boolean veg;
    private int orderCount;

    public Menu(int itemId, boolean veg, String itemName, int itemCost, String itemDescription){
        this.itemId = itemId;
        this.veg = veg;
        this.itemName = itemName;
        this.itemCost = itemCost;
        this.itemDescription = itemDescription;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public boolean isVeg() {
        return veg;
    }

    public int getItemCost() {
        return itemCost;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemName() {
        return itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemCost(int itemCost) {
        this.itemCost = itemCost;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public void setVeg(boolean veg) {
        this.veg = veg;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }
}
