package com.example.tileswipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        ConstraintLayout constraintLayout = findViewById(R.id.ConstraintLayout);
    }

    public void goToPuzzle(View view) {
        Intent intent = new Intent(this,PuzzleActivity.class);
        startActivity(intent);
    }
}