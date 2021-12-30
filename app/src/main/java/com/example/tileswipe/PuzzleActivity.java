package com.example.tileswipe;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PuzzleActivity extends AppCompatActivity {
    private static final float TEXT_VIEW_PERCENT = 0.05f;
    protected Chronometer chronometer;
    protected ConstraintLayout constraintLayout;
    protected DisplayMetrics displayMetrics;
    protected Resources.Theme theme;
    private PuzzleActivity thisActivity;
    private long lastStop;
    private boolean isTimerOn;
    protected TextView moveView;
    protected int moveCount;
    protected PuzzleGameDao puzzleGameDao;
    protected long secondsElapsed = -1;
    protected PuzzleGame puzzleGame;
    protected int offset;
    enum GameRetrievalOptions {GET_ALL, GET_SPECIFIC, GET_FASTEST, GET_FEWEST_MOVES, GET_MOST_RECENT};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        Context thisContext = this;

        Intent intent = getIntent();
        boolean resumePreviousGame = intent.getBooleanExtra("resume-previous-game", false);

        PuzzleGameDatabase db = Room.databaseBuilder(this, PuzzleGameDatabase.class, "puzzle-game-database").build();
        //PuzzleGameDatabase db = Room.databaseBuilder(this, PuzzleGameDatabase.class, "puzzle-game-database").allowMainThreadQueries().build();
        puzzleGameDao = db.puzzleGameDao();
        //puzzleGameDao.clearTable();

        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
       //         | View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);

        getSupportActionBar().hide();

        thisActivity = this;
        this.theme = getTheme();

        displayMetrics = this.getResources().getDisplayMetrics();
        Log.d("DisplayMetrics", displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);

        constraintLayout = findViewById(R.id.PuzzleViewConstraintLayout);

        offset = (int)(displayMetrics.heightPixels * TEXT_VIEW_PERCENT);

        LinearLayout topInfoBarLinearLayout = findViewById(R.id.puzzle_activity_linear_layout);
        Button backButton = findViewById(R.id.puzzle_activity_back_button);
        moveView = findViewById(R.id.puzzle_activity_moves_text_view);
        chronometer = findViewById(R.id.puzzle_activity_time_text_view);

        moveCount = 0;
        moveView.setText(R.string.puzzle_activity_move_text);
        moveView.append("" + moveCount);

        topInfoBarLinearLayout.setMinimumHeight(offset);
        ViewGroup.LayoutParams layoutParams = topInfoBarLinearLayout.getLayoutParams();
        layoutParams.height = offset;

        backButton.setText(R.string.puzzle_activity_back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        lastStop = SystemClock.elapsedRealtime();
        final int[] tickCount = {0};
        chronometer.setBase(lastStop);
        chronometer.setFormat("time: %s");
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (isTimerOn) {
                    //secondsElapsed++;
                }
                Log.d("onChronometerTick()", "time: " + secondsElapsed + "s, base: " + chronometer.getBase() + ", elapsed real time: " + SystemClock.elapsedRealtime());
            }
        });



        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback);

        //GameRetrievalParameters testRetrievalParams = new GameRetrievalParameters(GameRetrievalOptions.GET_ALL, null);
        //getPuzzleGames();

        if (resumePreviousGame) {
            StartPuzzleWithMostRecentGame startTask = new StartPuzzleWithMostRecentGame();
            startTask.execute((Object) null);
        }
        else {
            puzzleGame = new PuzzleGame();
            PuzzleView puzzleView = new PuzzleView(this, displayMetrics.widthPixels, displayMetrics.heightPixels - offset, theme, offset, this, puzzleGameDao, puzzleGame, resumePreviousGame);
            constraintLayout.addView(puzzleView);
        }
    }

    protected void goBack() {
        Log.d("handleOnBackPressed()", "handling back");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.back_dialog_positive_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writePuzzleGameToDatabase();
                dialog.dismiss();
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

    protected void startTimer() {
        if (!isTimerOn) {
            chronometer.setBase(chronometer.getBase() + SystemClock.elapsedRealtime() - lastStop);
            chronometer.start();
            isTimerOn = !isTimerOn;
        }
    }

    protected void stopTimer() {
        if (isTimerOn) {
            lastStop = SystemClock.elapsedRealtime();
            secondsElapsed = (lastStop - chronometer.getBase()) / 1000;
            puzzleGame.secondsElapsed = secondsElapsed;
            chronometer.stop();
            isTimerOn = !isTimerOn;
        }
    }

    protected void updateMoveCount(int moveCount) {
        moveView.setText(R.string.puzzle_activity_move_text);
        moveView.append(" " +moveCount);
        puzzleGame.moveCount = moveCount;
    }

    public class WritePuzzleGameToDatabaseTask extends AsyncTask<PuzzleGame, Integer, Long> {

        @Override
        protected Long doInBackground(PuzzleGame... puzzleGames) {
            for (PuzzleGame puzzleGame: puzzleGames) {
                puzzleGame.lastUpdated = new Date().getTime();
                if (puzzleGameDao.checkIfGameExists(puzzleGame.uid)) {
                    puzzleGameDao.updateGames(puzzleGame);
                }
                else {
                    puzzleGameDao.insertGames(puzzleGame);
                }
            }

            return null;
        }
    };

    public class GameRetrievalParameters {
        protected GameRetrievalOptions retrievalChoice;
        int[] ids;
        List<PuzzleGame> retrievedGames;
        boolean finished;

        public GameRetrievalParameters(GameRetrievalOptions retrievalChoice, int[] ids) {
            this.retrievalChoice = retrievalChoice;
            this.ids = ids;
            retrievedGames = null;
            finished = false;
        }
    }
    public class RetrievePuzzleGameFromDatabaseTask extends AsyncTask<GameRetrievalParameters, Integer, Long> {

        @Override
        protected Long doInBackground(GameRetrievalParameters... gameRetrievalParameters) {
            for (int i = 0; i < gameRetrievalParameters.length; i++) {
                switch (gameRetrievalParameters[i].retrievalChoice) {
                    case GET_ALL:
                        gameRetrievalParameters[i].retrievedGames = puzzleGameDao.getAll();
                        break;
                    case GET_FASTEST:
                        gameRetrievalParameters[i].retrievedGames = puzzleGameDao.getFastestGames();
                        break;
                    case GET_FEWEST_MOVES:
                        gameRetrievalParameters[i].retrievedGames = puzzleGameDao.getLowestMoveCountGames();
                        break;
                    case GET_SPECIFIC:
                        gameRetrievalParameters[i].retrievedGames = puzzleGameDao.getGamesWithIds(gameRetrievalParameters[i].ids);
                        break;
                    case GET_MOST_RECENT:
                        gameRetrievalParameters[i].retrievedGames = puzzleGameDao.getMostRecentGames();
                }
            }
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        }
    }

    public class StartPuzzleWithMostRecentGame extends AsyncTask<Object, Integer, Long> {

        @Override
        protected Long doInBackground(Object... objects) {
            puzzleGame = puzzleGameDao.getMostRecentGames().get(0);
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            PuzzleView puzzleView = new PuzzleView(thisActivity, displayMetrics.widthPixels, displayMetrics.heightPixels - offset, theme, offset, thisActivity, puzzleGameDao, puzzleGame, true);
            constraintLayout.addView(puzzleView);
            chronometer.setBase(SystemClock.elapsedRealtime() - puzzleGame.secondsElapsed);
            secondsElapsed = puzzleGame.secondsElapsed;
            Log.d("onPostExecute()", "" + secondsElapsed + ", " + puzzleGame.secondsElapsed + ", " + (SystemClock.elapsedRealtime() - chronometer.getBase()));
            startTimer();
        }
    }

    protected void getPuzzleGames(GameRetrievalParameters... gameRetrievalParameters) {
        RetrievePuzzleGameFromDatabaseTask retrieveTask = new RetrievePuzzleGameFromDatabaseTask();
        retrieveTask.execute(gameRetrievalParameters);
    }

    protected boolean writePuzzleGameToDatabase() {
        WritePuzzleGameToDatabaseTask writeTask = new WritePuzzleGameToDatabaseTask();
        writeTask.execute(puzzleGame);
        return true;
    }

    @Override
    protected void onPause() {
        stopTimer();
        writePuzzleGameToDatabase();
        super.onPause();
    }

    @Override
    protected void onResume() {
        startTimer();
        super.onResume();
    }

    @Override
    protected void onStop() {
        stopTimer();
        writePuzzleGameToDatabase();
        super.onStop();
    }

    @Override
    protected void onRestart() {
        startTimer();
        super.onRestart();
    }
}