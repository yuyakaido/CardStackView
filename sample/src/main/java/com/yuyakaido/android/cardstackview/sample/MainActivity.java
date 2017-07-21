package com.yuyakaido.android.cardstackview.sample;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Quadrant;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeDirection;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private CardStackView cardStackView;
    private CardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        reload();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_main_reload:
                reload();
                break;
            case R.id.menu_activity_main_swipe_left:
                swipeLeft();
                break;
            case R.id.menu_activity_main_swipe_right:
                swipeRight();
                break;
            case R.id.menu_activity_main_reverse:
                reverse();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private CardAdapter createCardAdapter() {
        final CardAdapter adapter = new CardAdapter(getApplicationContext());
        for (int i = 0; i < 20; i++) {
            adapter.add(String.valueOf(i));
        }
        return adapter;
    }

    private void setup() {
        progressBar = (ProgressBar) findViewById(R.id.activity_main_progress_bar);

        cardStackView = (CardStackView) findViewById(R.id.activity_main_card_stack_view);
        cardStackView.setStackFrom(StackFrom.Top);
        cardStackView.setLeftOverlay(R.layout.view_overlay_left);
        cardStackView.setRightOverlay(R.layout.view_overlay_right);
        cardStackView.setCardEventListener(new CardStackView.CardEventListener() {
            @Override
            public void onCardDragging(float percentX, float percentY) {
                Log.d("CardStackView", "onCardDragging");
            }

            @Override
            public void onCardSwiped(Quadrant quadrant) {
                Log.d("CardStackView", "onCardSwiped: " + quadrant.toString());
                if (adapter.getCount() == cardStackView.getTopIndex()) {
                    reload();
                }
            }

            @Override
            public void onCardReversed() {
                Log.d("CardStackView", "onCardReversed");
            }

            @Override
            public void onCardMovedToOrigin() {
                Log.d("CardStackView", "onCardMovedToOrigin");
            }

            @Override
            public void onCardClicked(int index) {
                Log.d("CardStackView", "onCardClicked: " + index);
            }
        });
    }

    private void reload() {
        cardStackView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter = createCardAdapter();
                cardStackView.setAdapter(adapter);
                cardStackView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }, 1000);
    }

    public void swipeLeft() {
        View target = cardStackView.getTopView();

        ValueAnimator rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", 5f));
        rotation.setDuration(200);
        ValueAnimator translate = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0.f, -2000f));
        translate.setStartDelay(100);
        translate.setDuration(500);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(rotation, translate);

        cardStackView.swipe(SwipeDirection.Left, set);
    }

    public void swipeRight() {
        View target = cardStackView.getTopView();

        ValueAnimator rotation = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("rotation", -5f));
        rotation.setDuration(200);
        ValueAnimator translate = ObjectAnimator.ofPropertyValuesHolder(
                target, PropertyValuesHolder.ofFloat("translationX", 0.f, 2000f));
        translate.setStartDelay(100);
        translate.setDuration(500);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(rotation, translate);

        cardStackView.swipe(SwipeDirection.Right, set);
    }

    private void reverse() {
        cardStackView.reverse();
    }

}
