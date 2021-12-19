package com.aqel.chameleona;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.aqel.chameleona.adapters.ColorPickerAdapter;
import com.aqel.chameleona.databinding.ActivityColorSelectionBinding;

public class AnalogWatchFaceWearableConfigActivity extends Activity {
    private WearableRecyclerView mRecyclerView;
    private ActivityColorSelectionBinding binding;
    private ColorPickerAdapter mColorPickerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityColorSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mColorPickerAdapter = new ColorPickerAdapter("com.aqel.chameleona.hours_color");
        mRecyclerView = binding.wearableRecyclerView;
        mRecyclerView.setEdgeItemsCenteringEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Improves performance because we know changes in content do not change the layout size of
        // the RecyclerView.
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mColorPickerAdapter);
    }
}
