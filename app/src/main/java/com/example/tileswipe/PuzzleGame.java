package com.example.tileswipe;

import androidx.room.*;

import java.util.ArrayList;

@Entity
public class PuzzleGame {
    @PrimaryKey
    public int uid;

    @ColumnInfo (name = "last_updated")
    public long lastUpdated;

    @ColumnInfo(name = "ms_elapsed")
    public long msElapsed;

    @ColumnInfo(name = "move_count")
    public int moveCount;

    @ColumnInfo(name = "scramble_moves")
    public int[] scrambleMoves;

    @ColumnInfo(name = "solve_moves")
    public ArrayList<Integer> solveMoves;

    @ColumnInfo(name = "date_solved_mdy")
    public int[] dateSolvedMDY;

    @ColumnInfo(name = "is_solved")
    public boolean isSolved;

    @ColumnInfo(name = "puzzle_grid")
    public int[][] puzzleGrid;

    @Ignore
    public static String getTimeString(Long msElapsed) {
        return msElapsed / 1000 / 60 + ":" + (msElapsed / 1000) % 60;
    }
}

