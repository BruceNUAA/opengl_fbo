package com.test.gl_draw.d2;

import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.igl_draw.ISprite;

public class SpriteDataProvider2D implements ISprite.IDataProvider {

	private Texture mTexture;
	private float[] mRectF = new float[4];
	private float[] mOrigin = new float[2];
	private float[] mRotateOrigin = { 0, 0 };
	private float mAlpha = 1;
	private float mRotateDegree;
	private boolean mVisible = true;

	public SpriteDataProvider2D() {
	}

	@Override
	public Texture getRenderTexture() {
		return mTexture;
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
	
	@Override
	public boolean isVisible() {
	    return mVisible;
	}

	public void setTexture(Texture texture) {
	    mTexture = texture;
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
	
	public void setVisible(boolean visible) {
	    mVisible = visible;
	}
}
