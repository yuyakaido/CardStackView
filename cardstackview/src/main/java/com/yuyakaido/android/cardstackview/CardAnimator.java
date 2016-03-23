package com.yuyakaido.android.cardstackview;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardAnimator {
    private Context context;
    private List<View> containers;
    private float rotation;
    private Map<View, LayoutParams> cardParams = new HashMap<>();
    private LayoutParams[] remoteParams = new LayoutParams[4];
    private LayoutParams baseParams;

    public CardAnimator(Context context, List<View> containers) {
        this.context = context;
        this.containers = containers;
        init();
    }

    private void init() {
        for (View v : containers) {
            LayoutParams params = (LayoutParams) v.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            params.width = LayoutParams.MATCH_PARENT;
            params.height = LayoutParams.MATCH_PARENT;
        }

        baseParams = CardUtil.cloneParams((LayoutParams) containers.get(0).getLayoutParams());

        initCards();

        for (View v : containers) {
            cardParams.put(v, CardUtil.cloneParams((LayoutParams) v.getLayoutParams()));
        }

        initRemoteParams();
    }

    public void initCards() {
        int size = containers.size();
        for (View v : containers) {
            int index =  containers.indexOf(v);
            if (index != 0) {
                index -= 1;
            }
            LayoutParams params = CardUtil.cloneParams(baseParams);
            v.setLayoutParams(params);

            CardUtil.scale(v, -(size - index - 1) * 5);
            CardUtil.move(v, index * 20, 0);
            v.setRotation(0);
        }
    }

    public void initRemoteParams() {
        int width = CardUtil.getDisplayWidth(context);
        int height = CardUtil.getDisplayHeight(context);

        View topView = getTopView();
        remoteParams[0] = CardUtil.getMoveParams(topView, height, -width);
        remoteParams[1] = CardUtil.getMoveParams(topView, height, width);
        remoteParams[2] = CardUtil.getMoveParams(topView, -height, -width);
        remoteParams[3] = CardUtil.getMoveParams(topView, -height, width);
    }

    public View getTopView() {
        return containers.get(containers.size() - 1);
    }

    public void moveToBack(View child) {
        ViewGroup parent = (ViewGroup) child.getParent();
        if (null != parent) {
            parent.removeView(child);
            parent.addView(child, 0);
        }
    }

    public void reorder() {
        View topView = getTopView();
        moveToBack(topView);

        for (int i = containers.size() - 1; i > 0; i--) {
            containers.set(i, containers.get(i - 1));
        }

        containers.set(0, topView);
    }

    public void discard(Direction direction, final AnimatorListener listener) {
        AnimatorSet animatorSet = new AnimatorSet();
        List<Animator> animators = new ArrayList<>();

        final View topView = getTopView();
        LayoutParams topBeginParams = CardUtil.cloneParams((LayoutParams) topView.getLayoutParams());
        LayoutParams topEndParams = remoteParams[direction.getIndex()];
        ValueAnimator topAnimator = ValueAnimator.ofObject(
                new LayoutParamsEvaluator(), topBeginParams, topEndParams);
        topAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator value) {
                topView.setLayoutParams((LayoutParams) value.getAnimatedValue());
            }
        });

        topAnimator.setDuration(250);
        animators.add(topAnimator);

        for (int i = 0, size = containers.size(); i < size - 1; i++) {
            final View currentView = containers.get(i);
            View nextView = containers.get(i + 1);
            LayoutParams beginParams = CardUtil.cloneParams((LayoutParams) currentView.getLayoutParams());
            LayoutParams endParams = cardParams.get(nextView);
            ValueAnimator animator = ValueAnimator.ofObject(
                    new LayoutParamsEvaluator(), beginParams, endParams);
            animator.setDuration(250);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator value) {
                    currentView.setLayoutParams((LayoutParams) value.getAnimatedValue());
                }
            });
            animators.add(animator);
        }

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                reorder();
                if (listener != null) {
                    listener.onAnimationEnd(animation);
                }
                cardParams = new HashMap<>();
                for (View v : containers) {
                    cardParams.put(v, CardUtil.cloneParams((LayoutParams) v.getLayoutParams()));
                }
            }
        });

        animatorSet.playTogether(animators);
        animatorSet.start();
    }

    public void reverse() {
        final View topView = getTopView();
        ValueAnimator topAnimator = ValueAnimator.ofFloat(rotation, 0f);
        topAnimator.setDuration(250);
        topAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator v) {
                topView.setRotation((float) v.getAnimatedValue());
            }
        });
        topAnimator.start();

        for (final View v : containers) {
            LayoutParams beginParams = (LayoutParams) v.getLayoutParams();
            LayoutParams endLayout = CardUtil.cloneParams(beginParams);
            ValueAnimator animator = ValueAnimator.ofObject(
                    new LayoutParamsEvaluator(), endLayout, cardParams.get(v));
            animator.setDuration(250);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator value) {
                    v.setLayoutParams((LayoutParams) value.getAnimatedValue());
                }
            });
            animator.start();
        }
    }

    public void drag(MotionEvent e1, MotionEvent e2) {
        View topView =  getTopView();

        float rotationCoefficient = 20f;

        LayoutParams beforeParams = cardParams.get(topView);
        LayoutParams afterParams = (LayoutParams) topView.getLayoutParams();
        int distanceX = (int) (e2.getRawX() - e1.getRawX());
        int distanceY = (int) (e2.getRawY() - e1.getRawY());

        afterParams.leftMargin  = beforeParams.leftMargin + distanceX;
        afterParams.rightMargin = beforeParams.rightMargin - distanceX;
        afterParams.topMargin  = beforeParams.topMargin + distanceY;
        afterParams.bottomMargin  = beforeParams.bottomMargin - distanceY;

        rotation = distanceX / rotationCoefficient;
        topView.setRotation(rotation);
        topView.setLayoutParams(afterParams);

        for (View v : containers) {
            int index  = containers.indexOf(v);
            if (v != getTopView() && index != 0) {
                LayoutParams l = CardUtil.scale(
                        v, cardParams.get(v), (int) (Math.abs(distanceX) * 0.005));
                CardUtil.move(v, l, (int) (Math.abs(distanceX) * 0.025), 0);
            }
        }
    }

}
