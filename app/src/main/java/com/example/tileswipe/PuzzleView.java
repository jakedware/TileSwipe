package com.example.tileswipe;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

public class PuzzleView extends View {
    protected static final int MOVE_UP = 0;
    protected static final int MOVE_DOWN = 1;
    protected static final int MOVE_LEFT = 2;
    protected static final int MOVE_RIGHT = 3;
    private final int NUM_GRID_SCRAMBLES = 100;
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
    private int outOfPlaceIndexX = -1;
    private int outOfPlaceIndexY = -1;
    private float previousX = -1f;
    private float previousY = -1f;
    private float firstX = -1f;
    private float firstY = -1f;
    private boolean tileIsOutOfPlace;
    PuzzleGrid myPuzzleGrid;
    private int moveCount;
    private int offset;
    private PuzzleActivity puzzleActivity;
    protected PuzzleGameDao puzzleGameDao;
    protected PuzzleGame puzzleGame;
    private ArrayList<Integer> solveMoves;
    private Bitmap bitmap;

    public PuzzleView(Context context, int displayWidth, int displayHeight, Resources.Theme theme, int offset,
                      PuzzleActivity puzzleActivity, PuzzleGameDao puzzleGameDao, PuzzleGame puzzleGame, boolean resumePreviousGame, Bitmap bitmap) {
        super(context);

        this.bitmap = bitmap;

        this.puzzleGameDao = puzzleGameDao;
        this.puzzleGame = puzzleGame;

        this.puzzleActivity = puzzleActivity;
        this.offset = offset;
        this.theme = theme;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;

        if (resumePreviousGame) {
            if (puzzleGame.isSolved) {
                puzzleActivity.showPuzzleSolvedDialog();
            }
            solveMoves = puzzleGame.solveMoves;
            moveCount = puzzleGame.moveCount;
        }
        else {
            solveMoves = new ArrayList<>();
            puzzleGame.solveMoves = solveMoves;
            moveCount = 0;
        }
        puzzleActivity.updateMoveCount(moveCount);


        numTilesY = 6;
        numTilesX = 4;

        tileWidth = displayWidth * (1 - 2 * PuzzleBorder.BORDER_PERCENT) / numTilesX;
        tileHeight = displayHeight * (1 - 2 * PuzzleBorder.BORDER_PERCENT) / numTilesY;

        tileSpacer = 0;
        puzzleBorder = new PuzzleBorder(displayWidth, displayHeight, offset);

        gridCoords = new float[(int)numTilesY][(int)numTilesY][];
        myPuzzleGrid = new PuzzleGrid((int)numTilesX, (int)numTilesY, gridCoords, tileWidth, tileHeight);
        puzzleGrid = myPuzzleGrid.puzzleGrid;
        for (int i = 0; i < puzzleGrid.length; i++) {
            for (int j = 0; j < puzzleGrid[i].length; j++) {

                int tileNumber;
                if (resumePreviousGame) {
                    tileNumber = puzzleGame.puzzleGrid[i][j];
                }
                else {
                    tileNumber = (int) (j * numTilesX + i + 1);
                }
                PuzzleTile currTile = new PuzzleTile(tileWidth, tileHeight, tileNumber, numTilesX, numTilesY, bitmap);

                Matrix matrix = new Matrix();
                gridCoords[i][j] = new float[]{puzzleBorder.thicknessX + (i * tileWidth), offset + puzzleBorder.thicknessY + (j * tileHeight)};
                currTile.setPos(gridCoords[i][j][0], gridCoords[i][j][1]);
                matrix.preTranslate(gridCoords[i][j][0], gridCoords[i][j][1]);
                currTile.getTilePath().transform(matrix);

                if (tileNumber == numTilesX * numTilesY) {
                    currTile.setEmpty();
                    myPuzzleGrid.setEmpty(i, j);
                }

                puzzleGrid[i][j] = currTile;
            }
        }

        if (resumePreviousGame) {
            //scramblePuzzle(puzzleGame.scrambleMoves);
            updatePuzzleGameGrid();
        }
        else {
            int[] scrambleList = scramblePuzzle(null);
            //int[] scrambleList = new int[100];
            puzzleGame.scrambleMoves = scrambleList;
            updatePuzzleGameGrid();
            puzzleGame.dateSolvedMDY = new int[3];
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawPath(puzzleBorder.getBorderPath(), puzzleBorder.getBorderPaint());
        if (!isPuzzleSolved()) {
            drawTiles(canvas);
        }
        else {
            //drawImage(canvas);
            drawTiles(canvas);
        }
    }

    public void drawImage(Canvas canvas) {
        canvas.save();

        canvas.clipPath(puzzleBorder.getInnerPath());
        canvas.drawBitmap(bitmap, null, puzzleBorder.getOuterRect(), null);

        canvas.restore();
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
                if (y <= offset || y >= displayHeight + offset) {
                    return false;
                }
                float innerX = x - puzzleBorder.thicknessX;
                float innerY = y - puzzleBorder.thicknessY - offset;

                if (innerX <= 0 || innerY <= 0) {
                    Log.d("PuzzleView.onDown()", "outside grid");
                    return false;
                }

                int i = (int) (innerX / tileWidth);
                int j = (int) (innerY / tileHeight);

                if (i >= numTilesX || j >= numTilesY || i < 0 || j < 0) {
                    Log.d("PuzzleView.onDown()", "outside grid");
                    return false;
                }

                previousX = x;
                previousY = y;

                if (tileIsOutOfPlace) {
                    Log.d("onTouchEvent()", "tile is out of place");
                    if (myPuzzleGrid.isTouchOnTile(x, y, outOfPlaceIndexX, outOfPlaceIndexY)) {
                        Log.d("onTouchEvent()","moving tile at " + x + "," + y);
                        gridIndexX = outOfPlaceIndexX;
                        gridIndexY = outOfPlaceIndexY;
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
                //previousX = -1;
                //previousY = -1;
                break;
        }
        return true;
    }

    public void drawTiles(Canvas canvas) {
        for (int i = 0; i < puzzleGrid.length; i++) {
            for(int j = 0; j < puzzleGrid[i].length; j++) {
                PuzzleTile currTile = puzzleGrid[i][j];
                if (!currTile.isEmpty() || isPuzzleSolved()) {

                    if (bitmap != null) {
                        canvas.save();
                        canvas.clipPath(currTile.getClipPath());
                        canvas.drawBitmap(bitmap, currTile.getSrcRect(), currTile.getDstRect(), currTile.getTilePaint());
                        canvas.restore();

                        if (!isPuzzleSolved()) {
                            canvas.drawText("" + currTile.getNumber(), currTile.posX + tileWidth / 4, currTile.posY + tileHeight / 4, currTile.getTextPaint());
                        }
                    }
                    else {
                        canvas.drawPath(currTile.getTilePath(), currTile.getTilePaint());
                        canvas.drawText("" + currTile.getNumber(), currTile.posX + tileWidth / 2, currTile.posY + tileHeight / 2, currTile.getTextPaint());
                    }
                }
            }
        }
    }

    public boolean moveTile(float x, float y) {
        if (gridIndexX >= numTilesX || gridIndexY >= numTilesY || gridIndexX < 0 || gridIndexY < 0) {
            Log.d("PuzzleView.onDown()", "outside grid");
            return false;
        }
        if (tileIsOutOfPlace && (outOfPlaceIndexY != gridIndexY || outOfPlaceIndexX != gridIndexX)) {
            return false;
        }

        float deltaX = x - previousX;
        float deltaY = y - previousY;
        //Log.d("onTouchEvent()", "deltaX: " + deltaX + ", deltaY: " + deltaY);
        if (deltaX == 0 && deltaY == 0) {
            return false;
        }
        Log.d("onTouchEvent()", "gridIndexX: " + gridIndexX + " gridIndexY: " + gridIndexY);

        int[] move = new int[1];
        move[0] = -1;
        switch (myPuzzleGrid.tryToMoveTile(gridIndexX, gridIndexY, firstX, firstY, deltaX, deltaY, x, y, move)) {
            case PuzzleGrid.TILE_MOVED:
                previousX = x;
                previousY = y;
                tileIsOutOfPlace = true;
                outOfPlaceIndexX = gridIndexX;
                outOfPlaceIndexY = gridIndexY;
                invalidate();
                break;
            case PuzzleGrid.TILE_NEW_LOCATION:
                puzzleActivity.updateMoveCount(++moveCount);
                solveMoves.add(move[0]);
                updatePuzzleGameGrid();

                int[] emptyIndices = myPuzzleGrid.getPreviousEmptyIndices();
                gridIndexX = emptyIndices[0];
                gridIndexY = emptyIndices[1];

                if (isPuzzleSolved()) {
                    puzzleGame.isSolved = true;
                    Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                    puzzleGame.dateSolvedMDY = new int[] {calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)};

                    //Toast toast = Toast.makeText(getContext(), "Puzzle solved!", Toast.LENGTH_SHORT);
                    //toast.show();
                    puzzleActivity.showPuzzleSolvedDialog();
                    updatePuzzleGameGrid();
                }
                Log.d("isPuzzleSolved()", "" + isPuzzleSolved());
            case PuzzleGrid.TILE_OLD_LOCATION:
                tileIsOutOfPlace = false;
                invalidate();
                break;
            case PuzzleGrid.TILE_NOT_MOVED:
                break;
        }
        return true;
    }

    public boolean isPuzzleSolved() {
        for (int i = 0; i < puzzleGrid.length; i++) {
            for (int j = 0; j < puzzleGrid[i].length; j++) {
                if (puzzleGrid[i][j].getNumber() != (j * numTilesX + i + 1)) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[] scramblePuzzle(int[] moves) {
        int n = (int) (NUM_GRID_SCRAMBLES * numTilesX * numTilesY);

        boolean useGivenScramble = moves != null;
        if (useGivenScramble) {
            n = moves.length;
        }

        int[] moveList = new int[n];
        int move = -1;
        for (int i = 0; i < n; i++) {

            if (!useGivenScramble) {
                move = getRandomScrambleMove(move);
                while (!isScrambleMoveValid(move)) {
                    move = getRandomScrambleMove(move);
                }
                moveList[i] = move;
            }
            else {
                move = moves[i];
            }

            switch (move) {
                case MOVE_UP:
                    myPuzzleGrid.switchMovingWithEmpty(myPuzzleGrid.emptyX, myPuzzleGrid.emptyY - 1);
                    break;
                case MOVE_DOWN:
                    myPuzzleGrid.switchMovingWithEmpty(myPuzzleGrid.emptyX, myPuzzleGrid.emptyY + 1);
                    break;
                case MOVE_LEFT:
                    myPuzzleGrid.switchMovingWithEmpty(myPuzzleGrid.emptyX - 1, myPuzzleGrid.emptyY);
                    break;
                case MOVE_RIGHT:
                    myPuzzleGrid.switchMovingWithEmpty(myPuzzleGrid.emptyX + 1, myPuzzleGrid.emptyY);
                    break;

            }

        }

        return moveList;
    }

    public boolean isScrambleMoveValid(int move) {
        int x = myPuzzleGrid.emptyX;
        int y = myPuzzleGrid.emptyY;

        switch (move) {
            case MOVE_UP:
                if (y - 1 < 0) {
                    return false;
                }
                break;
            case MOVE_DOWN:
                if (y + 1 >= numTilesY) {
                    return false;
                }
                break;
            case MOVE_LEFT:
                if (x - 1 < 0) {
                    return false;
                }
                break;
            case MOVE_RIGHT:
                if (x + 1 >= numTilesX) {
                    return false;
                }
                break;
        }

        return true;
    }

    public int getRandomScrambleMove(int lastMove) {
        Random random = new Random();
        int[][] moves = new int[][] {{MOVE_UP, MOVE_DOWN}, {MOVE_LEFT, MOVE_RIGHT}};
        int randomI = random.nextInt(moves[0].length);
        int randomJ = random.nextInt(moves[1].length);

        while (lastMove == moves[randomI][randomJ]) {
            randomI = random.nextInt(moves[0].length);
            randomJ = random.nextInt(moves[1].length);
        }

        return moves[randomI][randomJ];
    }

    private void updatePuzzleGameGrid() {
        int[][] tileLocations = new int[(int) numTilesX][(int) numTilesY];
        for (int i = 0; i < numTilesX; i++) {
            for (int j = 0; j < numTilesY; j++) {
                tileLocations[i][j] = puzzleGrid[i][j].getNumber();
            }
        }
        puzzleGame.puzzleGrid = tileLocations;
    }
}
