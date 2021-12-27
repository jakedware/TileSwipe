package com.example.tileswipe;

import android.graphics.Matrix;

public class PuzzleGrid {
    protected static final int MOVE_UP = 0;
    protected static final int MOVE_RIGHT = 1;
    protected static final int MOVE_DOWN = 2;
    protected static final int MOVE_LEFT = 3;
    protected PuzzleTile[][] puzzleGrid;
    protected int emptyX;
    protected int emptyY;
    protected int numX;
    protected int numY;

    public PuzzleGrid(int numX, int numY) {
        puzzleGrid = new PuzzleTile[numX][numY];
        emptyX = numX - 1;
        emptyY = numY - 1;
    }

    public boolean tryToMoveTile(int i, int j, float x, float y) {
        Matrix matrix;
        if (x > 0 && i != numX && puzzleGrid[i + 1][j].isEmpty()) {
            matrix = new Matrix();
            matrix.preTranslate(x, 0);
        }
        else if (x < 0 && i != 0 && puzzleGrid[i - 1][j].isEmpty()) {
            matrix = new Matrix();
            matrix.preTranslate(x, 0);
        }
        else if (y > 0 && j != numY && puzzleGrid[i][j + 1].isEmpty()) {
            matrix = new Matrix();
            matrix.preTranslate(0, y);
        }
        else if (y < 0 && j != 0 && puzzleGrid[i - 1][j].isEmpty()) {
            matrix = new Matrix();
            matrix.preTranslate(0, y);
        }
        else {
            return false;
        }

        puzzleGrid[i][j].getTilePath().transform(matrix);
        return true;
    }
}
