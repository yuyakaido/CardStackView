package com.yuyakaido.android.cardstackview.sample;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.yuyakaido.android.cardstackview.CardStackView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardAdapter adapter = new CardAdapter(getApplicationContext());
        for (int i = 0; i < 20; i++) {
            adapter.add(String.valueOf(i));
        }
        final CardStackView cardStackView = (CardStackView) findViewById(R.id.activity_main_card_stack_view);
        cardStackView.setAdapter(adapter);

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

}
