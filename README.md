# CardStackView

Tinder like swipeable card for Android

## Demo

### Tinder like swipe

![Tinder like swipe](https://github.com/yuyakaido/CardStackView/blob/master/sample-orverview.gif)

### Reverse

![Reverse](https://github.com/yuyakaido/CardStackView/blob/master/sample-reverse.gif)

### Custom animation

![Custom animation](https://github.com/yuyakaido/CardStackView/blob/master/sample-custom-animation.gif)

## Requirement

- Android 4.0+

## Usage

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

## Install

```
dependencies {
    compile 'com.yuyakaido.android:card-stack-view:0.6.0'
}
```
