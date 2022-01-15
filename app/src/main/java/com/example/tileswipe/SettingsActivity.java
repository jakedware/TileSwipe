package com.example.tileswipe;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SettingsActivity extends AppCompatActivity {
    final static String CUSTOM_IMAGE_PREFERENCES_URI_KEY = "custom_image_preferences_uri_key";
    final static int CUSTOM_IMAGE_PERMISSION_REQUEST_CODE = 1000;
    LinearLayout deleteSolveHistoryLayout;
    LinearLayout setCustomImageLayout;
    LinearLayout removeCustomImageLayout;
    LinearLayout changePuzzleBoardColorsLayout;
    SettingsActivity thisActivity = this;

    // custom image upload from user
    ActivityResultContract<String, Uri> contract = new ActivityResultContract() {
        @NonNull
        @Override
        public Intent createIntent(@NonNull Context context, Object input) {
            return new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            return intent == null ? null : intent.getData();
        }
    };

    ActivityResultLauncher<String> galleryResult = registerForActivityResult(contract, new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if (result == null) {
                return;
            }

            Thread thread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Bitmap bitmap;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), result);
                            bitmap = ImageDecoder.decodeBitmap(source, new ImageDecoder.OnHeaderDecodedListener() {
                                @Override
                                public void onHeaderDecoded(@NonNull ImageDecoder imageDecoder, @NonNull ImageDecoder.ImageInfo imageInfo, @NonNull ImageDecoder.Source source) {

                                    // load image only as big as we need
                                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                                    int offset = (int)(displayMetrics.heightPixels * PuzzleActivity.TEXT_VIEW_PERCENT);
                                    imageDecoder.setTargetSize(displayMetrics.widthPixels, displayMetrics.heightPixels - offset);

                                }
                            });

                        }
                        else {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
                        }

                        File dir = getFilesDir();

                        File bitmapFile = new File(dir, getString(R.string.custom_image_file_name));
                        FileOutputStream fileOutputStream = new FileOutputStream(bitmapFile);
                        bitmap.compress(Bitmap.CompressFormat.PNG,100, fileOutputStream);
                        fileOutputStream.close();

                    } catch (IOException e) {
                        Toast toast = new Toast(thisActivity);
                        toast.setText(R.string.set_custom_image_error_text);
                        toast.show();
                        //e.printStackTrace();
                    }
                }
            });

            thread.run();
        }
    });

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

        setCustomImageLayout = findViewById(R.id.set_custom_image_linear_layout);
        setCustomImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (checkSelfPermission(READ_EXTERNAL_STORAGE) != PERMISSION_GRANTED) {
                        requestPermissions(new String[] {READ_EXTERNAL_STORAGE}, CUSTOM_IMAGE_PERMISSION_REQUEST_CODE);
                    }
                    else {
                        getImage();
                    }
                }
            }
        });

        removeCustomImageLayout = findViewById(R.id.remove_custom_image_linear_layout);
        removeCustomImageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
                builder.setTitle(R.string.remove_custom_image_confirmation_title);
                builder.setMessage(R.string.remove_custom_image_confirmation_message);

                builder.setPositiveButton(R.string.remove_custom_image_confirmation_positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File file = new File(getFilesDir().getPath() + "/" + getString(R.string.custom_image_file_name));
                        file.delete();
                    }
                });

                builder.setNegativeButton(R.string.remove_custom_image_confirmation_negative_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.show();

            }
        });

        changePuzzleBoardColorsLayout = findViewById(R.id.change_puzzle_board_colors_layout);
        changePuzzleBoardColorsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChangePuzzleBoardColorsActivity();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CUSTOM_IMAGE_PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    getImage();
                }
                else {
                    Toast toast = new Toast(this);
                    toast.setText(getString(R.string.set_custom_image_error_text));
                    toast.show();
                }
                return;
        }
    }

    public void getImage() {
        galleryResult.launch("");
    }

    public void goToChangePuzzleBoardColorsActivity() {
        Intent intent = new Intent(this, ChangePuzzleBoardColorsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int rc, int resc, Intent intent) {
        super.onActivityResult(rc, resc, intent);
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

    // from developer.android.com
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}