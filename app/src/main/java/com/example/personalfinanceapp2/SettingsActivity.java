package com.example.personalfinanceapp2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.personalfinanceapp2.data.database.DatabaseClient;

public class SettingsActivity extends AppCompatActivity {

    private Button btnResetData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        btnResetData = findViewById(R.id.btnResetData);

        btnResetData.setOnClickListener(v -> {
            new Thread(() -> {
                DatabaseClient.getInstance(SettingsActivity.this)
                        .getAppDatabase()
                        .clearAllTables();

                runOnUiThread(() -> {
                    Toast.makeText(SettingsActivity.this, "All data deleted", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }).start();
        });
    }
}