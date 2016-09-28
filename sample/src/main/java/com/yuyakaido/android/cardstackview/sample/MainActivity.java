package com.yuyakaido.android.cardstackview.sample;

import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements CardStackView.CardStackEventListener {
    private CardStackView cardStackView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardAdapter adapter = new CardAdapter(getApplicationContext());
        for (int i = 0; i < 20; i++) {
            adapter.add(String.valueOf(i));
        }
        cardStackView = (CardStackView) findViewById(R.id.activity_main_card_stack_view);
        cardStackView.setAdapter(adapter);
        cardStackView.setCardStackEventListener(this);

        View reverseButton = findViewById(R.id.activity_main_reverse_button);
        reverseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStackView.reverse();
            }
        });

        View customAnimationButton = findViewById(R.id.activity_main_custom_animation_button);
        customAnimationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup target = cardStackView.getTopView();
                
                PropertyValuesHolder holderY = PropertyValuesHolder.ofFloat("translationY", 0.f, 600.f);
                PropertyValuesHolder holderAlpha = PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.8f);
                PropertyValuesHolder holderScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.3f);
                PropertyValuesHolder holderScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.3f);
                ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, holderY, holderScaleY, holderScaleX, holderAlpha);
                animator.setDuration(500);

                cardStackView.discard(animator);
            }
        });
    }

    @Override
    public void onBeginSwipe(int index, Direction direction) {
    }
    @Override
    public void onEndSwipe(Direction direction) {
        cardStackView.getTopView().findViewById(R.id.item_card_stack_right_text).setAlpha(0);
        cardStackView.getTopView().findViewById(R.id.item_card_stack_left_text).setAlpha(0);
    }
    @Override
    public void onSwiping(float positionX) {
        TextView right = (TextView) cardStackView.getTopView().findViewById(R.id.item_card_stack_right_text);
        TextView left = (TextView) cardStackView.getTopView().findViewById(R.id.item_card_stack_left_text);
        if (positionX > 0) {
            right.setAlpha(positionX);
        } else if (positionX < 0) {
            left.setAlpha(-positionX);
        }
    }
    @Override
    public void onDiscarded(int index, Direction direction) {
    }
    @Override
    public void onTapUp(int index) {
    }
}
