package com.adi.exam.animations;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ExpandListAnimation extends Animation {

    private View mAnimatedView;

    private int mEndHeight;

    private int mStartVisibility;

    public ExpandListAnimation(View view, int duration) {

        setDuration(duration);

        mAnimatedView = view;

        mEndHeight = mAnimatedView.getLayoutParams().height;

        mStartVisibility = mAnimatedView.getVisibility();

        if (mStartVisibility == View.GONE || mStartVisibility == View.INVISIBLE) {

            mAnimatedView.setVisibility(View.VISIBLE);

            mAnimatedView.getLayoutParams().height = 0;

        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        super.applyTransformation(interpolatedTime, t);

        if (interpolatedTime < 1.0f) {

            if (mStartVisibility == View.GONE || mStartVisibility == View.INVISIBLE) {

                mAnimatedView.getLayoutParams().height = (int) (mEndHeight * interpolatedTime);

            } else {

                mAnimatedView.getLayoutParams().height = mEndHeight - (int) (mEndHeight * interpolatedTime);

            }

            mAnimatedView.requestLayout();

        } else {

            if (mStartVisibility == View.GONE || mStartVisibility == View.INVISIBLE) {

                mAnimatedView.getLayoutParams().height = mEndHeight;

                mAnimatedView.requestLayout();

            } else {

                mAnimatedView.getLayoutParams().height = 0;

                mAnimatedView.setVisibility(View.GONE);

                mAnimatedView.requestLayout();

                mAnimatedView.getLayoutParams().height = mEndHeight;

            }

        }

    }

    public static void setHeightForWrapContent(Activity activity, View view) {

        DisplayMetrics metrics = new DisplayMetrics();

        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int screenWidth = metrics.widthPixels;

        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(screenWidth, MeasureSpec.EXACTLY);

        view.measure(widthMeasureSpec, heightMeasureSpec);

        view.getLayoutParams().height = view.getMeasuredHeight();

    }

}
