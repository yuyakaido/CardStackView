package com.yuyakaido.android.cardstackview.internal;

import android.graphics.Point;

import com.yuyakaido.android.cardstackview.Quadrant;

public class Util {

    private Util() {}

    public static float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static Point getTargetPoint(float x1, float y1, float x2, float y2) {
        float radius = 2000f;
        float width = x2 - x1;
        float height = y1 - y2;
        double radian = Math.atan(Math.abs(height) / Math.abs(width));

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
        if (x2 > x1) { // RIGHT
            if (y2 > y1) { // BOTTOM
                return Quadrant.BottomRight;
            } else { // TOP
                return Quadrant.TopRight;
            }
        } else { // LEFT
            if (y2 > y1) { // BOTTOM
                return Quadrant.BottomLeft;
            } else { // TOP
                return Quadrant.TopLeft;
            }
        }
    }

}
