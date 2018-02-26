package com.yuyakaido.android.cardstackview.sample;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;

import com.yuyakaido.android.cardstackview.internal.CardContainerView;

/**
 * Created by luongvo on 2/26/18.
 */

public class MyCardContainerView extends CardContainerView implements ViewPager.OnPageChangeListener {

    private static int THRESHOLD_X = 30;
    private static int THRESHOLD_Y = 0;
    private ViewPager viewPager;
    private double oldX, oldY;
    private boolean cardDragging;
    private boolean pageScrolling;

    public MyCardContainerView(Context context) {
        super(context);
    }

    /**
     * Spy or monitor all the events include those been sent to child views
     * ref: https://stackoverflow.com/a/35113182/2100084
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean canDragging = true;

        // don't check touch point when dragging, make a smooth dragging
        if (!cardDragging && viewPager != null && viewPager.getAdapter().getCount() > 1) {
            // disable for move or up events by default, make higher priority for page scrolling
            canDragging = false;

            int newX = (int) event.getX();
            int newY = (int) event.getY();
            int pageIndex = viewPager.getCurrentItem();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // send down event to prepare for next dragging (move event)
                    canDragging = true;

                    oldX = newX;
                    oldY = newY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    // swipe down for top
                    if ((pageIndex == 0 && newY - oldY > THRESHOLD_Y) ||
                            // or swipe up for bottom
                            (pageIndex == viewPager.getAdapter().getCount() - 1 && newY - oldY < -THRESHOLD_Y) ||
                            // or swipe left or right
                            Math.abs(newX - oldX) > THRESHOLD_X) {
                        // enable card dragging if can not scroll the pager vertically
                        canDragging = true;
                    }
                    break;
            }
        }

        if (canDragging && !pageScrolling) {
            // handle the motion event even if a child returns true in OnTouchEvent
            // the MotionEvent may have been canceled by the child view
            handleTouchEvent(event);

            // mark dragging state
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    cardDragging = true;
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    cardDragging = false;
                    break;
            }
        }

        // don't send when dragging
        if (!cardDragging) {
            // send event to child views: button, viewpager, etc
            super.dispatchTouchEvent(event);
        }

        // to keep receive event that follow down event
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // OnTouchEvent is called manually, if OnTouchEvent propagates back to this layout do
        // nothing as it was already handled.
        return true;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        // mark page scrolling state
        pageScrolling = state != ViewPager.SCROLL_STATE_IDLE;
    }

    public void setViewPager(ViewPager viewPager) {
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(this);
    }
}
