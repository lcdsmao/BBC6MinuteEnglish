package com.paranoid.mao.bbclearningenglish.utilities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Paranoid on 17/10/17.
 */

public class ExpansionAnimatorUtility {

    public static void animateOpen(final View view) {
        view.setVisibility(View.VISIBLE);
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        final int toHeight = view.getMeasuredHeight();
        ValueAnimator animator = createDropAnim(view, 0, toHeight);
        animator.start();
    }

    public static void animateClose(final View view) {
        int orgHeight = view.getMeasuredHeight();
        ValueAnimator animator = createDropAnim(view, orgHeight, 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
            }
        });
        animator.start();
    }

    private static ValueAnimator createDropAnim(final View view, int from, int to) {
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = value;
                view.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }
}
