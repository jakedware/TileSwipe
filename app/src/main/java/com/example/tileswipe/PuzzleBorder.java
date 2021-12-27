package com.example.tileswipe;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;

public class PuzzleBorder {
    protected static final float BORDER_CORNER_RX = 50;
    protected static final float BORDER_CORNER_RY = 50;
    protected static final float BORDER_PERCENT = (float) 0.025;
    protected float thicknessX;
    protected float thicknessY;
    private Path borderPath;
    private Paint borderPaint;

    public PuzzleBorder(float displayWidth, float displayHeight) {
        borderPath = new Path();
        borderPath.setFillType(Path.FillType.EVEN_ODD);

        thicknessX = displayWidth * BORDER_PERCENT;
        thicknessY = displayHeight * BORDER_PERCENT;

        RectF borderRect = new RectF(0, 0, displayWidth, displayHeight);
        borderPath.addRoundRect(borderRect, BORDER_CORNER_RX, BORDER_CORNER_RY, Path.Direction.CW);

        float innerLeft = (displayWidth * BORDER_PERCENT);
        float innerTop = (displayHeight * BORDER_PERCENT);
        float innerRight = displayWidth - innerLeft;
        float innerBottom = displayHeight - innerTop;
        RectF innerRect = new RectF(innerLeft, innerTop, innerRight, innerBottom);
        borderPath.addRoundRect(innerRect, BORDER_CORNER_RX, BORDER_CORNER_RY, Path.Direction.CW);

        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
    }

    public Path getBorderPath() {
        return borderPath;
    }

    public Paint getBorderPaint() {
        return borderPaint;
    }
}
