package com.example.tileswipe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    LinearLayout deleteSolveHistoryLayout;
    SettingsActivity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        thisActivity = this;

        deleteSolveHistoryLayout = findViewById(R.id.delete_solve_history_linear_layout);
        deleteSolveHistoryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle(R.string.delete_database_confirmation_title);
                builder.setMessage(R.string.delete_database_confirmation_message);

                builder.setPositiveButton(R.string.delete_database_confirmation_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClearDatabase clearDatabase = new ClearDatabase();
                        PuzzleGameDatabase db = PuzzleGameDatabase.getInstance(thisActivity);
                        clearDatabase.execute(db.puzzleGameDao());
                    }
                });

                builder.setNegativeButton(R.string.delete_database_confirmation_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });
    }

    public class ClearDatabase extends AsyncTask<PuzzleGameDao, Object, Long> {

        @Override
        protected Long doInBackground(PuzzleGameDao... puzzleGameDaos) {
            puzzleGameDaos[0].clearTable();
            return 0L;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            Log.d("Settings Activity", "database cleared");
            Toast toast = new Toast(thisActivity);
            toast.setText("database cleared");
            toast.show();

        }
    }
}