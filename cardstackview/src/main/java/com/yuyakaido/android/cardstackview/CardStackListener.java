package com.yuyakaido.android.cardstackview;

public interface CardStackListener {
    void onCardSwiped(Direction direction);
    void onCardRewound();
    void onCardCanceled();

    CardStackListener DEFAULT = new CardStackListener() {
        @Override
        public void onCardSwiped(Direction direction) {}
        @Override
        public void onCardRewound() {}
        @Override
        public void onCardCanceled() {}
    };
}
