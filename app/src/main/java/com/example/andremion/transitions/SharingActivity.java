package com.example.andremion.transitions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class SharingActivity extends AppCompatActivity {

    private View mShareButton;
    private ViewGroup mContentView;
    private View mPlaceholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        mShareButton = findViewById(R.id.share_button);
        mShareButton.setClickable(false);
        mContentView = (ViewGroup) findViewById(R.id.content);
        mContentView.setVisibility(View.INVISIBLE);
        mPlaceholder = findViewById(R.id.placeholder);

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                getWindow().getSharedElementEnterTransition().removeListener(this);
                mContentView.setVisibility(View.VISIBLE);
                Animator reveal = createRevealAnimator(true);
                reveal.start();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Animator hide = createRevealAnimator(false);
        hide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mContentView.setVisibility(View.INVISIBLE);
                supportFinishAfterTransition();
            }
        });
        hide.start();
    }

    private Animator createRevealAnimator(boolean show) {
        final int cx = mContentView.getWidth() / 2;
        final int cy = mContentView.getHeight() / 2;
        // A lit bit more than half the width and half the height because this view is a square
        // and it's not going to perfectly align with a circle.
        final int radius = (int) Math.hypot(cx, cy);
        final Animator animator;
        if (show) {
            animator = ViewAnimationUtils.createCircularReveal(mContentView, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateInterpolator());
        } else {
            animator = ViewAnimationUtils.createCircularReveal(mContentView, cx, cy, radius, 0);
            animator.setInterpolator(new DecelerateInterpolator());
        }
        return animator;
    }

    public void shareClick(View view) {

        AnimatorSet main = new AnimatorSet();
        AnimatorSet.Builder builder = main.play(ObjectAnimator.ofFloat(mShareButton, View.ALPHA, 0));

        // Children
        for (int i = 0; i < mContentView.getChildCount(); i++) {
            View childView = mContentView.getChildAt(i);
            if (childView != view) {
                childView.animate().alpha(0);
                builder.with(ObjectAnimator.ofFloat(childView, View.ALPHA, 0));
            }
        }

        // Item chosen
        PropertyValuesHolder x = PropertyValuesHolder.ofFloat(View.X, mContentView.getWidth() / 2f - view.getWidth() / 2f);
        PropertyValuesHolder y = PropertyValuesHolder.ofFloat(View.Y, mContentView.getHeight() / 2f - view.getHeight() / 2f);
        builder.after(ObjectAnimator.ofPropertyValuesHolder(view, x, y));

        // Grand finale
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0);
        Animator finale = ObjectAnimator.ofPropertyValuesHolder(mContentView, scaleX, scaleY);
        finale.setInterpolator(new AccelerateInterpolator());
        builder.before(finale);

        main.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }
        });
        main.start();
    }

}