package com.example.tileswipe;


import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class PuzzleTile {
    protected static final int TILE_CORNER_RX = 50;
    protected static final int TILE_CORNER_RY = 50;
    protected static final Path.Direction TILE_DIRECTION = Path.Direction.CW;
    private Path tilePath;
    private Paint tilePaint;
    private boolean isEmpty = false;
    protected Matrix matrix;

    public PuzzleTile(float tileWidth, float tileHeight) {
        tilePaint = new Paint();
        //tilePaint.setColor(Color.WHITE);
        tilePaint.setColor(Color.GRAY);

        tilePath = new Path();
        RectF rect = new RectF(0, 0, tileWidth, tileHeight);
        tilePath.addRoundRect(rect, TILE_CORNER_RX, TILE_CORNER_RY, TILE_DIRECTION);
    }

    public Path getTilePath() {
        if (isEmpty()) {
            return new Path();
        }
        return tilePath;
    }

    public Paint getTilePaint() {
        return tilePaint;
    }
    public void setEmpty() {
        isEmpty = true;
    }

    public boolean isEmpty() {
        return isEmpty;
    }
}
