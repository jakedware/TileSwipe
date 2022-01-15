package com.example.tileswipe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ChangePuzzleBoardColorsActivity extends AppCompatActivity implements ColorPickerDialogFragment.ColorPickerListener {
    ColorPickerDialogFragment colorPickerDialogFragment;
    Dialog dialog;
    LinearLayout verticalLayout;
    Button borderButton;
    Button tileButton;
    Button numberButton;
    Button currButton;
    public enum ButtonType {BORDER_BUTTON, TILE_BUTTON, NUMBER_BUTTON};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_puzzle_board_colors);

        verticalLayout = findViewById(R.id.change_puzzle_board_colors_vertical_linear_layout);

        borderButton = findViewById(R.id.change_border_color_button);
        tileButton = findViewById(R.id.change_tile_color_button);
        numberButton = findViewById(R.id.change_number_color_button);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        //PuzzleView puzzleView = new PuzzleView(this, displayMetrics.widthPixels, displayMetrics.heightPixels, null,0,null,null, new PuzzleGame(),false,null);
        //verticalLayout.addView(puzzleView);

        borderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog();
                currButton = borderButton;
            }
        });

        tileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog();
                currButton = tileButton;
            }
        });

        numberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog();
                currButton = numberButton;
            }
        });

    }

    public void showColorPickerDialog() {
        colorPickerDialogFragment = new ColorPickerDialogFragment();
        colorPickerDialogFragment.show(getSupportFragmentManager(), "TAG");

    }

    @Override
    public void onARGBChange(int[] argb) {

    }

    @Override
    public void onConfirm(int[] argb) {
        Log.d("onConfirm()", argb[0] + " " + argb[1] + " " + argb[2] + " " + argb[3]);
        //currButton.setBackgroundColor(Color.argb(argb[0], argb[1], argb[2], argb[3]));

        ColorStateList colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {Color.argb(argb[0],argb[1],argb[2],argb[3])});
        currButton.setBackgroundTintList(colorStateList);
    }

    public void setButtonColor(int[] argb, ButtonType buttonType) {
        switch (buttonType) {
            case TILE_BUTTON:
                tileButton.setBackgroundColor(Color.argb(argb[0], argb[1], argb[2], argb[3]));
                break;
            case BORDER_BUTTON:
                borderButton.setBackgroundColor(Color.argb(argb[0], argb[1], argb[2], argb[3]));
                break;
            case NUMBER_BUTTON:
                numberButton.setBackgroundColor(Color.argb(argb[0], argb[1], argb[2], argb[3]));
                break;
        }
    }
}