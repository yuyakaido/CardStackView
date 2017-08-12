![Logo](https://github.com/yuyakaido/CardStackView/blob/v1/logo.png)

# CardStackView

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Download](https://api.bintray.com/packages/yuyakaido/maven/CardStackView/images/download.svg)](https://bintray.com/yuyakaido/maven/CardStackView/_latestVersion)

# Feature

## StackFrom

| Default | Value | Sample |
| :----: | :----: | :----: |
| ✅ | Top | ![StackFrom-Top](https://github.com/yuyakaido/CardStackView/blob/v1/images/stack-from-top.png) |
| | Bottom | ![StackFrom-Bottom](https://github.com/yuyakaido/CardStackView/blob/v1/images/stack-from-bottom.png) |

```xml
<com.yuyakaido.android.cardstackview.CardStackView
    app:stackFrom="top"
    or
    app:stackFrom="bottom"/>
```

```java
CardStackView#setStackFrom(StackFrom.Top);
or
CardStackView#setStackFrom(StackFrom.Bottom);
```

## VisibleCount

| Default | Value | Sample |
| :----: | :----: | :----: |
| ✅ | 3 | ![VisibleCount-3](https://github.com/yuyakaido/CardStackView/blob/v1/images/visible-count-3.png) |
| | 4 | ![VisibleCount-4](https://github.com/yuyakaido/CardStackView/blob/v1/images/visible-count-4.png) |

```xml
<com.yuyakaido.android.cardstackview.CardStackView
    app:visibleCount="3"
    or
    app:visibleCount="3"/>
```

```java
CardStackView#setVisibleCount(3);
or
CardStackView#setVisibleCount(4);
```

## ElevationEnabled

| Default | Value | Sample |
| :----: | :----: | :----: |
| ✅ | true | ![ElevationEnabled](https://github.com/yuyakaido/CardStackView/blob/v1/images/elevation-enabled.png) |
| | false | ![ElevationDisabled](https://github.com/yuyakaido/CardStackView/blob/v1/images/elevation-disabled.png) |

```xml
<com.yuyakaido.android.cardstackview.CardStackView
    app:isElevationEnabled="true"
    or
    app:isElevationEnabled="false"/>
```

```java
CardStackView#setElevationEnabled(true);
or
CardStackView#setElevationEnabled(false);
```

# Installation

Latest version is ![Download](https://api.bintray.com/packages/yuyakaido/maven/CardStackView/images/download.svg)

```groovy
dependencies {
    compile "com.yuyakaido.android:card-stack-view:${LatestVersion}"
}
```

# License

```
Copyright 2016 yuyakaido

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
