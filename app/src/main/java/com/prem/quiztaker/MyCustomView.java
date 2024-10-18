package com.prem.quiztaker;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MyCustomView extends View {

    private Paint paint;
    private float rectSize; // Size of the rectangle
    private static final float MAX_SIZE = 300f; // Initial size of the rectangle
    private static final float MIN_SIZE = 50f;  // Final size of the rectangle

    public MyCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        startAnimation(); // Start the animation
    }

    // Initialize the paint object and rectangle size
    private void init() {
        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(5f);

        rectSize = MAX_SIZE; // Start with the maximum size
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Calculate the position to center the rectangle
        float left = (getWidth() - rectSize) / 2;
        float top = (getHeight() - rectSize) / 2;
        float right = (getWidth() + rectSize) / 2;
        float bottom = (getHeight() + rectSize) / 2;

        // Draw the rectangle
        canvas.drawRect(left, top, right, bottom, paint);
    }

    // Start the animation to decrease the rectangle size
    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(MAX_SIZE, MIN_SIZE);
        animator.setDuration(2000); // 2 seconds duration
        animator.setRepeatCount(ValueAnimator.INFINITE); // Repeat indefinitely
        animator.setRepeatMode(ValueAnimator.REVERSE); // Reverse the animation

        // Update the size during animation
        animator.addUpdateListener(animation -> {
            rectSize = (float) animation.getAnimatedValue(); // Get the animated size
            invalidate(); // Redraw the view
        });

        animator.start(); // Start the animation
    }
}
