package com.adi.exam.controls;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;

public class CustomAnimation {
    static TranslateAnimation anim;


    public static void changeViewstoPosition(final String from, final View view, float fromXDelta,
                                             float toXDelta, float fromYDelta, float toYDelta, final long duration) {
        anim = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);
        anim.setDuration(duration);
        anim.setFillEnabled(false);

        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }
        });

        view.startAnimation(anim);
    }


    public static class ResizeAnimation extends Animation {
        final int targetHeight;
        View view;
        int startHeight;

        ResizeAnimation(View view, int targetHeight, int startHeight) {
            this.view = view;
            this.targetHeight = targetHeight;
            this.startHeight = startHeight;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newHeight = (int) (startHeight + targetHeight * interpolatedTime);
            //to support decent animation, change new heigt as Nico S. recommended in comments
            view.getLayoutParams().height = newHeight;
            view.requestLayout();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

}
