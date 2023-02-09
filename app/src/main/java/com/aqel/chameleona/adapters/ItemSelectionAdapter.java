package com.aqel.chameleona.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aqel.chameleona.ColorPickerActivity;
import com.aqel.chameleona.R;
import com.aqel.chameleona.model.ClockItemInfo;

import java.util.ArrayList;

public class ItemSelectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static private String EXTRA_INTEGER = "com.aqel.chameleona.EXTRA_INTEGER";

    ArrayList<ClockItemInfo> mItems;

    public ItemSelectionAdapter() {
        mItems = new ArrayList<>();
        mItems.add(new ClockItemInfo("Hours", 0, R.string.config_hours_color));
        mItems.add(new ClockItemInfo("Minutes", 0, R.string.config_minutes_color));
        mItems.add(new ClockItemInfo("Seconds", 0, R.string.config_seconds_color));
        mItems.add(new ClockItemInfo("Background", 0, R.string.config_background_color));
        mItems.add(new ClockItemInfo("Ticks", 0, R.string.config_ticks_color));

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder =
                new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selector_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ClockItemInfo clockItemInfo = mItems.get(position);

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setInfo(clockItemInfo);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CircledImageView mItemCircleView;
        private final TextView mItemNameView;
        private final Context mContext;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mItemCircleView = itemView.findViewById(R.id.clock_item);
            mItemNameView = itemView.findViewById(R.id.clock_item_name);
            mContext = itemView.getContext();
        }

        @Override
        public void onClick(View v) {
            Log.d(this.getClass().getSimpleName(), "OnClick");
            ClockItemInfo clockItemInfo = mItems.get(getAbsoluteAdapterPosition());
            Intent colorPickerIntent = new Intent(v.getContext(), ColorPickerActivity.class);
            colorPickerIntent.putExtra(EXTRA_INTEGER, clockItemInfo.getPreferenceStringId());
            Activity activity = (Activity) v.getContext();
            activity.startActivity(colorPickerIntent);
        }

        public void setInfo(ClockItemInfo clockItemInfo) {
            mItemNameView.setText(clockItemInfo.getItemName());
            SharedPreferences mSharedPref = mContext.getSharedPreferences(
                    mContext.getString(R.string.analog_config_file),
                    Context.MODE_PRIVATE);
            String preferenceString = mContext.getString(clockItemInfo.getPreferenceStringId());
            int color = mSharedPref.getInt(preferenceString, 0x2a2a2a);
            mItemCircleView.setCircleColor(color);
        }
    }
}
