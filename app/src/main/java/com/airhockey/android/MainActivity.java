package com.airhockey.android;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.airhockey.android.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.demoList.setLayoutManager(layoutManager);
        binding.demoList.setAdapter(new MainRVAdapter(this));
        binding.demoList.setItemAnimator(new DefaultItemAnimator());
    }
}
