package com.example.tileswipe;

import androidx.room.TypeConverter;

import java.util.ArrayList;

public class Converter {
    private static final String LIST_DELIM = "/";
    private static final String COL_DELIM = "//";
    @TypeConverter
    public static String scrambleMovesToString(int[] scrambleMoves) {
        String movesString = "";
        for (int i = 0; i < scrambleMoves.length; i++) {
            movesString += scrambleMoves[i];
            if (i < scrambleMoves.length - 1) {
                movesString += LIST_DELIM;
            }
        }
        return movesString;
    }


    @TypeConverter
    public static int[] stringToScrambleMoves(String movesString) {
        String[] movesArray = movesString.split(LIST_DELIM);
        int[] scrambleMoves = new int[movesArray.length];

        for (int i = 0; i < movesArray.length; i++) {
            scrambleMoves[i] = Integer.parseInt(movesArray[i]);
        }

        return scrambleMoves;
    }

    @TypeConverter
    public static String puzzleGridToString(int[][] puzzleGrid) {
        String puzzleGridString = "";
        for (int i = 0; i < puzzleGrid.length; i++) {
            for (int j = 0; j < puzzleGrid[i].length; j++) {
                puzzleGridString += puzzleGrid[i][j];

                if (j != puzzleGrid[i].length - 1) {
                    puzzleGridString += LIST_DELIM;
                }
            }
            if (i != puzzleGrid.length - 1) {
                puzzleGridString += COL_DELIM;
            }
        }

        return puzzleGridString;
    }

    @TypeConverter
    public static int[][] stringToPuzzleGrid(String puzzleGridString) {
        String[] puzzleColumns = puzzleGridString.split(COL_DELIM);
        int[][] puzzleGrid = new int[puzzleColumns.length][];

        for (int i = 0; i < puzzleColumns.length; i++) {
            String[] currColumn = puzzleColumns[i].split(LIST_DELIM);
            puzzleGrid[i] = new int[currColumn.length];

            for (int j = 0; j < currColumn.length; j++) {
                puzzleGrid[i][j] = Integer.parseInt(currColumn[j]);
            }
        }

        return puzzleGrid;
    }

    @TypeConverter
    public static String solveMovesToString(ArrayList<Integer> solveMoves) {
        String solveMovesString = "";
        for (int i = 0; i < solveMoves.size(); i++) {
            solveMovesString += solveMoves.get(i);
            if (i != solveMoves.size() - 1) {
                solveMovesString += LIST_DELIM;
            }
        }

        return solveMovesString;
    }

    @TypeConverter
    public static ArrayList<Integer> stringToSolveMoves(String solveMovesString) {
        String[] solveMovesStringArr = solveMovesString.split(LIST_DELIM);
        ArrayList<Integer> solveMoves = new ArrayList<>();
        for (int i = 0; i < solveMovesStringArr.length; i++) {
            solveMoves.add(Integer.parseInt(solveMovesStringArr[i]));
        }

        return solveMoves;
    }

}
