package com.yuyakaido.android.cardstackview.internal;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;

public class CardStackSmoothScroller extends RecyclerView.SmoothScroller {

    public enum ScrollType {
        AutomaticSwipe,
        AutomaticRewind,
        ManualSwipe,
        ManualCancel
    }

    private ScrollType type;
    private CardStackLayoutManager manager;

    public CardStackSmoothScroller(
            ScrollType type,
            CardStackLayoutManager manager
    ) {
        this.type = type;
        this.manager = manager;
    }

    @Override
    protected void onSeekTargetStep(
            int dx,
            int dy,
            @NonNull RecyclerView.State state,
            @NonNull Action action
    ) {
        if (type == ScrollType.AutomaticRewind) {
            // ■ 概要
            // ここでViewのRemoveを行わないとRewindが無限ループに陥ってしまう
            // ■ 再現手順
            // 1. `manager.removeAllViews();`をコメントアウト
            // 2. AutomaticSwipeを1度実行する
            // 3. AutomaticRewindを1度実行する
            // 4. AutomaticSwipeを1度実行する
            // 5. AutomaticRewindを1度実行する -> これが無限ループに陥ってしまう
            // ■ 調査結果
            // SmoothScroller.onStartが呼ばれたタイミングでは、Rewind対象のViewが見つからない（正常挙動）
            // Rewind処理開始後のupdateで対象のViewが追加されるはずだが、現状の実装では正常に追加されない
            // 結果として、Rewind対象のViewが永遠に見つからずに無限ループに陥ってしまう
            // ■ 副作用
            // ViewのRemoveを行っているため、表示対象となっているViewの再生成が実行されてしまう
            // これによってパフォーマンス上の問題が発生する可能性がある
            manager.removeAllViews();
            RewindAnimationSetting setting = manager.getCardStackSetting().rewindAnimationSetting;
            action.update(
                    -getDx(setting),
                    -getDy(setting),
                    1,
                    setting.getInterpolator()
            );
        }
    }

    @Override
    protected void onTargetFound(
            @NonNull View targetView,
            @NonNull RecyclerView.State state,
            @NonNull Action action
    ) {
        int x = (int) targetView.getTranslationX();
        int y = (int) targetView.getTranslationY();
        AnimationSetting setting;
        switch (type) {
            case AutomaticSwipe:
                setting = manager.getCardStackSetting().swipeAnimationSetting;
                action.update(
                        -getDx(setting),
                        -getDy(setting),
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
            case AutomaticRewind:
                setting = manager.getCardStackSetting().rewindAnimationSetting;
                action.update(
                        x,
                        y,
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
            case ManualSwipe:
                int dx = -x * 10;
                int dy = -y * 10;
                setting = manager.getCardStackSetting().swipeAnimationSetting;
                action.update(
                        dx,
                        dy,
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
            case ManualCancel:
                setting = manager.getCardStackSetting().rewindAnimationSetting;
                action.update(
                        x,
                        y,
                        setting.getDuration(),
                        setting.getInterpolator()
                );
                break;
        }
    }

    @Override
    protected void onStart() {
        CardStackListener listener = manager.getCardStackListener();
        CardStackState state = manager.getCardStackState();
        switch (type) {
            case AutomaticSwipe:
                state.next(CardStackState.Status.AutomaticSwipeAnimating);
                listener.onCardDisappeared(manager.getTopView(), manager.getTopPosition());
                break;
            case AutomaticRewind:
                state.next(CardStackState.Status.RewindAnimating);
                break;
            case ManualSwipe:
                state.next(CardStackState.Status.ManualSwipeAnimating);
                listener.onCardDisappeared(manager.getTopView(), manager.getTopPosition());
                break;
            case ManualCancel:
                state.next(CardStackState.Status.RewindAnimating);
                break;
        }
    }

    @Override
    protected void onStop() {
        CardStackListener listener = manager.getCardStackListener();
        switch (type) {
            case AutomaticSwipe:
                // Notify callback from CardStackLayoutManager
                break;
            case AutomaticRewind:
                listener.onCardRewound();
                listener.onCardAppeared(manager.getTopView(), manager.getTopPosition());
                break;
            case ManualSwipe:
                // Notify callback from CardStackLayoutManager
                break;
            case ManualCancel:
                listener.onCardCanceled();
                break;
        }
    }

    private int getDx(AnimationSetting setting) {
        CardStackState state = manager.getCardStackState();
        int dx = 0;
        switch (setting.getDirection()) {
            case Left:
                dx = -state.width * 2;
                break;
            case Right:
                dx = state.width * 2;
                break;
            case Top:
            case Bottom:
                dx = 0;
                break;
        }
        return dx;
    }

    private int getDy(AnimationSetting setting) {
        CardStackState state = manager.getCardStackState();
        int dy = 0;
        switch (setting.getDirection()) {
            case Left:
            case Right:
                dy = state.height / 4;
                break;
            case Top:
                dy = -state.height * 2;
                break;
            case Bottom:
                dy = state.height * 2;
                break;
        }
        return dy;
    }

}
