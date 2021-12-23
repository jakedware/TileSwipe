package com.example.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class PuzzleView extends View {
    private Paint myPaint;
    private Path myPath;
    private Paint innerPaint;
    private Path innerPath;
    int displayWidth;
    int displayHeight;
    private Resources.Theme theme;
    Path[][] puzzleBoard;


    public PuzzleView(Context context, int displayWidth, int displayHeight, Resources.Theme theme) {
        super(context);

        this.theme = theme;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        myPaint = new Paint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            myPaint.setColor(getResources().getColor(R.color.puzzle_border_color, theme));
        }

        myPath = new Path();
        myPath.setFillType(Path.FillType.EVEN_ODD);

        float borderPercent = (float) 0.025;
        float rx = 50;
        float ry = 50;
        float left = 0;
        float top = 0;
        float right = displayWidth;
        float bottom = displayHeight;
        RectF borderRect = new RectF(left, top, right, bottom);
        myPath.addRoundRect(borderRect,rx,ry, Path.Direction.CW);

        float innerLeft = (displayWidth * borderPercent);
        float innerTop = (displayHeight * borderPercent);
        float innerRight = displayWidth - innerLeft;
        float innerBottom = displayHeight - innerTop;
        RectF innerRect = new RectF(innerLeft, innerTop, innerRight, innerBottom);
        innerPaint = new Paint();
        innerPaint.setColor(Color.RED);
        myPath.addRoundRect(innerRect, rx, ry, Path.Direction.CW);


        int numTilesX = 4;
        int numTilesY = 6;
        puzzleBoard = new Path[numTilesX][numTilesY];
        float tileWidth = innerRight - innerLeft;
        float tileHeight = innerBottom - innerTop;
        Paint tilePaint = new Paint();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tilePaint.setColor(getResources().getColor(R.color.tile_background_color, theme));
        }
        RectF tileRect = new RectF(0, 0, tileWidth, tileHeight);
        Path tilePath = new Path();
        tilePath.addRoundRect(tileRect, rx, ry, Path.Direction.CW);
        for (int i = 0; i < numTilesX; i++) {
            for(int j = 0; j < numTilesY; j++) {
                puzzleBoard[i][j] = new Path(tilePath);
            }
        }

    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawPath(myPath, myPaint);
        //canvas.drawPath(innerPath, innerPaint);
    }

    public void drawTiles() {
        for (int i = 0; i < puzzleBoard.length; i++) {
            for(int j = 0; j < puzzleBoard[i].length; j++) {
            }
        }
    }
}
