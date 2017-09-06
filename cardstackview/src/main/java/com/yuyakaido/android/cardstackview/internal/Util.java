package com.yuyakaido.android.cardstackview.internal;

import android.content.Context;
import android.graphics.Point;

public class Util {

    private Util() {}

    public static float toPx(Context context, float dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public static double getRadian(float x1, float y1, float x2, float y2) {
        float width = x2 - x1;
        float height = y1 - y2;
        return Math.atan(Math.abs(height) / Math.abs(width));
    }

    public static Point getTargetPoint(float x1, float y1, float x2, float y2) {
        float radius = 2000f;
        double radian = Util.getRadian(x1, y1, x2, y2);

        Quadrant quadrant = getQuadrant(x1, y1, x2, y2);
        if (quadrant == Quadrant.TopLeft) {
            double degree = Math.toDegrees(radian);
            degree = 180 - degree;
            radian = Math.toRadians(degree);
        } else if (quadrant == Quadrant.BottomLeft) {
            double degree = Math.toDegrees(radian);
            degree = 180 + degree;
            radian = Math.toRadians(degree);
        } else if (quadrant == Quadrant.BottomRight) {
            double degree = Math.toDegrees(radian);
            degree = 360 - degree;
            radian = Math.toRadians(degree);
        } else {
            double degree = Math.toDegrees(radian);
            radian = Math.toRadians(degree);
        }

        double x = radius * Math.cos(radian);
        double y = radius * Math.sin(radian);

        return new Point((int) x, (int) y);
    }

    public static Quadrant getQuadrant(float x1, float y1, float x2, float y2) {
        if (x2 > x1) { // Right
            if (y2 > y1) { // Bottom
                return Quadrant.BottomRight;
            } else { // Top
                return Quadrant.TopRight;
            }
        } else { // Left
            if (y2 > y1) { // Bottom
                return Quadrant.BottomLeft;
            } else { // Top
                return Quadrant.TopLeft;
            }
        }
    }

}
