# AndroidSnowfall-master —— 一个下雪效果的View

## 效果演示 ##
![](/screenshots/snowfall.gif)

## 使用方法 ##
#### 默认的实现： ####
```xml
    <com.liyunlong.snowfall.SnowfallView
          android:layout_width="match_parent"
          android:layout_height="match_parent"/>
```

#### 全部自定义效果： ####
```xml
    <com.liyunlong.snowfall.SnowfallView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:snowflakeAlphaMax="255"
        app:snowflakeAlphaMin="150"
        app:snowflakeAngleMax="10"
        app:snowflakeImage="@drawable/snowflake"
        app:snowflakeSizeMax="26dp"
        app:snowflakeSizeMin="4dp"
        app:snowflakeSpeedMax="12"
        app:snowflakeSpeedMin="4"
        app:snowflakesAlreadyFalling="false"
        app:snowflakesFadingEnabled="true"
        app:snowflakesNum="450" />
```
