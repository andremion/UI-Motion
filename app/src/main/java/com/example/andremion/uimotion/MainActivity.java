package com.example.andremion.uimotion;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void pictureClick(View view) {
        // Create an object containing information about our scene transition animation
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,
                view, getString(R.string.picture_transition_name));
        PictureView pictureView = (PictureView) view;
        int picture = pictureView.getImageResource();
        CharSequence title = view.getContentDescription();
        // Pass information to Detail Activity in order to show the chosen image and its details
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_PICTURE, picture);
        intent.putExtra(DetailActivity.EXTRA_TITLE, title);
        startActivity(intent, options.toBundle());
    }

}
