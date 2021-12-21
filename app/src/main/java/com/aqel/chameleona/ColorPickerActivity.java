package com.aqel.chameleona;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.wear.widget.WearableRecyclerView;

import android.app.Activity;
import android.os.Bundle;
import com.aqel.chameleona.adapters.ColorPickerAdapter;
import com.aqel.chameleona.adapters.ItemSelectionAdapter;
import com.aqel.chameleona.databinding.ActivityColorPickerBinding;
import com.aqel.chameleona.databinding.ActivityItemSelectionBinding;

public class ColorPickerActivity extends Activity {
    private WearableRecyclerView mRecyclerView;
    private ActivityColorPickerBinding binding;
    private ColorPickerAdapter colorPickerAdapter;
    static private String EXTRA_INTEGER = "com.aqel.chameleona.EXTRA_INTEGER";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityColorPickerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int preferenceId = getIntent().getIntExtra(EXTRA_INTEGER, -1);
        if (preferenceId == -1) {
            this.setResult(Activity.RESULT_CANCELED);
            this.finish();
        }
        colorPickerAdapter = new ColorPickerAdapter(preferenceId);
        mRecyclerView = binding.wearableRecyclerView;
        mRecyclerView.setEdgeItemsCenteringEnabled(true);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Improves performance because we know changes in content do not change the layout size of
        // the RecyclerView.
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(colorPickerAdapter);
    }
}