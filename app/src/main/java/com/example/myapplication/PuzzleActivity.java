package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

public class PuzzleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        getSupportActionBar().hide();

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Log.d("DisplayMetrics", displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);

        PuzzleView puzzleView = new PuzzleView(this, displayMetrics.widthPixels, displayMetrics.heightPixels, this.getTheme());
        ConstraintLayout constraintLayout = findViewById(R.id.PuzzleViewConstraintLayout);
        constraintLayout.addView(puzzleView);
    }
}