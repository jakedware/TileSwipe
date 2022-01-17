package com.example.tileswipe;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class ChangePuzzleBoardColorsActivity extends AppCompatActivity implements ColorPickerDialogFragment.ColorPickerListener {
    public static final String COLOR_SHARED_PREFERENCES_KEY = "color_shared_preferences_key";
    ColorPickerDialogFragment colorPickerDialogFragment;
    Dialog dialog;
    LinearLayout verticalLayout;
    Button borderButton;
    Button tileButton;
    Button numberButton;
    Button resetBorderColorButton;
    Button resetTileColorButton;
    Button resetNumberColorButton;

    public enum ButtonType {BORDER_BUTTON, TILE_BUTTON, NUMBER_BUTTON};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_puzzle_board_colors);

        verticalLayout = findViewById(R.id.change_puzzle_board_colors_vertical_linear_layout);

        borderButton = findViewById(R.id.change_border_color_button);
        tileButton = findViewById(R.id.change_tile_color_button);
        numberButton = findViewById(R.id.change_number_color_button);

        resetBorderColorButton = findViewById(R.id.reset_border_color_button);
        resetTileColorButton = findViewById(R.id.reset_tile_color_button);
        resetNumberColorButton = findViewById(R.id.reset_number_color_button);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        //PuzzleView puzzleView = new PuzzleView(this, displayMetrics.widthPixels, displayMetrics.heightPixels, null,0,null,null, new PuzzleGame(),false,null);
        //verticalLayout.addView(puzzleView);

        borderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog(ButtonType.BORDER_BUTTON);
            }
        });

        tileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog(ButtonType.TILE_BUTTON);
            }
        });

        numberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showColorPickerDialog(ButtonType.NUMBER_BUTTON);
            }
        });

        resetBorderColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetBorderColor();
            }
        });

        resetTileColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTileColor();
            }
        });

        resetNumberColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetNumberColor();
            }
        });

        setCurrentButtonColors();
    }

    public void showColorPickerDialog(ButtonType buttonType) {
        colorPickerDialogFragment = new ColorPickerDialogFragment();
        colorPickerDialogFragment.show(getSupportFragmentManager(), buttonType.name());
    }

    @Override
    public void onARGBChange(int[] argb, String buttonTag) {

    }

    @Override
    public void onConfirm(int[] argb, String buttonTag) {
        Log.d("onConfirm()", argb[0] + " " + argb[1] + " " + argb[2] + " " + argb[3]);

        ButtonType buttonType = ButtonType.valueOf(buttonTag);
        setButtonColor(argb, buttonType);
    }

    public void setCurrentButtonColors() {
        SharedPreferences preferences = getSharedPreferences(COLOR_SHARED_PREFERENCES_KEY, MODE_PRIVATE);

        String border = preferences.getString(PuzzleBorder.BORDER_COLOR_KEY, Color.BLACK + "");
        String tile = preferences.getString(PuzzleTile.TILE_COLOR_KEY, Color.GRAY + "");
        String number = preferences.getString(PuzzleTile.NUMBER_COLOR_KEY, Color.BLACK + "");

        ColorStateList colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {Integer.parseInt(border)});
        borderButton.setBackgroundTintList(colorStateList);

        colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {Integer.parseInt(tile)});
        tileButton.setBackgroundTintList(colorStateList);

        colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {Integer.parseInt(number)});
        numberButton.setBackgroundTintList(colorStateList);
    }

    public void setButtonColor(int[] argb, ButtonType buttonType) {
        SharedPreferences.Editor editor = getSharedPreferences(ChangePuzzleBoardColorsActivity.COLOR_SHARED_PREFERENCES_KEY, MODE_PRIVATE).edit();

        int color;
        ColorStateList colorStateList;
        switch (buttonType) {
            case TILE_BUTTON:
                color = Color.argb(argb[0], argb[1], argb[2], argb[3]);
                colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {color});
                tileButton.setBackgroundTintList(colorStateList);

                editor.putString(PuzzleTile.TILE_COLOR_KEY, "" + color);
                break;
            case BORDER_BUTTON:
                color = Color.argb(argb[0], argb[1], argb[2], argb[3]);
                colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {color});
                borderButton.setBackgroundTintList(colorStateList);
                editor.putString(PuzzleBorder.BORDER_COLOR_KEY, "" + color);
                break;
            case NUMBER_BUTTON:
                color = Color.argb(argb[0], argb[1], argb[2], argb[3]);
                colorStateList = new ColorStateList(new int[][] {new int[] {android.R.attr.state_enabled}}, new int[] {color});
                numberButton.setBackgroundTintList(colorStateList);
                editor.putString(PuzzleTile.NUMBER_COLOR_KEY, "" + color);
                break;
        }

        editor.apply();
    }

    public void resetColors() {
        SharedPreferences preferences = getSharedPreferences(COLOR_SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int borderColor = Color.BLACK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            borderColor = getColor(R.color.puzzle_border_color);
        }

        int tileColor = Color.GRAY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tileColor = getColor(R.color.tile_color);
        }

        int numberColor = Color.BLACK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            numberColor = getColor(R.color.number_color);
        }

        editor.putString(PuzzleBorder.BORDER_COLOR_KEY, "" + borderColor);
        editor.putString(PuzzleTile.TILE_COLOR_KEY, "" + tileColor);
        editor.putString(PuzzleTile.NUMBER_COLOR_KEY, "" + numberColor);

        editor.apply();
    }

    public void resetBorderColor() {
        SharedPreferences preferences = getSharedPreferences(COLOR_SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int borderColor = Color.BLACK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            borderColor = getColor(R.color.puzzle_border_color);
        }

        editor.putString(PuzzleBorder.BORDER_COLOR_KEY, "" + borderColor);
        editor.apply();

        setCurrentButtonColors();
    }

    public void resetTileColor() {
        SharedPreferences preferences = getSharedPreferences(COLOR_SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int tileColor = Color.GRAY;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tileColor = getColor(R.color.tile_color);
        }

        editor.putString(PuzzleTile.TILE_COLOR_KEY, "" + tileColor);
        editor.apply();

        setCurrentButtonColors();
    }

    public void resetNumberColor() {
        SharedPreferences preferences = getSharedPreferences(COLOR_SHARED_PREFERENCES_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        int numberColor = Color.BLACK;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            numberColor = getColor(R.color.number_color);
        }

        editor.putString(PuzzleTile.NUMBER_COLOR_KEY, "" + numberColor);
        editor.apply();

        setCurrentButtonColors();
    }
}