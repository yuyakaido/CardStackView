![Logo](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-logo.png)

# CardStackView

![Platform](http://img.shields.io/badge/platform-android-blue.svg?style=flat)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![API](https://img.shields.io/badge/API-14%2B-blue.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![Download](https://api.bintray.com/packages/yuyakaido/maven/CardStackView/images/download.svg)](https://bintray.com/yuyakaido/maven/CardStackView/_latestVersion)
[![AndroidArsenal](https://img.shields.io/badge/Android%20Arsenal-CardStackView-blue.svg?style=flat)](https://android-arsenal.com/details/1/6075)
[![CircleCI](https://circleci.com/gh/yuyakaido/CardStackView.svg?style=svg)](https://circleci.com/gh/yuyakaido/CardStackView)

# Overview

![Overview](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-overview.gif)

# Contents

- [Setup](#setup)
- [Features](#features)
    - [Manual Swipe](#manual-swipe)
    - [Automatic Swipe](#automatic-swipe)
    - [Cancel](#cancel)
    - [Rewind](#rewind)
    - [Overlay](#overlay)
    - [Paging](#paging)
    - [Reloading](#reloading)
    - [Stack From](#stack-from)
    - [Visible Count](#visible-count)
    - [Translation Interval](#translation-interval)
    - [Scale Interval](#scale-interval)
    - [Swipe Threshold](#swipe-threshold)
    - [Max Degree](#max-degree)
    - [Swipe Direction](#swipe-direction)
    - [Swipe Restriction](#swipe-restriction)
- [Public Interfaces](#public-interfaces)
- [Callbacks](#callbacks)
- [Migration Guide](#migration-guide)
- [Installation](#installation)
- [License](#license)

# Setup

```java
CardStackLayoutManager manager = new CardStackLayoutManager();
CardStackAdapter adapter = new CardStackAdapter();
CardStackView cardStackView = findViewById(R.id.card_stack_view);
cardStackView.setLayoutManager(manager);
cardStackView.setAdapter(adapter);
```

# Features

## Manual Swipe

![ManualSwipe](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-manual-swipe.gif)

## Automatic Swipe

![AutomaticSwipe](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-automatic-swipe.gif)

```java
CardStackView.swipe();
```

You can set custom swipe animation.

```
SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
    .setDirection(Direction.Right)
    .setDuration(200)
    .setInterpolator(new AccelerateInterpolator())
    .build();
CardStackLayoutManager.setSwipeAnimationSetting(setting);
CardStackView.swipe();
```

## Cancel

Manual swipe is canceled when the card is dragged less than threshold.

![Cancel](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-cancel.gif)

## Rewind

![Rewind](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-rewind.gif)

```java
CardStackView.rewind();
```

You can set custom rewind animation.

```java
RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
    .setDirection(Direction.Bottom)
    .setDuration(200)
    .setInterpolator(new DecelerateInterpolator())
    .build();
CardStackLayoutManager.setRewindAnimationSetting(setting);
CardStackView.rewind();
```

## Overlay

| Value | Sample |
| :----: | :----: |
| Left | ![Overlay-Left](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-overlay-left.png) |
| Right | ![Overlay-Right](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-overlay-right.png) |

Put overlay view in your item layout of RecyclerView.

```xml
<FrameLayout
    android:id="@+id/left_overlay"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <!-- Set your left overlay -->

</FrameLayout>
```

| Value | Layout ID |
| :----: | :----: |
| Left | left_overlay |
| Right | right_overlay |
| Top | top_overlay |
| Bottom | bottom_overlay |

## Paging

You can implement paging by using following two ways.

1. Use [DiffUtil](https://developer.android.com/reference/android/support/v7/util/DiffUtil).
2. Call [RecyclerView.Adapter.notifyItemRangeInserted](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter#notifyItemRangeInserted(int,%20int)) manually.

**Caution**

You should **not** call `RecyclerView.Adapter.notifyDataSetChanged` for paging because this method will reset top position and maybe occur a perfomance issue.

## Reloading

You can implement reloading by calling `RecyclerView.Adapter.notifyDataSetChanged`.

## Stack From

| Default | Value | Sample |
| :----: | :----: | :----: |
| ✅ | None | ![StackFrom-None](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-stack-from-none.png) |
|  | Top | ![StackFrom-Top](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-stack-from-top.png) |
| | Bottom | ![StackFrom-Bottom](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-stack-from-bottom.png) |
| | Left | ![StackFrom-Left](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-stack-from-left.png) |
| | Right | ![StackFrom-Right](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-stack-from-right.png) |

```java
CardStackLayoutManager.setStackFrom(StackFrom.None);
```

## Visible Count

| Default | Value | Sample |
| :----: | :----: | :----: |
| | 2 | ![VisibleCount-2](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-visible-count-2.png) |
| ✅ | 3 | ![VisibleCount-3](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-visible-count-3.png) |
| | 4 | ![VisibleCount-4](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-visible-count-4.png) |

```java
CardStackLayoutManager.setVisibleCount(3);
```

## Translation Interval

| Default | Value | Sample |
| :----: | :----: | :----: |
| | 4dp | ![TranslationInterval-4dp](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-translation-interval-4dp.png) |
| ✅ | 8dp | ![TranslationInterval-8dp](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-translation-interval-8dp.png) |
| | 12dp | ![TranslationInterval-12dp](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-translation-interval-12dp.png) |

```java
CardStackLayoutManager.setTranslationInterval(8f);
```

## Scale Interval

| Default | Value | Sample |
| :----: | :----: | :----: |
| ✅ | 95% | ![ScaleInterval-95%](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-scale-interval-95.png) |
| | 90% | ![ScaleInterval-90%](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-scale-interval-90.png) |

```java
CardStackLayoutManager.setScaleInterval(0.95f);
```

## Max Degree

| Default | Value | Sample |
| :----: | :----: | :----: |
| ✅ | 20° | ![MaxDegree-20](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-max-degree-20.png) |
| | 0° | ![MaxDegree-0](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-max-degree-0.png) |

```java
CardStackLayoutManager.setMaxDegree(20f);
```

## Swipe Direction

| Default | Value | Sample |
| :----: | :----: | :----: |
| ✅ | Horizontal | ![SwipeDirection-Horizontal](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-swipe-direction-horizontal.gif) |
| | Vertical | ![SwipeDirection-Vertical](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-swipe-direction-vertical.gif) |
| | Freedom | ![SwipeDirection-Freedom](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-swipe-direction-freedom.gif) |

```java
CardStackLayoutManager.setDirections(Direction.HORIZONTAL);
```

## Swipe Threshold

| Default | Value | Sample |
| :----: | :----: | :----: |
| ✅ | 30% | ![SwipeThreshold-30%](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-swipe-threshold-30.gif) |
| | 10% | ![SwipeThreshold-10%](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-swipe-threshold-10.gif) |

```java
CardStackLayoutManager.setSwipeThreshold(0.3f);
```

## Swipe Restriction

| CanScrollHorizontal | CanScrollVertical | Sample |
| :----: | :----: | :----: |
| true | true | ![SwipeRestriction-NoRestriction](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-swipe-restriction-no-restriction.gif) |
| true | false | ![SwipeRestriction-CanScrollHorizontalOnly](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-swipe-restriction-can-scroll-horizontal-only.gif) |
| false | true | ![SwipeRestriction-CanScrollVerticalOnly](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-swipe-restriction-can-scroll-vertical-only.gif) |
| false | false | ![SwipeRestriction-CannotSwipe](https://github.com/yuyakaido/CardStackView/blob/master/images/sample-swipe-restriction-cannot-swipe.gif) |

```java
CardStackLayoutManager.setCanScrollHorizontal(true);
CardStackLayoutManager.setCanScrollVertical(true);
```

# Public Interfaces

## Basic usages

| Method | Description |
| :---- | :---- |
| CardStackView.swipe() | You can swipe once by calling this method. |
| CardStackView.rewind() | You can rewind once by calling this method. |
| CardStackLayoutManager.getTopPosition() | You can get position displayed on top. |
| CardStackLayoutManager.setStackFrom(StackFrom stackFrom) | You can set StackFrom. |
| CardStackLayoutManager.setTranslationInterval(float translationInterval) | You can set TranslationInterval. |
| CardStackLayoutManager.setScaleInterval(float scaleInterval) | You can set ScaleInterval. |
| CardStackLayoutManager.setSwipeThreshold(float swipeThreshold) | You can set SwipeThreshold. |
| CardStackLayoutManager.setMaxDegree(float maxDegree) | You can set MaxDegree. |
| CardStackLayoutManager.setDirections(List<Direction> directions) | You can set Direction. |
| CardStackLayoutManager.setCanScrollHorizontal(boolean canScrollHorizontal) | You can set CanScrollHorizontal. |
| CardStackLayoutManager.setCanScrollVertical(boolean canScrollVertical) | You can set CanScrollVertical. |
| CardStackLayoutManager.setSwipeAnimationSetting(SwipeAnimationSetting swipeAnimationSetting) | You can set SwipeAnimationSetting. |
| CardStackLayoutManager.setRewindAnimationSetting(RewindAnimationSetting rewindAnimationSetting) | You can set RewindAnimationSetting. |

## Advanced usages

| Method | Description |
| :---- | :---- |
| CardStackView.smoothScrollToPosition(int position) | You can scroll any position with animation. |
| CardStackView.scrollToPosition(int position) | You can scroll any position without animation. |

# Callbacks

| Method | Description |
| :---- | :---- |
| CardStackListener.onCardDragging(Direction direction, float ratio) | This method is called while the card is dragging. |
| CardStackListener.onCardSwiped(Direction direction) | This method is called when the card is swiped. |
| CardStackListener.onCardRewound() | This method is called when the card is rewinded. |
| CardStackListener.onCardCanceled() | This method is called when the card is dragged less than threshold. |

# Migration Guide

## Migration of Features

| 1.x | 2.x |
| :---- | :---- |
| Move to Origin | [Cancel](#cancel) |
| Reverse | [Rewind](#rewind) |
| ElevationEnabled | [Stack From](#stack-from) |
| TranslationDiff | [Translation Interval](#translation-interval) |
| ScaleDiff | [Scale Interval](#scale-interval) |
| SwipeEnabled | [Swipe Restriction](#swipe-restriction) |

## Migration of Callbacks

| 1.x | 2.x |
| :---- | :---- |
| CardStackView.CardEventListener | CardStackListener |
| onCardDragging(float percentX, float percentY) | onCardDragging(Direction direction, float ratio) |
| onCardSwiped(SwipeDirection direction) | onCardSwiped(Direction direction) |
| onCardReversed() | onCardRewound() |
| onCardMovedToOrigin() | onCardCanceled() |
| onCardClicked(int index) | This method is no longer provided. Please implement in your item of RecyclerView. |

# Installation

LatestVersion is ![LatestVersion](https://api.bintray.com/packages/yuyakaido/maven/CardStackView/images/download.svg)

```groovy
dependencies {
    implementation "com.yuyakaido.android:card-stack-view:${LatestVersion}"
}
```

# License

```
Copyright 2018 yuyakaido

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
