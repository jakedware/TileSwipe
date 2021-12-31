package com.example.tileswipe;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Database(entities = {PuzzleGame.class}, version = 1)
@TypeConverters({Converter.class})
public abstract class PuzzleGameDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "puzzle-game-database";
    public abstract PuzzleGameDao puzzleGameDao();
    private static volatile PuzzleGameDatabase instance;

    static synchronized PuzzleGameDatabase getInstance(Context context) {
        if (instance == null) {
            instance = createInstance(context);
        }
        return instance;
    }

    private static PuzzleGameDatabase createInstance(Context context) {
        return Room.databaseBuilder(context, PuzzleGameDatabase.class, DATABASE_NAME).build();
    }
}
