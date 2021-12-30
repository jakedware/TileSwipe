package com.example.tileswipe;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Database(entities = {PuzzleGame.class}, version = 1)
@TypeConverters({Converter.class})
public abstract class PuzzleGameDatabase extends RoomDatabase {
    public abstract PuzzleGameDao puzzleGameDao();
}
