package com.hyc.one.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/5/23.
 */
public class DrawableClickTextView extends TextView {
    private DrawableLeftListener mLeftListener;
    private DrawableRightListener mRightListener;

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_TOP = 1;
    final int DRAWABLE_RIGHT = 2;
    final int DRAWABLE_BOTTOM = 3;

    public DrawableClickTextView(Context context) {
        super(context);
    }

    public DrawableClickTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableClickTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDrawableLeftListener(DrawableLeftListener listener) {
        this.mLeftListener = listener;
    }

    public void setDrawableRightListener(DrawableRightListener listener) {
        this.mRightListener = listener;
    }

    public interface DrawableLeftListener {
        public void onDrawableLeftClick(View view);
    }

    public interface DrawableRightListener {
        public void onDrawableRightClick(View view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mRightListener != null) {
                    Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT];
                    if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                        mRightListener.onDrawableRightClick(this);
                        return true;
                    }
                }
                if (mLeftListener != null) {
                    Drawable drawableLift = getCompoundDrawables()[DRAWABLE_LEFT];
                    if (drawableLift != null && event.getRawX() >= (getLeft() + drawableLift.getBounds().width()))
                        mLeftListener.onDrawableLeftClick(this);
                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
