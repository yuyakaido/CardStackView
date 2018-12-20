package com.yuyakaido.android.cardstackview.internal;

import android.support.v7.widget.RecyclerView;

import com.yuyakaido.android.cardstackview.Direction;

public class CardStackState {
    public Status status = Status.Idle;
    public int width = 0;
    public int height = 0;
    public int dx = 0;
    public int dy = 0;
    public int topPosition = 0;
    public int targetPosition = RecyclerView.NO_POSITION;
    public float proportion = 0.0f;

    public enum Status {
        Idle, Dragging, RewindAnimating, PrepareSwipeAnimation, SwipeAnimating
    }

    public void next(Status state) {
        this.status = state;
    }

    public Direction getDirection() {
        if (Math.abs(dy) < Math.abs(dx)) {
            if (dx < 0.0f) {
                return Direction.Left;
            } else {
                return Direction.Right;
            }
        } else {
            if (dy < 0.0f) {
                return Direction.Top;
            } else {
                return Direction.Bottom;
            }
        }
    }

    public float getRatio() {
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        float ratio;
        if (absDx < absDy) {
            ratio = absDy / (height / 2.0f);
        } else {
            ratio = absDx / (width / 2.0f);
        }
        return Math.min(ratio, 1.0f);
    }

}
