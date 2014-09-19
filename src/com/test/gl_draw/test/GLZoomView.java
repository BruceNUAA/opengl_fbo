package com.test.gl_draw.test;

import android.graphics.RectF;

import com.test.gl_draw.glview.GLTextureView;

public class GLZoomView extends GLTextureView {
	private RectF mStartRect = new RectF();
	private RectF mEndRect = new RectF();
	
	public void SetZoomRect(RectF start, RectF end) {
		mStartRect.set(start);
		mEndRect.set(end);
	}
}
