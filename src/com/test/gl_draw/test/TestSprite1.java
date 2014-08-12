package com.test.gl_draw.test;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

import com.example.gl_fbo.BufferUtil;
import com.test.gl_draw.GLTimer;
import com.test.gl_draw.ISprite;
import com.test.gl_draw.Texture;
import com.test.gl_draw.utils;

public class TestSprite1 implements ISprite, GLTimer.OnAnimatListener {

	private Texture mTexture = new Texture();

	private Bitmap mBitmap;

	private FloatBuffer mVBuffererticleBuffer;
	private FloatBuffer mTextureBufferBuffer;
	private float mBitmapW;
	private float mBitmapH;

	private float mStep = 0.005f;
	private float mV = 0;

	private GLTimer mTimer;

	public static int sActive = 0;

	private int mTextActiveID = 0;
	//
	private int mFrameCount = 0;
	private int mTestDuration = 2000;
	private boolean mCanBeDraw = false;

	public TestSprite1(Bitmap bitmap) {
		mBitmap = bitmap;
		mBitmapW = bitmap.getWidth();
		mBitmapH = bitmap.getHeight();

		mTextActiveID = sActive;
		//sActive++;
		StartTimer();
	}

	private void init(GL10 gl) {
		//gl.glActiveTexture(GL10.GL_TEXTURE0 + mTextActiveID);
		utils.checkGLError(gl);
		mTexture.Init(gl, mBitmap);

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
	}

	@Override
	public void onSurfaceCreated(GL10 gl) {
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
	}

	@Override
	public void onPreDrawFrame(GL10 gl) {
		// if (!mCanBeDraw)
		// return;

		if (mTexture.getTexture() == 0) {
			init(gl);
		}
		utils.checkGLError(gl);
		gl.glPushMatrix();

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);

		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glActiveTexture(GL10.GL_TEXTURE0 + mTextActiveID);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.getTexture());
		
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// if (!mCanBeDraw)
		// return;

		utils.checkGLError(gl);

		mV += mStep;
		if (mV > 1 || mV < 0) {
			mStep *= -1;
			mV += mStep;
		}
		gl.glActiveTexture(GL10.GL_TEXTURE0 + mTextActiveID);
		gl.glRotatef(mV * 360f, 0.0f, 0.0f, -1.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVBuffererticleBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBufferBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}

	@Override
	public void onPostDrawFrame(GL10 gl) {
		// if (!mCanBeDraw)
		// return;

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		gl.glPopMatrix();
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
		Log.e("TestSprite:", mCanBeDraw + ":\t" + mFrameCount + "-- \t" + mV
				* 360);
		StartTimer();
	}

	private void StartTimer() {
		mTimer = GLTimer.ValeOf(0, 1, mTestDuration, this);
		mTimer.start();
	}
}
