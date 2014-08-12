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

public class TestSprite2 implements ISprite, GLTimer.OnAnimatListener {

	private Texture mTexture = new Texture();
	private Texture mTexture2 = new Texture();

	private Bitmap mBitmap;

	private Bitmap mBitmap2;

	private FloatBuffer mVBuffererticleBuffer;
	private FloatBuffer mTextureBufferBuffer;
	private float mBitmapW;
	private float mBitmapH;

	private FloatBuffer mVBuffererticleBuffer2;
	private FloatBuffer mTextureBufferBuffer2;
	private float mBitmapW2;
	private float mBitmapH2;

	private float mStep = 0.005f;
	private float mV = 0;

	private GLTimer mTimer;

	public static int sActive = 1;

	//
	private int mFrameCount = 0;
	private int mTestDuration = 2000;
	private boolean mCanBeDraw = false;

	public TestSprite2(Bitmap bitmap, Bitmap bitmap2) {
		mBitmap = bitmap;
		mBitmap2 = bitmap2;

		mBitmapW = bitmap.getWidth();
		mBitmapH = bitmap.getHeight();
		
		mBitmapW2 = bitmap2.getWidth();
		mBitmapH2 = bitmap2.getHeight();

		StartTimer();
	}

	private void init1(GL10 gl) {
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
	
	private void init2(GL10 gl) {
		utils.checkGLError(gl);

		mTexture2.Init(gl, mBitmap2);

		float x = -mBitmapW2 / 2.0f;
		float y = -mBitmapH2 / 2.0f;

		float[] f1 = {
				//
				x, y, 0,//
				x + mBitmapW2, y + 0, 0, //
				x + 0, y + mBitmapH2, 0, //
				x + mBitmapW2, y + mBitmapH2, 0 //
		};
		mVBuffererticleBuffer2 = BufferUtil.newFloatBuffer(f1.length);
		mVBuffererticleBuffer2.put(f1);
		mVBuffererticleBuffer2.position(0);

		RectF t_r = mTexture2.getTextRect();

		float[] f2 = {
				//
				t_r.left, t_r.top,//
				t_r.right, t_r.top, //
				t_r.left, t_r.bottom,//
				t_r.right, t_r.bottom, };
		mTextureBufferBuffer2 = BufferUtil.newFloatBuffer(f2.length);
		mTextureBufferBuffer2.put(f2);
		mTextureBufferBuffer2.position(0);
	}

	@Override
	public void onSurfaceCreated(GL10 gl) {
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
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
		draw1(gl);
		draw2(gl);
	}

	private void draw1(GL10 gl) {
		if (mTexture.getTexture() == 0) {
			init1(gl);
		}
		
		gl.glPushMatrix();
		
		gl.glRotatef(mV * 360f, 0.0f, 0.0f, -1.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVBuffererticleBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBufferBuffer);

		gl.glActiveTexture(GL10.GL_TEXTURE0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.getTexture());
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
		gl.glPopMatrix();
	}

	private void draw2(GL10 gl) {
		if (mTexture2.getTexture() == 0) {
			init2(gl);
		}
		
		gl.glPushMatrix();

		gl.glRotatef(mV * 45f, 0.0f, 0.0f, -1.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVBuffererticleBuffer2);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBufferBuffer2);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D+100, mTexture2.getTexture());
utils.checkGLError(gl);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

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
