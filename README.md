# CardStackView

Tinder like swipeable card view for Android

# Demo

## Swipe

![Tinder like swipe](https://github.com/yuyakaido/CardStackView/blob/master/sample-orverview.gif)

## Reverse

![Reverse](https://github.com/yuyakaido/CardStackView/blob/master/sample-reverse.gif)

## Custom animation

![Custom animation](https://github.com/yuyakaido/CardStackView/blob/master/sample-custom-animation.gif)

# Requirement

- Android 4.0+

# Usage

## Swipe

- Add CardStackView in layout file

```xml
<com.yuyakaido.android.cardstackview.CardStackView
    android:id="@+id/activity_main_card_stack"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="30dp"
    android:clipChildren="false"
    android:clipToPadding="false"/>
```

- Set adapter to CardStackView

```java
CardStackView cardStackView = (CardStackView) findViewById(R.id.activity_main_card_stack);
cardStackView.setAdapter(adapter);
```

## Reverse

- Call reverse method

```java
final CardStackView cardStackView = (CardStackView) findViewById(R.id.activity_main_card_stack_view);
cardStackView.setAdapter(adapter);

View reverseButton = findViewById(R.id.activity_main_reverse_button);
reverseButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        cardStackView.reverse();
    }
});
```

## Custom animation

- Call discard mathod with custom animator

```java
final CardStackView cardStackView = (CardStackView) findViewById(R.id.activity_main_card_stack_view);
cardStackView.setAdapter(adapter);

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
```

# Install

```
dependencies {
    compile 'com.yuyakaido.android:card-stack-view:0.6.0'
}
```
