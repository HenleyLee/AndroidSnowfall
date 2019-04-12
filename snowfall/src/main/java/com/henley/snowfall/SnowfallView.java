package com.henley.snowfall;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 下雪效果的View
 *
 * @author Henley
 * @date 2017/8/28 15:57
 */
public class SnowfallView extends View {

    private static final int DEFAULT_SNOWFLAKES_NUM = 300;
    private static final int DEFAULT_SNOWFLAKE_ALPHA_MIN = 150;
    private static final int DEFAULT_SNOWFLAKE_ALPHA_MAX = 250;
    private static final int DEFAULT_SNOWFLAKE_ANGLE_MAX = 10;
    private static final int DEFAULT_SNOWFLAKE_SIZE_MIN_IN_DP = 6;
    private static final int DEFAULT_SNOWFLAKE_SIZE_MAX_IN_DP = 20;
    private static final int DEFAULT_SNOWFLAKE_SPEED_MIN = 2;
    private static final int DEFAULT_SNOWFLAKE_SPEED_MAX = 12;
    private static final boolean DEFAULT_SNOWFLAKES_FADING_ENABLED = false;
    private static final boolean DEFAULT_SNOWFLAKES_ALREADY_FALLING = false;

    private Bitmap snowflakeImage;
    private int snowflakesNum;
    private int snowflakeAlphaMin;
    private int snowflakeAlphaMax;
    private int snowflakeAngleMax;
    private int snowflakeSizeMinInPx;
    private int snowflakeSizeMaxInPx;
    private int snowflakeSpeedMin;
    private int snowflakeSpeedMax;
    private boolean snowflakesFadingEnabled;
    private boolean snowflakesAlreadyFalling;

    private List<Snowflake> snowflakes;

    public SnowfallView(Context context) {
        this(context, null);
    }

    public SnowfallView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnowfallView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SnowfallView);
        try {
            snowflakesNum = typedArray.getInt(R.styleable.SnowfallView_snowflakesNum, DEFAULT_SNOWFLAKES_NUM);
            snowflakeImage = DrawableHelper.toBitmap(typedArray.getDrawable(R.styleable.SnowfallView_snowflakeImage));
            snowflakeAlphaMin = typedArray.getInt(R.styleable.SnowfallView_snowflakeAlphaMin, DEFAULT_SNOWFLAKE_ALPHA_MIN);
            snowflakeAlphaMax = typedArray.getInt(R.styleable.SnowfallView_snowflakeAlphaMax, DEFAULT_SNOWFLAKE_ALPHA_MAX);
            snowflakeAngleMax = typedArray.getInt(R.styleable.SnowfallView_snowflakeAngleMax, DEFAULT_SNOWFLAKE_ANGLE_MAX);
            snowflakeSizeMinInPx = typedArray.getDimensionPixelSize(R.styleable.SnowfallView_snowflakeSizeMin, dpToPx(DEFAULT_SNOWFLAKE_SIZE_MIN_IN_DP));
            snowflakeSizeMaxInPx = typedArray.getDimensionPixelSize(R.styleable.SnowfallView_snowflakeSizeMax, dpToPx(DEFAULT_SNOWFLAKE_SIZE_MAX_IN_DP));
            snowflakeSpeedMin = typedArray.getInt(R.styleable.SnowfallView_snowflakeSpeedMin, DEFAULT_SNOWFLAKE_SPEED_MIN);
            snowflakeSpeedMax = typedArray.getInt(R.styleable.SnowfallView_snowflakeSpeedMax, DEFAULT_SNOWFLAKE_SPEED_MAX);
            snowflakesFadingEnabled = typedArray.getBoolean(R.styleable.SnowfallView_snowflakesFadingEnabled, DEFAULT_SNOWFLAKES_FADING_ENABLED);
            snowflakesAlreadyFalling = typedArray.getBoolean(R.styleable.SnowfallView_snowflakesAlreadyFalling, DEFAULT_SNOWFLAKES_ALREADY_FALLING);
        } finally {
            typedArray.recycle();
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        snowflakes = createSnowflakes();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (changedView == this && visibility == GONE) {
            for (Snowflake snowflake : snowflakes) {
                snowflake.reset(null);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode()) {
            return;
        }
        for (Snowflake snowflake : snowflakes) {
            snowflake.draw(canvas);
        }
        updateSnowflakes();
    }

    private void updateSnowflakes() {
        post(new Runnable() {
            @Override
            public void run() {
                for (Snowflake snowflake : snowflakes) {
                    snowflake.update();
                }
                postInvalidateOnAnimation();
            }
        });
    }

    private List<Snowflake> createSnowflakes() {
        Params params = new Params();
        params.parentWidth = getWidth();
        params.parentHeight = getHeight();
        params.image = snowflakeImage;
        params.alphaMin = snowflakeAlphaMin;
        params.alphaMax = snowflakeAlphaMax;
        params.angleMax = snowflakeAngleMax;
        params.sizeMinInPx = snowflakeSizeMinInPx;
        params.sizeMaxInPx = snowflakeSizeMaxInPx;
        params.speedMin = snowflakeSpeedMin;
        params.speedMax = snowflakeSpeedMax;
        params.fadingEnabled = snowflakesFadingEnabled;
        params.alreadyFalling = snowflakesAlreadyFalling;
        List<Snowflake> snowflakes = new ArrayList<>(snowflakesNum);
        for (int i = 0; i < snowflakesNum; i++) {
            snowflakes.add(new Snowflake(params));
        }
        return snowflakes;
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private static final class Snowflake {

        private Params params;
        private int size = 0;
        private int alpha = 255;
        private Bitmap bitmap = null;
        private double speedX = 0.0;
        private double speedY = 0.0;
        private double positionX = 0.0;
        private double positionY = 0.0;
        private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private Randomizer randomizer = new Randomizer();

        Snowflake(Params params) {
            this.params = params;
            init();
        }

        void init() {
            paint.setColor(Color.rgb(255, 255, 255));
            paint.setStyle(Paint.Style.FILL);
            reset(null);
        }

        void reset(Double positionY) {
            size = randomizer.randomInt(params.sizeMinInPx, params.sizeMaxInPx, true);
            if (params.image != null) {
                bitmap = Bitmap.createScaledBitmap(params.image, size, size, false);
            }

            double speed = ((float) (size - params.sizeMinInPx) / (params.sizeMaxInPx - params.sizeMinInPx) *
                    (params.speedMax - params.speedMin) + params.speedMin);
            double angle = Math.toRadians(randomizer.randomDouble(params.angleMax) * randomizer.randomSignum());
            speedX = speed * Math.sin(angle);
            speedY = speed * Math.cos(angle);

            alpha = randomizer.randomInt(params.alphaMin, params.alphaMax);
            paint.setAlpha(alpha);

            positionX = randomizer.randomDouble(params.parentWidth);
            if (positionY != null) {
                this.positionY = positionY;
            } else {
                this.positionY = randomizer.randomDouble(params.parentHeight);
                if (!params.alreadyFalling) {
                    this.positionY = this.positionY - params.parentHeight - size;
                }
            }
        }

        void update() {
            positionX += speedX;
            positionY += speedY;
            if (positionY > params.parentHeight) {
                positionY = (double) -size;
                reset(positionY);
            }
            if (params.fadingEnabled) {
                paint.setAlpha((int) (alpha * ((float) (params.parentHeight - positionY) / params.parentHeight)));
            }
        }

        void draw(Canvas canvas) {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, (float) positionX, (float) positionY, paint);
            } else {
                canvas.drawCircle((float) positionX, (float) positionY, (float) size, paint);
            }
        }

    }

    private static final class Params {
        Bitmap image;
        int parentWidth;
        int parentHeight;
        int alphaMin;
        int alphaMax;
        int angleMax;
        int sizeMinInPx;
        int sizeMaxInPx;
        int speedMin;
        int speedMax;
        boolean fadingEnabled;
        boolean alreadyFalling;
    }

}
