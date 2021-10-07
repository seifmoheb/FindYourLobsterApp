package com.app.findyourlobster.data;

import android.view.View;

public interface CardStackListener {
    CardStackListener DEFAULT = new CardStackListener() {
        @Override
        public void onCardDragging(Direction direction, float ratio) {
        }

        @Override
        public void onCardSwiped(Direction direction) {
        }

        @Override
        public void onCardRewound() {
        }

        @Override
        public void onCardCanceled() {
        }

        @Override
        public void onCardAppeared(View view, int position) {
        }

        @Override
        public void onCardDisappeared(View view, int position) {
        }
    };

    void onCardDragging(Direction direction, float ratio);

    void onCardSwiped(Direction direction);

    void onCardRewound();

    void onCardCanceled();

    void onCardAppeared(View view, int position);

    void onCardDisappeared(View view, int position);
}
