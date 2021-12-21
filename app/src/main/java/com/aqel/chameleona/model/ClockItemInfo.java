package com.aqel.chameleona.model;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public class ClockItemInfo {
    String itemName;
    Integer itemBitmapId;
    @StringRes
    Integer preferenceString;


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Integer getItemBitmapId() {
        return itemBitmapId;
    }

    public void setItemBitmapId(@DrawableRes Integer itemBitmapId) {
        this.itemBitmapId = itemBitmapId;
    }

    public Integer getPreferenceStringId() {
        return preferenceString;
    }

    public void setPreferenceString(@StringRes Integer preferenceString) {
        this.preferenceString = preferenceString;
    }

    public ClockItemInfo(String itemName, Integer itemBitmapId, @StringRes Integer preferenceString) {
        this.itemName = itemName;
        this.itemBitmapId = itemBitmapId;
        this.preferenceString = preferenceString;
    }
}
