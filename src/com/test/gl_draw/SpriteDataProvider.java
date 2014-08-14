package com.test.gl_draw;

import com.test.gl_draw.igl_draw.ISprite;

import android.graphics.Bitmap;

public class SpriteDataProvider implements ISprite.IDataProvider {

	private Bitmap mBitmap;
	private float[] mRectF = new float[4];
	private float[] mOrigin = new float[2];
	private float[] mRotateOrigin = { 0, 0 };
	private float mAlpha = 1;
	private float mRotateDegree;

	public SpriteDataProvider() {
	}

	@Override
	public Bitmap getRenderBitmap() {
		return mBitmap;
	}

	@Override
	public float[] getRenderRect() {
		return mRectF;
	}

	@Override
	public float[] getOrigin() {
		return mOrigin;
	}

	@Override
	public float[] getRotateOrigin() {
		return mRotateOrigin;
	}

	@Override
	public float getRotateDegree() {
		return mRotateDegree;
	}

	@Override
	public float getAlpha() {
		return Math.min(Math.max(0, mAlpha), 1);
	}

	public void setBitmap(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	public void setAlpha(float alpha) {
		mAlpha = alpha;
	}

	public void setRotateDegree(float degree) {
		mRotateDegree = degree;
	}

	public void setRotateOrigin(float x, float y) {
		mRotateOrigin[0] = x;
		mRotateOrigin[1] = y;
	}

	public void setOrigin(float x, float y) {
		mOrigin[0] = x;
		mOrigin[1] = y;
	}

	public void setRect(float x, float y, float w, float h) {
		mRectF[0] = x;
		mRectF[1] = y;
		mRectF[2] = x + w;
		mRectF[3] = y + h;
	}
}
