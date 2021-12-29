package com.example.tileswipe;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

public class PuzzleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        Context thisContext = this;

        getSupportActionBar().hide();

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        Log.d("DisplayMetrics", displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);

        PuzzleView puzzleView = new PuzzleView(this, displayMetrics.widthPixels, displayMetrics.heightPixels, this.getTheme());
        ConstraintLayout constraintLayout = findViewById(R.id.PuzzleViewConstraintLayout);
        constraintLayout.addView(puzzleView);


        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Log.d("handleOnBackPressed()", "handling back");
                AlertDialog.Builder builder = new AlertDialog.Builder(thisContext);

                builder.setPositiveButton(R.string.back_dialog_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton(R.string.back_dialog_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                builder.setTitle(R.string.back_dialog_title);
                builder.setMessage(R.string.back_dialog_message);

                builder.show();
            }
        };
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback);
    }
}