package com.test.gl_draw;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Texture {

	private RectF mTextRectF = new RectF();
	private int[] mTexture = { 0 };
	private int mTextureOriginW = 0;
	private int mTextureOriginH = 0;

	// 仅用来判断纹理是否被重复加载
	private Bitmap mTureBitmap = null;

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	public boolean Init(GL10 gl, Bitmap b) {
		if (b == null) {
			return false;
		} else if (b.sameAs(mTureBitmap)) {
			return true;
		}

		UnLoad(gl);

		mTureBitmap = b;

		mTextureOriginW = b.getWidth();
		mTextureOriginH = b.getHeight();

		int new_w = (int) cellPowerOf2(mTextureOriginW);
		int new_h = (int) cellPowerOf2(mTextureOriginH);

		float map_w = b.getWidth() / (float) new_w;
		float map_h = b.getHeight() / (float) new_h;
		float map_x = (1 - map_w) / 2;
		float map_y = (1 - map_h) / 2;

		mTextRectF.set(map_x, map_y, map_x + map_w, map_y + map_h);

		Bitmap resizedBitmap = resizeBitmap(b, new_w, new_h);

		mTexture[0] = utils.loadTexture(gl, resizedBitmap);

		resizedBitmap.recycle();

		return mTexture[0] != 0;
	}

	public void Init(GL10 gl, int w, int h) {
		UnLoad(gl);

		mTextureOriginW = w;
		mTextureOriginH = h;

		int new_w = (int) cellPowerOf2(mTextureOriginW);
		int new_h = (int) cellPowerOf2(mTextureOriginH);

		float map_w = w / (float) new_w;
		float map_h = h / (float) new_h;
		float map_x = (1 - map_w) / 2;
		float map_y = (1 - map_h) / 2;

		mTextRectF.set(map_x, map_y, map_x + map_w, map_y + map_h);

		mTexture[0] = utils.createTargetTexture(gl, new_w, new_h);
	}

	public void UnLoad(GL10 gl) {
		mTureBitmap = null;

		utils.checkEGLContextOK();

		if (mTexture[0] != 0) {
			gl.glDeleteTextures(1, mTexture, 0);
			mTexture[0] = 0;
		}
	}

	public RectF getTextRect() {
		return mTextRectF;
	}

	public int getTexture() {
		return mTexture[0];
	}

	public int[] getTextSize() {
		return new int[] { mTextureOriginW, mTextureOriginH };
	}

	//
	private long cellPowerOf2(long n) {
		n--;
		n |= n >> 1;
		n |= n >> 2;
		n |= n >> 4;
		n |= n >> 8;
		n |= n >> 16;
		n++;
		return n;
	}

	private Bitmap resizeBitmap(Bitmap b, int new_w, int new_h) {
		Bitmap resized_b = null;

		if (new_w < b.getWidth() || new_h < b.getHeight()) {
			throw new RuntimeException("resizeBitmap error!");
		}

		try {
			resized_b = Bitmap.createBitmap(new_w, new_h, Config.ARGB_4444);

			Canvas c = new Canvas(resized_b);
			Rect src = new Rect(0, 0, b.getWidth(), b.getHeight());
			RectF dst = new RectF((new_w - b.getWidth()) / 2.0f,
					(new_h - b.getHeight()) / 2.0f,
					(new_w + b.getWidth()) / 2.0f,
					(new_h + b.getHeight()) / 2.0f);
			Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG
					| Paint.ANTI_ALIAS_FLAG);

			c.drawBitmap(b, src, dst, paint);

		} catch (Exception e) {
			return null;
		} catch (Error e) {
			return null;
		}
		return resized_b;
	}
}
