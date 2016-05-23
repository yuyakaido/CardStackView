package com.yuyakaido.android.cardstackview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class CardAnimator {
    private Context context;
    private List<ViewGroup> containers;
    private float rotation;
    private Map<View, LayoutParams> cardParams = new HashMap<>();
    private LayoutParams[] remoteParams = new LayoutParams[4];
    private LayoutParams baseParams;

    public CardAnimator(Context context, List<ViewGroup> containers) {
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
        for (ViewGroup v : containers) {
            int index =  containers.indexOf(v);
            if (index != 0) {
                index -= 1;
            }
            LayoutParams params = CardUtil.cloneParams(baseParams);
            v.setLayoutParams(params);

            clearAlpha(v);
            clearScale(v);
            clearTranslation(v);

            CardUtil.scale(v, -(size - index - 1) * 5);
            CardUtil.move(v, index * 20, 0);
            v.setRotation(0);
        }
    }

    public void clearTranslation(ViewGroup viewGroup) {
        if (viewGroup != null) {
            viewGroup.setTranslationX(0);
            viewGroup.setTranslationY(0);
        }
    }

    public void clearScale(ViewGroup viewGroup) {
        if (viewGroup != null) {
            viewGroup.setScaleX(1.0f);
            viewGroup.setScaleY(1.0f);
        }
    }

    public void clearAlpha(ViewGroup viewGroup) {
        if (viewGroup != null) {
            viewGroup.setAlpha(1.0f);
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

    public ViewGroup getTopView() {
        return containers.get(containers.size() - 1);
    }

    public ViewGroup getBottomView() {
        return containers.get(0);
    }

    public void moveToBottom(ViewGroup container) {
        ViewGroup parent = (ViewGroup) container.getParent();
        if (parent != null) {
            parent.removeView(container);
            parent.addView(container, 0);
        }
    }

    public void moveToTop(ViewGroup container, View child) {
        ViewGroup parent = (ViewGroup) container.getParent();
        if (parent != null) {
            parent.removeView(container);
            parent.addView(container);

            container.removeAllViews();
            container.addView(child);
            container.setVisibility(View.VISIBLE);
        }
    }

    public void reorderForDiscard() {
        ViewGroup topView = getTopView();
        moveToBottom(topView);

        for (int i = containers.size() - 1; i > 0; i--) {
            containers.set(i, containers.get(i - 1));
        }

        containers.set(0, topView);
    }

    public void reorderForReverse(View prevView) {
        ViewGroup bottomView = getBottomView();
        moveToTop(bottomView, prevView);

        for (int i = 0, size = containers.size() - 1; i < size; i++) {
            containers.set(i, containers.get(i + 1));
        }

        containers.set(containers.size() - 1, bottomView);
    }

    public void reverse(Direction direction, View prevView, final AnimatorListener listener) {
        reorderForReverse(prevView);
        initCards();

        AnimatorSet animatorSet = new AnimatorSet();
        List<Animator> animators = new ArrayList<>();

        final View topView = getTopView();
        LayoutParams topBeginParams = remoteParams[direction.getIndex()];
        LayoutParams topEndParams = CardUtil.cloneParams((LayoutParams) topView.getLayoutParams());
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

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
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
                reorderForDiscard();
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

    public void discard(Direction direction, ObjectAnimator topAnimator, final AnimatorListener listener) {
        AnimatorSet animatorSet = new AnimatorSet();
        List<Animator> animators = new ArrayList<>();
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
                reorderForDiscard();
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

    public void moveToOrigin() {
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

        for (ViewGroup v : containers) {
            int index  = containers.indexOf(v);
            if (v != getTopView() && index != 0) {
                LayoutParams l = CardUtil.scale(
                        v, cardParams.get(v), (int) (Math.abs(distanceX) * 0.005));
                CardUtil.move(v, l, (int) (Math.abs(distanceX) * 0.025), 0);
            }
        }
    }

}
