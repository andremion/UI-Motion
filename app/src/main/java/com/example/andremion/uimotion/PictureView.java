package com.example.andremion.uimotion;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PictureView extends ImageView {

    private int mResId;

    public PictureView(Context context) {
        this(context, null, 0, 0);
    }

    public PictureView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public PictureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PictureView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        if (attrs != null) {
            final String namespace = "http://schemas.android.com/apk/res/android";
            final String attribute = "src";
            mResId = attrs.getAttributeResourceValue(namespace, attribute, 0);
        }
    }

    public int getImageResource() {
        return mResId;
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mResId = resId;
    }
}
