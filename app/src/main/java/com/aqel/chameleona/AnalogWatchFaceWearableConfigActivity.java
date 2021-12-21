package com.aqel.chameleona;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import com.aqel.chameleona.adapters.ItemSelectionAdapter;
import com.aqel.chameleona.databinding.ActivityItemSelectionBinding;

public class AnalogWatchFaceWearableConfigActivity extends Activity {
    private WearableRecyclerView mRecyclerView;
    private ActivityItemSelectionBinding binding;
    private ItemSelectionAdapter itemSelectionAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityItemSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        itemSelectionAdapter = new ItemSelectionAdapter();
        mRecyclerView = binding.wearableRecyclerView;
        mRecyclerView.setEdgeItemsCenteringEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Improves performance because we know changes in content do not change the layout size of
        // the RecyclerView.
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(itemSelectionAdapter);
    }
}
