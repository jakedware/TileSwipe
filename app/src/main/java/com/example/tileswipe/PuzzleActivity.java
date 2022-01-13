package com.example.tileswipe;

import static com.example.tileswipe.SettingsActivity.calculateInSampleSize;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PuzzleActivity extends AppCompatActivity {
    protected static final float TEXT_VIEW_PERCENT = 0.05f;
    public static final String RESUME_GAME_INTENT_KEY = "resume-previous-game";
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
    protected long msElapsed = -1;
    protected PuzzleGame puzzleGame;
    protected int offset;
    enum GameRetrievalOptions {GET_ALL, GET_SPECIFIC, GET_FASTEST, GET_FEWEST_MOVES, GET_MOST_RECENT, GET_HIGHEST_ID};
    private boolean resumePreviousGame;
    private Bitmap bitmap;
    private ImageButton viewImageButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        Context thisContext = this;

        Intent intent = getIntent();
        resumePreviousGame = intent.getBooleanExtra(RESUME_GAME_INTENT_KEY, false);

        PuzzleGameDatabase db = PuzzleGameDatabase.getInstance(this);
        puzzleGameDao = db.puzzleGameDao();

        displayMetrics = this.getResources().getDisplayMetrics();
        Log.d("DisplayMetrics", displayMetrics.heightPixels + "x" + displayMetrics.widthPixels);
        offset = (int)(displayMetrics.heightPixels * TEXT_VIEW_PERCENT);

        String filePath = getFilesDir().getPath() + "/" + getString(R.string.custom_image_file_name);

        // make sure bitmap isn't absurdly large
        // from developer.android.com
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels - offset);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(filePath, options);
        // end from developer.android.com


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


        constraintLayout = findViewById(R.id.PuzzleViewConstraintLayout);


        LinearLayout topInfoBarLinearLayout = findViewById(R.id.puzzle_activity_linear_layout);
        ImageButton backButton = findViewById(R.id.puzzle_activity_back_button);
        viewImageButton = findViewById(R.id.puzzle_activity_show_image_button);
        moveView = findViewById(R.id.puzzle_activity_moves_text_view);
        chronometer = findViewById(R.id.puzzle_activity_time_text_view);

        moveCount = 0;
        moveView.setText(R.string.puzzle_activity_move_text);
        moveView.append("" + moveCount);

        topInfoBarLinearLayout.setMinimumHeight(offset);
        ViewGroup.LayoutParams layoutParams = topInfoBarLinearLayout.getLayoutParams();
        layoutParams.height = offset;

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        viewImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
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
                Log.d("onChronometerTick()", "time: " + msElapsed + "ms, base: " + chronometer.getBase() + ", elapsed real time: " + SystemClock.elapsedRealtime());
            }
        });
        chronometer.setTextColor(moveView.getTextColors().getDefaultColor());

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback);

        if (resumePreviousGame) {
            StartPuzzleWithMostRecentGame startTask = new StartPuzzleWithMostRecentGame();
            startTask.execute((Object) null);
        }
        else {
            StartPuzzleWithNewGame startTask = new StartPuzzleWithNewGame();
            startTask.execute((Object) null);
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

        /*
        builder.setNeutralButton(R.string.back_dialog_neutral_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
         */

        builder.setTitle(R.string.back_dialog_title);
        builder.setMessage(R.string.back_dialog_message);

        builder.show();
    }

    private void showImage() {
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(imageView);
        builder.setCancelable(true);
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
            msElapsed = (lastStop - chronometer.getBase());
            puzzleGame.msElapsed = msElapsed;
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

    public class StartPuzzleWithNewGame extends AsyncTask<Object, Integer, Long> {
        int newUid = -1;
        ArrayList<PuzzleGame> highestIds;

        @Override
        protected Long doInBackground(Object... objects) {
            highestIds = (ArrayList<PuzzleGame>) puzzleGameDao.getHighestId();
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            puzzleGame = new PuzzleGame();
            if (highestIds.size() != 0) {
                puzzleGame.uid = highestIds.get(0).uid + 1;
            }
            PuzzleView puzzleView = new PuzzleView(thisActivity, displayMetrics.widthPixels, displayMetrics.heightPixels - offset,
                    theme, offset, thisActivity, puzzleGameDao, puzzleGame, resumePreviousGame, bitmap);
            constraintLayout.addView(puzzleView);
            startTimer();
        }
    }

    public class StartPuzzleWithMostRecentGame extends AsyncTask<Object, Integer, Long> {
        ArrayList<PuzzleGame> games;

        @Override
        protected Long doInBackground(Object... objects) {
            games = (ArrayList<PuzzleGame>) puzzleGameDao.getMostRecentGames();
            // if
            if (games.size() == 0) {
                games.add(new PuzzleGame());
                resumePreviousGame = false;
            }
            else {
                games = games;
            }
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            puzzleGame = games.get(0);
            PuzzleView puzzleView = new PuzzleView(thisActivity, displayMetrics.widthPixels, displayMetrics.heightPixels - offset,
                    theme, offset, thisActivity, puzzleGameDao, puzzleGame, resumePreviousGame, bitmap);
            constraintLayout.addView(puzzleView);
            chronometer.setBase(SystemClock.elapsedRealtime() - puzzleGame.msElapsed);
            msElapsed = puzzleGame.msElapsed;
            Log.d("onPostExecute()", "" + msElapsed + ", " + puzzleGame.msElapsed + ", " + (SystemClock.elapsedRealtime() - chronometer.getBase()));

            if (!puzzleGame.isSolved) {
                startTimer();
            }
        }
    }

    protected boolean writePuzzleGameToDatabase() {
        WritePuzzleGameToDatabaseTask writeTask = new WritePuzzleGameToDatabaseTask();
        writeTask.execute(puzzleGame);
        return true;
    }

    protected void showPuzzleSolvedDialog() {
        /*
        PuzzleSolvedDialogFragment solvedDialog = new PuzzleSolvedDialogFragment();
        solvedDialog.show(getSupportFragmentManager(), PuzzleSolvedDialogFragment.TAG);
        */
        stopTimer();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.puzzle_solved_dialog_text);
        builder.setCancelable(false);
        builder.setMessage("time taken: " + PuzzleGame.getTimeString(puzzleGame.msElapsed) +
                "\nmove count: " + puzzleGame.moveCount +
                "\ndate finished: " + (puzzleGame.dateSolvedMDY[0] + 1) + "/" + puzzleGame.dateSolvedMDY[1] + "/" + puzzleGame.dateSolvedMDY[2]);
        builder.setPositiveButton("back to main menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writePuzzleGameToDatabase();
                dialog.dismiss();
                finish();
            }
        });
        builder.show();

    }


    @Override
    protected void onPause() {
        stopTimer();
        writePuzzleGameToDatabase();
        super.onPause();
    }

    @Override
    protected void onResume() {
        //startTimer();
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