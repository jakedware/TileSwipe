package com.example.tileswipe;

import androidx.room.*;

import java.util.ArrayList;

@Entity
public class PuzzleGame {
    @PrimaryKey
    public int uid;

    @ColumnInfo (name = "last_updated")
    public long lastUpdated;

    @ColumnInfo(name = "seconds_elapsed")
    public long secondsElapsed;

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
}

