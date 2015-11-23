package com.example.andremion.uimotion;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class DetailActivity extends AppCompatActivity {

    static final String EXTRA_PICTURE = DetailActivity.class.getPackage() + ".extra.PICTURE";
    static final String EXTRA_TITLE = DetailActivity.class.getPackage() + ".extra.TITLE";

    private View mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        int picture = getIntent().getExtras().getInt(EXTRA_PICTURE);
        CharSequence title = getIntent().getExtras().getCharSequence(EXTRA_TITLE);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);

        ImageView pictureView = (ImageView) findViewById(R.id.picture);
        pictureView.setImageResource(picture);
        pictureView.setContentDescription(title);

        mShareButton = findViewById(R.id.share_button);
        mShareButton.setScaleX(0);
        mShareButton.setScaleY(0);
        getWindow().getEnterTransition().addListener(new TransitionAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                getWindow().getEnterTransition().removeListener(this);
                mShareButton.animate().scaleX(1).scaleY(1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mShareButton.animate().scaleX(0).scaleY(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                supportFinishAfterTransition();
            }
        });
    }

    public void shareClick(View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                mShareButton, getString(R.string.share_transition_name));
        Intent intent = new Intent(this, SharingActivity.class);
        startActivity(intent, options.toBundle());
    }

    public static class CustomFloatingActionButtonBehavior extends FloatingActionButton.Behavior {

        public CustomFloatingActionButtonBehavior(Context context, AttributeSet attrs) {
            super();
        }

        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, FloatingActionButton child, int layoutDirection) {
            // Prevent the FAB disappears when return from called Activity
            return false;
        }

    }
}