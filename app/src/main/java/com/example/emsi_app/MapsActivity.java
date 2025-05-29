package com.example.emsi_app;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class  MapsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Add MapsFragment to the activity
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.map, new MapsFragment())
                .commit();
        }
    }
} 