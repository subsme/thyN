package com.thyn.graphics;

import android.widget.ImageView;
import android.content.Context;
import android.util.AttributeSet;
/**
 * Created by shalu on 7/15/16.
 */
public class SquareImageView extends ImageView{

        public SquareImageView(Context context) {
            super(context);
        }

        public SquareImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), (int)(0.5*getMeasuredWidth())); //Snap to width
        }

}
