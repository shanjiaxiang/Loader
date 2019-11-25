package com.xx.loader;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ClassifyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recog);
        getSupportActionBar().setTitle(R.string.classify);




    }
}
