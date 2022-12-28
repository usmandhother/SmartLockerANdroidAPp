package com.example.smartlocker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class blockScreenActivity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_screen);
    }
}