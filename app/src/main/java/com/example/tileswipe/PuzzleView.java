package com.example.tileswipe;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class PuzzleView extends View {
    float displayWidth;
    float displayHeight;
    float numTilesX;
    float numTilesY;
    float tileWidth;
    float tileHeight;
    protected final float tileSpacer;
    private Resources.Theme theme;
    private PuzzleTile[][] puzzleGrid;
    private PuzzleBorder puzzleBorder;
    private float[][][] gridCoords;
    private GestureDetector gestureDetector;
    VelocityTracker mVelocityTracker;
    private int gridIndexX = -1;
    private int gridIndexY = -1;
    private float previousX = -1f;
    private float previousY = -1f;
    private float firstX = -1f;
    private float firstY = -1f;
    private boolean tileIsOutOfPlace;
    PuzzleGrid myPuzzleGrid;

    public PuzzleView(Context context, int displayWidth, int displayHeight, Resources.Theme theme) {
        super(context);

        this.theme = theme;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        numTilesY = 6;
        numTilesX = 4;

        tileWidth = displayWidth * (1 - 2 * PuzzleBorder.BORDER_PERCENT) / numTilesX;
        tileHeight = displayHeight * (1 - 2 * PuzzleBorder.BORDER_PERCENT) / numTilesY;

        tileSpacer = 0;
        puzzleBorder = new PuzzleBorder(displayWidth, displayHeight);

        gridCoords = new float[(int)numTilesY][(int)numTilesY][];
        myPuzzleGrid = new PuzzleGrid((int)numTilesX, (int)numTilesY, gridCoords, tileWidth, tileHeight);
        puzzleGrid = myPuzzleGrid.puzzleGrid;
        for (int i = 0; i < puzzleGrid.length; i++) {
            for (int j = 0; j < puzzleGrid[i].length; j++) {
                PuzzleTile currTile = new PuzzleTile(tileWidth, tileHeight);

                Matrix matrix = new Matrix();
                gridCoords[i][j] = new float[]{puzzleBorder.thicknessX + (i * tileWidth), puzzleBorder.thicknessY + (j * tileHeight)};
                currTile.setPos(gridCoords[i][j][0], gridCoords[i][j][1]);
                matrix.preTranslate(gridCoords[i][j][0], gridCoords[i][j][1]);
                currTile.getTilePath().transform(matrix);

                if (i == puzzleGrid.length - 1 && j == puzzleGrid[i].length - 1) {
                    currTile.setEmpty();
                }

                puzzleGrid[i][j] = currTile;
            }
        }

    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawPath(puzzleBorder.getBorderPath(), puzzleBorder.getBorderPaint());
        drawTiles(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);
        float x;
        float y;
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                Log.d("PuzzleView.onDown()", "gesture detected");
                x = event.getX();
                y = event.getY();
                Log.d("PuzzleView.onDown()", x + "," + y);
                float innerX = x - puzzleBorder.thicknessX;
                float innerY = y - puzzleBorder.thicknessY;

                if (innerX <= 0 || innerY <= 0) {
                    Log.d("PuzzleView.onDown()", "outside grid");
                }

                int i = (int) (innerX / tileWidth);
                int j = (int) (innerY / tileHeight);

                if (i >= numTilesX || j >= numTilesY) {
                    Log.d("PuzzleView.onDown()", "outside grid");
                    break;
                }

                previousX = x;
                previousY = y;

                if (tileIsOutOfPlace) {
                    Log.d("onTouchEvent()", "tile is out of place");
                    if (myPuzzleGrid.isTouchOnTile(x, y, gridIndexX , gridIndexY)) {
                        Log.d("onTouchEvent()","moving tile at " + x + "," + y);
                        break;
                        //moveTile(x, y);
                    }

                }

                gridIndexX = i;
                gridIndexY = j;
                firstX = x;
                firstY = y;
                Log.d("PuzzleView.onDown()", String.format("[%d][%d]", gridIndexX, gridIndexY));

                break;
            case MotionEvent.ACTION_MOVE:
                if (moveTile(event.getX(), event.getY())) {
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                previousX = -1;
                previousY = -1;
                break;
        }
        return true;
    }

    public void drawTiles(Canvas canvas) {
        for (int i = 0; i < puzzleGrid.length; i++) {
            for(int j = 0; j < puzzleGrid[i].length; j++) {
                PuzzleTile currTile = puzzleGrid[i][j];
                canvas.drawPath(currTile.getTilePath(), currTile.getTilePaint());
            }
        }
    }

    public boolean moveTile(float x, float y) {
        if (gridIndexX == numTilesX || gridIndexY == numTilesY) {
            Log.d("PuzzleView.onDown()", "outside grid");
            return false;
        }
        float deltaX = x - previousX;
        float deltaY = y - previousY;
        //Log.d("onTouchEvent()", "deltaX: " + deltaX + ", deltaY: " + deltaY);
        if (deltaX == 0 && deltaY == 0) {
            return false;
        }
        //Log.d("onTouchEvent()", "gridIndexX: " + gridIndexX + " gridIndexY: " + gridIndexY);

        switch (myPuzzleGrid.tryToMoveTile(gridIndexX, gridIndexY, firstX, firstY, deltaX, deltaY, x, y)) {
            case PuzzleGrid.TILE_MOVED:
                previousX = x;
                previousY = y;
                tileIsOutOfPlace = true;
                invalidate();
                break;
            case PuzzleGrid.TILE_NEW_LOCATION:
                tileIsOutOfPlace = false;
                invalidate();
                break;
            case PuzzleGrid.TILE_NOT_MOVED:
                break;
        }
        return true;
    }
}
