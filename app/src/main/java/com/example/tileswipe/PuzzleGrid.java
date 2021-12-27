package com.example.tileswipe;

import android.graphics.Matrix;
import android.util.Log;

public class PuzzleGrid {
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

    public int tryToMoveTile(int i, int j, float firstX, float firstY, float deltaX, float deltaY, float x, float y) {
        if (i + 1 != emptyX && i - 1 != emptyX && j + 1 != emptyY && j - 1 != emptyY) {
            Log.d("tryToMoveTile()", "no room to move");
            return TILE_NOT_MOVED;
        }

        // snap tile to new location
        if (Math.abs(x - firstX) >= tileWidth || Math.abs(y - firstY) >= tileHeight) {
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
            return TILE_NEW_LOCATION;
        }

        PuzzleTile currTile  = puzzleGrid[i][j];
        Matrix matrix;
        if (deltaX > 0 && i + 1 < numX && puzzleGrid[i + 1][j].isEmpty()) {
            matrix = new Matrix();
            matrix.preTranslate(deltaX, 0);
            currTile.moveX(deltaX);
        }
        else if (deltaX < 0 && i > 0 && puzzleGrid[i - 1][j].isEmpty()) {
            matrix = new Matrix();
            matrix.preTranslate(deltaX, 0);
            currTile.moveX(deltaX);
        }
        else if (deltaY > 0 && j + 1 < numY && puzzleGrid[i][j + 1].isEmpty()) {
            matrix = new Matrix();
            matrix.preTranslate(0, deltaY);
            currTile.moveY(deltaY);
        }
        else if (deltaY < 0 && j > 0 && puzzleGrid[i][j - 1].isEmpty()) {
            matrix = new Matrix();
            matrix.preTranslate(0, deltaY);
            currTile.moveY(deltaY);
        }
        else {
            return TILE_NOT_MOVED;
        }

        puzzleGrid[i][j].getTilePath().transform(matrix);
        return TILE_MOVED;
    }
}
