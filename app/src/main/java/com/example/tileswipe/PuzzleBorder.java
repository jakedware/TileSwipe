package com.example.tileswipe;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;

public class PuzzleBorder {
    public static final String BORDER_COLOR_KEY = ChangePuzzleBoardColorsActivity.ButtonType.BORDER_BUTTON.name();
    protected static final float BORDER_CORNER_RX = 50;
    protected static final float BORDER_CORNER_RY = 50;
    protected static final float BORDER_PERCENT = (float) 0.025;
    protected float thicknessX;
    protected float thicknessY;
    private Path borderPath;
    private Paint borderPaint;
    private RectF innerRect;
    private RectF outerRect;
    private Path innerPath;

    public PuzzleBorder(float displayWidth, float displayHeight, int offset, int borderColor) {
        borderPath = new Path();
        borderPath.setFillType(Path.FillType.EVEN_ODD);

        thicknessX = displayWidth * BORDER_PERCENT;
        thicknessY = displayHeight * BORDER_PERCENT;

        Path outerPath = new Path();
        outerRect = new RectF(0, offset, displayWidth, displayHeight + offset);
        borderPath.addRoundRect(outerRect, BORDER_CORNER_RX, BORDER_CORNER_RY, Path.Direction.CW);

        float innerLeft = (displayWidth * BORDER_PERCENT);
        float innerTop = (displayHeight * BORDER_PERCENT);
        float innerRight = displayWidth - innerLeft;
        float innerBottom = displayHeight - innerTop;
        innerRect = new RectF(innerLeft, innerTop + offset, innerRight, innerBottom + offset);
        borderPath.addRoundRect(innerRect, BORDER_CORNER_RX, BORDER_CORNER_RY, Path.Direction.CW);

        innerPath = new Path();
        innerPath.addRoundRect(innerRect, BORDER_CORNER_RX, BORDER_CORNER_RY, Path.Direction.CW);

        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
    }

    public Path getBorderPath() {
        return borderPath;
    }

    public Paint getBorderPaint() {
        return borderPaint;
    }

    public Path getInnerPath() {
        return innerPath;
    }

    public RectF getOuterRect() {
        return outerRect;
    }
}
