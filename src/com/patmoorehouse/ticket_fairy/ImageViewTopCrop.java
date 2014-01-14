package com.patmoorehouse.ticket_fairy;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author Chris Arriola taken from https://gist.github.com/arriolac/3843346
 */
public class ImageViewTopCrop extends ImageView {
	 
    public ImageViewTopCrop(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ScaleType.MATRIX);
    }
 
    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        Matrix matrix = getImageMatrix();
            
        float scale;
        int viewWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int viewHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int drawableWidth = getDrawable().getIntrinsicWidth();
        int drawableHeight = getDrawable().getIntrinsicHeight();
        
        if (drawableWidth * viewHeight > drawableHeight * viewWidth) {
            scale = (float) viewHeight / (float) drawableHeight;
        } else {
            scale = (float) viewWidth / (float) drawableWidth;
        }
            
        matrix.setScale(scale, scale);
        setImageMatrix(matrix);
            
        return super.setFrame(l, t-60, r, b);
    }        
}