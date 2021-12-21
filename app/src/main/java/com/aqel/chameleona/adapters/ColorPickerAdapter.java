package com.aqel.chameleona.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.wearable.view.CircledImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aqel.chameleona.R;
import com.aqel.chameleona.model.ColorInfo;
import static com.aqel.chameleona.model.ColorInfo.*;

import java.util.ArrayList;

public class ColorPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ColorInfo> mColors;
    private Integer mSharedPreferencesStringId;

    public ColorPickerAdapter(Integer sharedPreferencesStringId) {
        mColors = ColorInfo.getListOfColors();
        mSharedPreferencesStringId = sharedPreferencesStringId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder =
                new ColorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.color_picker_item, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        ColorInfo colorInfo = mColors.get(position);

        ColorViewHolder colorViewHolder = (ColorViewHolder) viewHolder;
        colorViewHolder.setInfo(colorInfo);
    }

    @Override
    public int getItemCount() {
        return mColors.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder implements View
            .OnClickListener {
        private CircledImageView mColorCircleView;
        private TextView mColorNameView;

        public ColorViewHolder(@NonNull View view) {
            super(view);
            view.setOnClickListener(this);
            mColorCircleView = view.findViewById(R.id.color);
            mColorNameView = view.findViewById(R.id.color_name);
        }

        @Override
        public void onClick(View v) {
            ColorInfo colorInfo = mColors.get(getAbsoluteAdapterPosition());
            Activity activity = (Activity) v.getContext();
            String sharedPreferenceString = activity.getString(mSharedPreferencesStringId);
            Log.d(this.getClass().getSimpleName(), "Preference String: " + sharedPreferenceString);
            if (sharedPreferenceString != null && !sharedPreferenceString.isEmpty()) {

                SharedPreferences sharedPref = activity.getSharedPreferences(
                        activity.getString(R.string.analog_config_file),
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(sharedPreferenceString, colorInfo.getColor());
                editor.apply();
                activity.setResult(Activity.RESULT_OK);
            }
            activity.finish();
        }

        public void setInfo(ColorInfo colorInfo) {
            mColorCircleView.setCircleColor(colorInfo.getColor());
            mColorNameView.setText(colorInfo.getName());
        }
    }
}
