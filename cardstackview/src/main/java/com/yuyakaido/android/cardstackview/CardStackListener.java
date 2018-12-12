package com.yuyakaido.android.cardstackview;

public interface CardStackListener {
    void onCardDragging(Direction direction, float ratio);
    void onCardSwiped(Direction direction);
    void onCardRewound();
    void onCardCanceled();
    void onCardDisplayed(int position);

    CardStackListener DEFAULT = new CardStackListener() {
        @Override
        public void onCardDragging(Direction direction, float ratio) {}
        @Override
        public void onCardSwiped(Direction direction) {}
        @Override
        public void onCardRewound() {}
        @Override
        public void onCardCanceled() {}
        @Override
        public void onCardDisplayed(int position) {}
    };
}
