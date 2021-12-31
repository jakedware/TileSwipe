package com.example.tileswipe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final float BUTTON_WIDTH_PADDING_PERCENT = 0.15f;
    MainActivity thisActivity;
    ConstraintLayout constraintLayout;
    LinearLayout buttonLayout;
    Button newGameButton;
    Button resumeGameButton;
    Button statsButton;
    Button settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivity = this;
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        //         | View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();

        constraintLayout = findViewById(R.id.ConstraintLayout);
        buttonLayout = findViewById(R.id.main_activity_buttons_linear_layout);

        newGameButton = findViewById(R.id.start_new_game_button);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPuzzle(false);
            }
        });

        resumeGameButton = findViewById(R.id.resume_last_game_button);
        resumeGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPuzzle(true);
            }
        });

        statsButton = findViewById(R.id.statistics_button);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowStatistics statsTask = new ShowStatistics();
                PuzzleGameDatabase db = PuzzleGameDatabase.getInstance(thisActivity);
                statsTask.execute(db.puzzleGameDao());
            }
        });

        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        buttonLayout.setPadding((int) (displayMetrics.widthPixels * BUTTON_WIDTH_PADDING_PERCENT), 0, (int)(displayMetrics.widthPixels * BUTTON_WIDTH_PADDING_PERCENT), 0);

    }

    public void startPuzzle(boolean resumePreviousGame) {
        Intent intent = new Intent(this, PuzzleActivity.class);
        intent.putExtra(PuzzleActivity.RESUME_GAME_INTENT_KEY, resumePreviousGame);
        startActivity(intent);
    }

    private class ShowStatistics extends AsyncTask<PuzzleGameDao, Integer, Long> {
        ArrayList<PuzzleGame> games;

        @Override
        protected Long doInBackground(PuzzleGameDao... puzzleGameDao) {
            games = (ArrayList<PuzzleGame>) puzzleGameDao[0].getAllSolved();
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);

            int totalPuzzlesSolved = games.size();
            int avgMoveCount = 0;
            Long avgTimeSpent = 0L;
            int bestMoveCount = Integer.MAX_VALUE;
            Long fastestSolve = Long.MAX_VALUE;
            for (int i = 0; i < games.size(); i++) {
                PuzzleGame currGame = games.get(i);

                if (currGame.moveCount < bestMoveCount) {
                    bestMoveCount = currGame.moveCount;
                }
                if (currGame.msElapsed < fastestSolve) {
                    fastestSolve = currGame.msElapsed;
                }

                avgMoveCount += currGame.moveCount;
                avgTimeSpent += currGame.msElapsed;
            }

            if (totalPuzzlesSolved > 0) {
                avgMoveCount /= totalPuzzlesSolved;
                avgTimeSpent /= totalPuzzlesSolved;
            }
            else {
                bestMoveCount = 0;
                fastestSolve = 0L;
            }

            builder.setMessage("Total Puzzles Solved: " + totalPuzzlesSolved +
                    "\nBest Time: " + PuzzleGame.getTimeString(fastestSolve) +
                    "\nBest Move Count: " + bestMoveCount +
                    "\nAverage Time: " + PuzzleGame.getTimeString(avgTimeSpent) +
                    "\nAverage Move Count: " + avgMoveCount);

            builder.show();
        }
    }
}