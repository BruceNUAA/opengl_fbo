package com.test.gl_draw.d2.test;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.test.gl_draw.d2.Sprite2D;
import com.test.gl_draw.gl_base.GLTimer;
import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.utils.BufferUtil;
import com.test.gl_draw.utils.utils;

public class TestSprite1 extends Sprite2D implements GLTimer.OnAnimatListener {

	private Texture mTexture = new Texture();

	private Bitmap mBitmap;

	private FloatBuffer mVBuffererticleBuffer;
	private FloatBuffer mTextureBufferBuffer;
	private FloatBuffer mColorBuffer;

	private float mBitmapW;
	private float mBitmapH;

	private float mStep = 0.008f;
	private float mV = 0;

	private GLTimer mTimer;

	//
	private int mFrameCount = 0;
	private int mTestDuration = 8000;
	private boolean mCanBeDraw = false;
	private boolean mTestFrame = false;
	private int mTestMaxDegree = 180;

	public TestSprite1(Bitmap bitmap) {
		mBitmap = bitmap;
		mBitmapW = bitmap.getWidth();
		mBitmapH = bitmap.getHeight();

		StartTimer();
	}

	private boolean init(GL10 gl) {
		utils.checkGLError(gl);

		if (!mTexture.Init(mBitmap))
			return false;

		float x = -mBitmapW / 2.0f;
		float y = -mBitmapH / 2.0f;

		float[] f1 = {
				//
				x, y, 0,//
				x + mBitmapW, y + 0, 0, //
				x + 0, y + mBitmapH, 0, //
				x + mBitmapW, y + mBitmapH, 0 //
		};
		mVBuffererticleBuffer = BufferUtil.newFloatBuffer(f1.length);
		mVBuffererticleBuffer.put(f1);
		mVBuffererticleBuffer.position(0);

		float t = 0.8f;

		float[] color = {
				//
				t, t, t, t,//
				t, t, t, t,//
				t, t, t, t,//
				t, 0, 0, t,//
		};

		mColorBuffer = BufferUtil.newFloatBuffer(color.length);
		mColorBuffer.put(color);

		mColorBuffer.position(0);

		RectF t_r = mTexture.getTextRect();

		float[] f2 = {
				//
				t_r.left, t_r.top,//
				t_r.right, t_r.top, //
				t_r.left, t_r.bottom,//
				t_r.right, t_r.bottom, };
		mTextureBufferBuffer = BufferUtil.newFloatBuffer(f2.length);
		mTextureBufferBuffer.put(f2);
		mTextureBufferBuffer.position(0);

		return true;
	}

	public void onSurfaceCreated(GL10 gl) {
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
	}

	public void onDrawFrame(GL10 gl) {

		// test {
		mV += mStep;
		if (mV > 1 || mV < 0) {
			mStep *= -1;
			mV += mStep;
		}

		if (!mCanBeDraw && mTestFrame)
			return;

		// }

		if (mTexture.getTexture() == 0 && !init(gl))
			return;

		if (!mTimer.isRunning())
			return;

		mV = mTimer.getAnimationValue();

		utils.checkGLError(gl);
		gl.glPushMatrix();

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.getTexture());

		gl.glRotatef(mV * mTestMaxDegree, 0.0f, 0.0f, -1.0f);

		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVBuffererticleBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBufferBuffer);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		gl.glPopMatrix();
		utils.checkGLError(gl);
	}

	@Override
	public void OnAnimationStart() {
		mCanBeDraw = !mCanBeDraw;
		mFrameCount = 0;
	}

	@Override
	public void OnAnimationUpdate(float last_v, float new_v) {
		mFrameCount++;
	}

	@Override
	public void OnAnimationEnd() {
		if (mCanBeDraw) {
			Log.e("TestSprite:", mCanBeDraw + ":\t" + mFrameCount + "-- \t"
					+ mV * 360);
		} else {
			Log.w("TestSprite:", mCanBeDraw + ":\t" + mFrameCount + "-- \t"
					+ mV * 360);
		}
		StartTimer();
	}

	private void StartTimer() {
		if (mTimer == null) {
			mTimer = GLTimer.ValeOf(1, 0, mTestDuration, this);
		} else {

			float[] args = mTimer.getAnimationArgs();

			mTimer = GLTimer.ValeOf(args[1], args[0], (long) args[2], this);
		}

		mTimer.start();
	}
}
