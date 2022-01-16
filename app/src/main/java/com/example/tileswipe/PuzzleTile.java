package com.example.tileswipe;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

public class PuzzleTile {
    public static final String TILE_COLOR_KEY = "tile_color_key";
    public static final String NUMBER_COLOR_KEY = "number_color_key";
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
    private float tileWidth;
    private float tileHeight;
    private Path clipPath;
    private Bitmap bitmap;
    private Rect srcRect;
    private Rect dstRect;
    private float numTilesX;
    private float numTilesY;

    public PuzzleTile(float tileWidth, float tileHeight, int number, float numTilesX, float numTilesY, Bitmap bitmap, int tileColor, int numberColor) {
        TILE_NUMBER = number;
        this.numTilesX = numTilesX;
        this.numTilesY = numTilesY;
        this.bitmap = bitmap;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        tilePaint = new Paint();
        tilePaint.setColor(tileColor);

        textPaint = new TextPaint();
        textPaint.setColor(numberColor);
        textPaint.setTextAlign(Paint.Align.CENTER);

        textSize = tileHeight / 8;
        textPaint.setTextSize(textSize);


        tilePath = new Path();
        RectF rect = new RectF(0, 0, tileWidth, tileHeight);
        tilePath.addRoundRect(rect, TILE_CORNER_RX, TILE_CORNER_RY, TILE_DIRECTION);

        if (bitmap != null) {
            clipPath = new Path();
            clipPath.addRoundRect(posX, posY, posX + tileWidth, posY + tileHeight, TILE_CORNER_RX, TILE_CORNER_RY, TILE_DIRECTION);


            float tileBitmapY = bitmap.getHeight() / numTilesY;
            float tileBitmapX = bitmap.getWidth() / numTilesX;
            int x = (int) ((getNumber() - 1) % numTilesX);
            int y = (int) ((getNumber() - 1) / numTilesX);
            float currBitmapX = tileBitmapX * x;
            float currBitmapY = tileBitmapY * y;
            srcRect = new Rect((int) currBitmapX, (int) currBitmapY, (int) (currBitmapX + tileBitmapX), (int) (currBitmapY + tileBitmapY));
            dstRect = new Rect((int) (posX), (int) (posY), (int) (posX + tileWidth), (int) (posY + tileHeight));
        }
    }

    public Path getTilePath() {
        if (isEmpty()) {
            return new Path();
        }
        return tilePath;
    }

    public Path getClipPath() {
        return clipPath;
    }

    public Rect getSrcRect() {
        return (srcRect);
    }

    public Rect getDstRect() {
        return (dstRect);
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

        if (bitmap != null) {
            clipPath = new Path();
            clipPath.addRoundRect(posX, posY, posX + tileWidth, posY + tileHeight, TILE_CORNER_RX, TILE_CORNER_RY, TILE_DIRECTION);

            /*
            float tileBitmapY = bitmap.getHeight() / numTilesY;
            float tileBitmapX = bitmap.getWidth() / numTilesX;
            int i = (int) ((getNumber() - 1) % numTilesX);
            int j = (int) ((getNumber() - 1) / numTilesX);
            float currBitmapX = tileBitmapX * i;
            float currBitmapY = tileBitmapY * j;
            srcRect = new Rect((int) currBitmapX, (int) currBitmapY, (int) (currBitmapX + tileBitmapX), (int) (currBitmapY + tileBitmapY));

             */
            dstRect = new Rect((int) (posX), (int) (posY), (int) (posX + tileWidth), (int) (posY + tileHeight));
        }
    }

    public void moveX (float delta) {
        setPos(posX + delta, posY);
    }

    public void moveY (float delta) {
        setPos(posX, posY + delta);
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
