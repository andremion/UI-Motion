package com.example.andremion.uimotion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SharingActivity extends AppCompatActivity {

    private int mDefaultDurationAnim;

    private ViewGroup mRootView;
    private ImageView mBackgroundView;
    private View[] mItemViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        mDefaultDurationAnim = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        // This is just a place holder
        View shareButton = findViewById(R.id.share_button);
        // Disable it clicks
        shareButton.setClickable(false);

        // Find view references
        mRootView = (ViewGroup) findViewById(R.id.content_root);
        mBackgroundView = (ImageView) findViewById(R.id.background);
        mItemViews = new View[]{findViewById(R.id.facebook), findViewById(R.id.google_plus),
                findViewById(R.id.instagram), findViewById(R.id.twitter)};

        if (savedInstanceState == null) {
            // Setup initial states
            mBackgroundView.setVisibility(View.INVISIBLE);
            for (View itemView : mItemViews) {
                itemView.setAlpha(0);
            }
        }

        getWindow().getSharedElementEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                getWindow().getSharedElementEnterTransition().removeListener(this);
                revealTheBackground();
                showTheItems();
            }
        });
    }

    @Override
    public void onBackPressed() {
        hideTheItems();
        hideTheBackground();
    }

    /**
     * Reveal the background
     */
    private void revealTheBackground() {
        mBackgroundView.setVisibility(View.VISIBLE);
        Animator reveal = createRevealAnimator(true);
        reveal.start();
    }

    /**
     * Hide the background
     */
    private void hideTheBackground() {
        Animator hide = createRevealAnimator(false);
        hide.setStartDelay(mDefaultDurationAnim);
        hide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBackgroundView.setVisibility(View.INVISIBLE);
                supportFinishAfterTransition();
            }
        });
        hide.start();
    }

    /**
     * Show the items
     */
    private void showTheItems() {
        for (int i = 0; i < mItemViews.length; i++) {
            View itemView = mItemViews[i];
            long startDelay = (mDefaultDurationAnim / mItemViews.length) * (i + 1);
            itemView.animate().alpha(1).setStartDelay(startDelay);
        }
    }

    /**
     * Hide the items
     */
    private void hideTheItems() {
        for (int i = 0; i < mItemViews.length; i++) {
            View itemView = mItemViews[i];
            long startDelay = (mDefaultDurationAnim / mItemViews.length) * (mItemViews.length - i);
            itemView.animate().alpha(0).setStartDelay(startDelay);
        }
    }

    private Animator createRevealAnimator(boolean show) {
        final int cx = mBackgroundView.getWidth() / 2;
        final int cy = mBackgroundView.getHeight() / 2;
        // A lit bit more than half the width and half the height because this view is a square
        // and it's not going to perfectly align with a circle.
        final int radius = (int) Math.hypot(cx, cy);
        final Animator animator;
        if (show) {
            animator = ViewAnimationUtils.createCircularReveal(mBackgroundView, cx, cy, 0, radius);
            animator.setInterpolator(new DecelerateInterpolator());
        } else {
            animator = ViewAnimationUtils.createCircularReveal(mBackgroundView, cx, cy, radius, 0);
//            animator.setInterpolator(new DecelerateInterpolator());
            animator.setInterpolator(new AccelerateInterpolator());
        }
        animator.setDuration(mDefaultDurationAnim);
        return animator;
    }

    public void shareClick(View view) {

        TransitionSet transition = new TransitionSet();
        transition.addTransition(new Fade(Fade.OUT).excludeTarget(R.id.content_root, true));
        transition.addTransition(new ChangeBounds());
        transition.addTransition(new ChangeImageTransform().setStartDelay(mDefaultDurationAnim));
        transition.addTransition(new Fade(Fade.OUT).addTarget(R.id.content_root).setStartDelay(mDefaultDurationAnim * 2));
        transition.addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                finish();
            }
        });
        TransitionManager.beginDelayedTransition(mRootView, transition);

        // Item chosen
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        view.setLayoutParams(layoutParams);

        // Rest of items
        for (View itemView : mItemViews) {
            if (itemView != view) {
                itemView.setVisibility(View.INVISIBLE);
            }
        }

        // Background
        double diagonal = Math.sqrt(mRootView.getHeight() * mRootView.getHeight() + mRootView.getWidth() * mRootView.getWidth());
        float radius = (float) (diagonal / 2f);
        int h = mBackgroundView.getDrawable().getIntrinsicHeight();
        float scale = radius / (h / 2f);
        Matrix matrix = new Matrix(mBackgroundView.getImageMatrix());
        matrix.postScale(scale, scale, mBackgroundView.getWidth() / 2f, mBackgroundView.getHeight() / 2f);
        mBackgroundView.setScaleType(ImageView.ScaleType.MATRIX);
        mBackgroundView.setImageMatrix(matrix);

        // Root
        mRootView.setVisibility(View.INVISIBLE);
    }

}