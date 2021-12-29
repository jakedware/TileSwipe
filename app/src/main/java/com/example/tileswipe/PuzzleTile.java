package com.example.tileswipe;


import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;

public class PuzzleTile {
    protected static final int TILE_CORNER_RX = 50;
    protected static final int TILE_CORNER_RY = 50;
    protected static final Path.Direction TILE_DIRECTION = Path.Direction.CW;
    private Path tilePath;
    private Paint tilePaint;
    private Paint textPaint;
    protected float posX;
    protected float posY;
    private boolean isEmpty = false;
    private boolean isMoving = false;
    protected Matrix matrix;
    private final int TILE_NUMBER;
    private float textSize;

    public PuzzleTile(float tileWidth, float tileHeight, int number) {
        TILE_NUMBER = number;
        tilePaint = new Paint();
        //tilePaint.setColor(Color.WHITE);
        tilePaint.setColor(Color.GRAY);

        textPaint = new TextPaint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textSize = tileHeight / 8;
        textPaint.setTextSize(textSize);


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

    public Paint getTextPaint() {
        return  textPaint;
    }

    public int getNumber() {
        return  TILE_NUMBER;
    }

    public void setEmpty() {
        isEmpty = true;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setPos(float x, float y) {
        posX = x;
        posY = y;
    }

    public void moveX (float delta) {
        posX += delta;
    }

    public void moveY (float delta) {
        posY += delta;
    }

    public float getTextSize() {
        return  textSize;
    }

    public void startMoving() {
        isMoving = true;
    }

    public void stopMoving() {
        isMoving = false;
    }

    public boolean isMoving() {
        return isMoving;
    }
}
