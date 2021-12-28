package com.example.tileswipe;

import android.graphics.Matrix;
import android.util.Log;

public class PuzzleGrid {
    protected static final int MOVEMENT_ORIENTATION_HORIZONTAL = 0;
    protected static final int MOVEMENT_ORIENTATION_VERTICAL = 1;
    protected static final int MOVEMENT_ORIENTATION_INVALID = -1;
    protected static final int MOVEMENT_RANGE_INVALID_FIXED_PARAM = -2;
    protected static final int MOVEMENT_RANGE_UNDER = -1;
    protected static final int MOVEMENT_RANGE_WITHIN = 0;
    protected static final int MOVEMENT_RANGE_OVER = 1;
    protected static final int MOVE_NO_WHERE = -1;
    protected static final int MOVE_UP = 0;
    protected static final int MOVE_DOWN = 1;
    protected static final int MOVE_LEFT = 2;
    protected static final int MOVE_RIGHT = 3;
    protected static final int MOVE_REVERSE = 4;
    protected static final int TILE_NOT_MOVED = 0;
    protected static final int TILE_MOVED = 1;
    protected static final int TILE_NEW_LOCATION = 2;
    protected PuzzleTile[][] puzzleGrid;
    protected float[][][] gridCoords;
    protected int emptyX;
    protected int emptyY;
    protected int numX;
    protected int numY;
    protected float tileWidth;
    protected float tileHeight;
    private int moveDirection = -1;

    public PuzzleGrid(int numX, int numY, float[][][] gridCoords, float tileWidth, float tileHeight) {
        this.gridCoords = gridCoords;
        puzzleGrid = new PuzzleTile[numX][numY];
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.numX = numX;
        this.numY = numY;
        emptyX = numX - 1;
        emptyY = numY - 1;
    }

    private void returnTileToGridPosition(int i, int j) {
        PuzzleTile currTile = puzzleGrid[i][j];
        Matrix matrix = new Matrix();
        matrix.preTranslate(gridCoords[i][j][0] - currTile.posX, gridCoords[i][j][1] - currTile.posY);
        currTile.getTilePath().transform(matrix);
        currTile.setPos(gridCoords[i][j][0], gridCoords[i][j][1]);
    }

    private void switchMovingWithEmpty(int i, int j) {
        Log.d("tryToMoveTile()", "tile in new location");
        PuzzleTile emptyTile = puzzleGrid[emptyX][emptyY];
        puzzleGrid[emptyX][emptyY] = puzzleGrid[i][j];
        puzzleGrid[i][j] = emptyTile;

        Matrix emptyTileMatrix = new Matrix();
        emptyTileMatrix.preTranslate(gridCoords[i][j][0] - gridCoords[emptyX][emptyY][0], gridCoords[i][j][1] - gridCoords[emptyX][emptyY][1]);
        emptyTile.getTilePath().transform(emptyTileMatrix);
        emptyTile.setPos(gridCoords[i][j][0], gridCoords[i][j][1]);

        Matrix movedTileMatrix = new Matrix();
        movedTileMatrix.preTranslate(gridCoords[emptyX][emptyY][0] - puzzleGrid[emptyX][emptyY].posX, gridCoords[emptyX][emptyY][1] - puzzleGrid[emptyX][emptyY].posY);
        puzzleGrid[emptyX][emptyY].getTilePath().transform(movedTileMatrix);
        puzzleGrid[emptyX][emptyY].setPos(gridCoords[emptyX][emptyY][0], gridCoords[emptyX][emptyY][1]);

        emptyX = i;
        emptyY = j;
    }

    public boolean isTouchOnTile(float x, float y, int i, int j) {
        PuzzleTile currTile = puzzleGrid[i][j];
        if (currTile.posX <= x && x < currTile.posX + tileWidth && currTile.posY <= y && y < currTile.posY + tileHeight) {
            Log.d("isTouchOnTile()", String.format("touch detected at %f,%f for tile [%d][%d]", x, y, i, j));
            return true;
        }

        return false;
    }

    public int isMoveWithinRange(float[][] movementRange, int movementOrientation, float x, float y) {
        int lowerBound = 0;
        int upperBound = 1;
        if (movementOrientation == MOVEMENT_ORIENTATION_HORIZONTAL) {
            if (movementRange[0][0] > movementRange[1][0]) {
                //upperBound = 0;
                //lowerBound = 1;
                float[] temp = movementRange[0];
                movementRange[0] = movementRange[1];
                movementRange[1] = temp;
            }
            //Log.d("isMoveWithinRange()", String.format("testing position %f,%f in range %f,%f to %f,%f", x, y,
            //        movementRange[lowerBound][0], movementRange[lowerBound][1], movementRange[upperBound][0], movementRange[upperBound][1]));

            if (y != movementRange[0][1]) {
                return MOVEMENT_RANGE_INVALID_FIXED_PARAM;
            }

            if (x < movementRange[lowerBound][0]) {
                return MOVEMENT_RANGE_UNDER;
            }

            if (x > movementRange[upperBound][0]) {
                return  MOVEMENT_RANGE_OVER;
            }
        }
        else if (movementOrientation == MOVEMENT_ORIENTATION_VERTICAL) {
            if (movementRange[0][1] > movementRange[1][1]) {
                //upperBound = 0;
                //lowerBound = 1;
                float[] temp = movementRange[0];
                movementRange[0] = movementRange[1];
                movementRange[1] = temp;
            }
            //Log.d("isMoveWithinRange()", String.format("testing position %f,%f in range %f,%f to %f,%f", x, y,
            //        movementRange[lowerBound][0], movementRange[lowerBound][1], movementRange[upperBound][0], movementRange[upperBound][1]));

            if (x != movementRange[0][0]) {
                return MOVEMENT_RANGE_INVALID_FIXED_PARAM;
            }

            if (y < movementRange[lowerBound][1]) {
                return MOVEMENT_RANGE_UNDER;
            }
            if (y > movementRange[upperBound][1]) {
                return MOVEMENT_RANGE_OVER;
            }
        }

        return MOVEMENT_RANGE_WITHIN;
    }

    public int tryToMoveTile(int i, int j, float firstX, float firstY, float deltaX, float deltaY, float x, float y) {
        if (i + 1 != emptyX && i - 1 != emptyX && j + 1 != emptyY && j - 1 != emptyY) {
            //Log.d("tryToMoveTile()", "no room to move");
            return TILE_NOT_MOVED;
        }

        Log.d("tryToMoveTile()", String.format("Trying to move [%d][%d] at %f,%f by %f,%f", i, j, x, y, deltaX, deltaY));
        PuzzleTile currTile  = puzzleGrid[i][j];
        Matrix matrix;

        float[] movementRangeZero = new float[] {gridCoords[i][j][0], gridCoords[i][j][1]};
        float[] movementRangeOne = new float[] {gridCoords[emptyX][emptyY][0], gridCoords[emptyX][emptyY][1]};
        float[][] movementRange = new float[][] {movementRangeZero, movementRangeOne};
        int movementOrientation = MOVEMENT_ORIENTATION_INVALID;
        if ((j + 1 == emptyY || j - 1 == emptyY) && i == emptyX) {
            Log.d("tryToMoveTile()", "trying to move tile vertically");
            movementOrientation = MOVEMENT_ORIENTATION_VERTICAL;

            int movementRangeCheck = isMoveWithinRange(movementRange, movementOrientation, currTile.posX, currTile.posY + deltaY);
            Log.d("tryToMoveTile()", "tile is " + movementRangeCheck);
            switch (movementRangeCheck) {
                case MOVEMENT_RANGE_WITHIN:
                    matrix = new Matrix();
                    matrix.preTranslate(0, deltaY);
                    currTile.moveY(deltaY);
                    puzzleGrid[i][j].getTilePath().transform(matrix);
                    return TILE_MOVED;
                case MOVEMENT_RANGE_OVER:
                    if (movementRange[0] == movementRangeZero) {
                        switchMovingWithEmpty(i, j);
                        return TILE_NEW_LOCATION;
                    }
                    else {
                        returnTileToGridPosition(i, j);
                        return TILE_NEW_LOCATION;
                    }
                case MOVEMENT_RANGE_UNDER:
                    if (movementRange[1] == movementRangeZero) {
                        switchMovingWithEmpty(i, j);
                        return TILE_NEW_LOCATION;
                    }
                    else {
                        returnTileToGridPosition(i, j);
                        return TILE_NEW_LOCATION;
                    }
            }
        }

        if ((i + 1 == emptyX || i - 1 == emptyX) && j == emptyY) {
            Log.d("tryToMoveTile()", "trying to move tile horizontally");
            movementOrientation = MOVEMENT_ORIENTATION_HORIZONTAL;

            int movementRangeCheck = isMoveWithinRange(movementRange, movementOrientation, currTile.posX + deltaX, currTile.posY);
            Log.d("tryToMoveTile()", "tile is " + movementRangeCheck);
            switch (movementRangeCheck) {
                case MOVEMENT_RANGE_WITHIN:
                    matrix = new Matrix();
                    matrix.preTranslate(deltaX, 0);
                    currTile.moveX(deltaX);
                    puzzleGrid[i][j].getTilePath().transform(matrix);
                    return TILE_MOVED;
                case MOVEMENT_RANGE_OVER:
                    if (movementRange[0] == movementRangeZero) {
                        switchMovingWithEmpty(i, j);
                        return TILE_NEW_LOCATION;
                    }
                    else {
                        returnTileToGridPosition(i, j);
                        return TILE_NEW_LOCATION;
                    }
                case MOVEMENT_RANGE_UNDER:
                    if (movementRange[1] == movementRangeZero) {
                        switchMovingWithEmpty(i, j);
                        return TILE_NEW_LOCATION;
                    }
                    else {
                        returnTileToGridPosition(i, j);
                        return TILE_NEW_LOCATION;
                    }
            }
        }

        return TILE_NOT_MOVED;
    }
}
