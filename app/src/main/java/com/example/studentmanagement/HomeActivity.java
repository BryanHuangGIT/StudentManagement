
package com.example.studentmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    private Button buttonPartA;
    private Button buttonPartB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        buttonPartA = findViewById(R.id.buttonPartA);
        buttonPartB = findViewById(R.id.buttonPartB);

        buttonPartA.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    MainActivity.class
            );
            startActivity(intent);
        });

        buttonPartB.setOnClickListener(v -> {
            Intent intent = new Intent(
                    HomeActivity.this,
                    WeatherActivity.class
            );
            startActivity(intent);
        });
    }
}
