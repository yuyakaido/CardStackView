package com.yuyakaido.android.cardstackview.sample;

import android.content.Context;
import android.view.MotionEvent;

import com.yuyakaido.android.cardstackview.internal.CardContainerView;

/**
 * Created by luongvo on 2/26/18.
 */

public class MyCardContainerView extends CardContainerView {

    public MyCardContainerView(Context context) {
        super(context);
    }

    /**
     * Spy or monitor all the events include those been sent to child views
     * ref: https://stackoverflow.com/a/35113182/2100084
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // handle the motion event even if a child returns true in OnTouchEvent
        // the MotionEvent may have been canceled by the child view
        handleTouchEvent(event);

        super.dispatchTouchEvent(event);

        // to keep receive event that follow down event
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // OnTouchEvent is called manually, if OnTouchEvent propagates back to this layout do
        // nothing as it was already handled.
        return true;
    }
}
