package com.yuyakaido.android.cardstackview;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout.LayoutParams;

public class CardUtil {

    private CardUtil() {}

    public static void scale(View view, int pixel) {
        CardUtil.scale(view, (LayoutParams) view.getLayoutParams(), pixel);
    }

    public static LayoutParams scale(View view, LayoutParams params, int pixel) {
        params = CardUtil.cloneParams(params);
        params.leftMargin -= pixel;
        params.rightMargin -= pixel;
        params.topMargin -= pixel;
        params.bottomMargin -= pixel;
        view.setLayoutParams(params);
        return params;
    }

    public static void move(View view, int topBottom, int leftRight) {
        CardUtil.move(view, (LayoutParams) view.getLayoutParams(), topBottom, leftRight);
    }

    public static void move(View v, LayoutParams params, int topBottom, int leftRight) {
        params = CardUtil.cloneParams(params);
        params.leftMargin += leftRight;
        params.rightMargin -= leftRight;
        params.topMargin -= topBottom;
        params.bottomMargin += topBottom;
        v.setLayoutParams(params);
    }

    public static LayoutParams getMoveParams(View v, int topBottom, int leftRight) {
        LayoutParams original = (LayoutParams) v.getLayoutParams();
        LayoutParams params = CardUtil.cloneParams(original);
        params.leftMargin += leftRight;
        params.rightMargin -= leftRight;
        params.topMargin -= topBottom;
        params.bottomMargin += topBottom;
        return params;
    }

    public static LayoutParams cloneParams(LayoutParams params) {
        LayoutParams result = new LayoutParams(params.width, params.height);
        result.leftMargin = params.leftMargin;
        result.topMargin = params.topMargin;
        result.rightMargin = params.rightMargin;
        result.bottomMargin = params.bottomMargin;
        int[] rules = params.getRules();
        for (int i = 0, length = rules.length; i < length; i++) {
            result.addRule(i, rules[i]);
        }
        return result;
    }

    public static float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static Direction getDirection(float x1, float y1, float x2, float y2) {
        if (x2 > x1) { // RIGHT
            if (y2 > y1) { // BOTTOM
                return Direction.BottomRight;
            } else { // TOP
                return Direction.TopRight;
            }
        } else { // LEFT
            if (y2 > y1) { // BOTTOM
                return Direction.BottomLeft;
            } else { // TOP
                return Direction.TopLeft;
            }
        }
    }

    public static int getDisplayWidth(Context context) {
        return CardUtil.getDisplaySize(context).x;
    }

    public static int getDisplayHeight(Context context) {
        return CardUtil.getDisplaySize(context).y;
    }

    public static Point getDisplaySize(Context context) {
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        return point;
    }

}
