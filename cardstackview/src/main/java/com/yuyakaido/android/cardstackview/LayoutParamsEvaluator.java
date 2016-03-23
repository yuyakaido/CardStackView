package com.yuyakaido.android.cardstackview;

import android.animation.TypeEvaluator;
import android.widget.RelativeLayout.LayoutParams;

public class LayoutParamsEvaluator implements TypeEvaluator<LayoutParams> {

    @Override
    public LayoutParams evaluate(float fraction, LayoutParams begin, LayoutParams end) {
        LayoutParams result = CardUtil.cloneParams(begin);
        result.leftMargin += ((end.leftMargin - begin.leftMargin) * fraction);
        result.rightMargin += ((end.rightMargin - begin.rightMargin) * fraction);
        result.topMargin += ((end.topMargin - begin.topMargin) * fraction);
        result.bottomMargin += ((end.bottomMargin - begin.bottomMargin) * fraction);
        return result;
    }

}
