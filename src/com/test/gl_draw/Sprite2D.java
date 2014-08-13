package com.test.gl_draw;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.example.gl_fbo.BufferUtil;

public class Sprite2D implements ISprite {

	private IDataProvider mDataProvider;
	private Texture mTexture;

	private FloatBuffer mVBuffererticleBuffer;
	private FloatBuffer mTextureBufferBuffer;
	private FloatBuffer mColorBuffer;

	public Sprite2D() {
		mTexture = new Texture();
		mVBuffererticleBuffer = BufferUtil.newFloatBuffer(4 * 3);
		mColorBuffer = BufferUtil.newFloatBuffer(4 * 4);
		mTextureBufferBuffer = BufferUtil.newFloatBuffer(4 * 2);
	}

	public void ChangeTexture(GL10 gl, Texture texture) {
		if (texture == null)
			return;

		mTexture.UnLoad(gl);
		mTexture.Init(texture);
	}

	@Override
	public void setDataProvider(IDataProvider provider) {
		mDataProvider = provider;
	}

	@Override
	public void onSurfaceCreated(GL10 gl) {

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		if (!RefreshData(gl))
			return;

		gl.glPushMatrix();
		if (mDataProvider.getAlpha() < 1.0f) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
		}

		float[] origin = mDataProvider.getOrigin();
		float[] rotate_origin = mDataProvider.getRotateOrigin();

		// calc pos
		gl.glTranslatef(rotate_origin[0], rotate_origin[1], 0);
		gl.glRotatef(mDataProvider.getRotateDegree(), 0, 0, 1);
		gl.glTranslatef(-rotate_origin[0], -rotate_origin[1], 0);
		gl.glTranslatef(origin[0], origin[1], 0);

		// draw
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.getTexture());
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVBuffererticleBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBufferBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		//
		if (mDataProvider.getAlpha() < 1.0f) {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
		gl.glPopMatrix();
	}

	private boolean RefreshData(GL10 gl) {
		if (mDataProvider == null || mDataProvider.getRenderBitmap() == null)
			return false;

		float[] rect = mDataProvider.getRenderRect();
		float[] origin = mDataProvider.getOrigin();
		float[] rotate_origin = mDataProvider.getRotateOrigin();
		float alpha = mDataProvider.getAlpha();

		if (rect.length < 4 || origin.length < 2 || rotate_origin.length < 2)
			return false;

		if (mTexture.getTexture() == 0
				&& !mTexture.Init(gl, mDataProvider.getRenderBitmap()))
			return false;

		float[] pos = {
				//
				rect[0], rect[1], 0,//
				rect[2], rect[1], 0,//
				rect[0], rect[3], 0,//
				rect[2], rect[3], 0,//
		};
		mVBuffererticleBuffer.put(pos);
		mVBuffererticleBuffer.position(0);

		float[] color = {
				//
				alpha, alpha, alpha, alpha,//
				alpha, alpha, alpha, alpha,//
				alpha, alpha, alpha, alpha,//
				alpha, alpha, alpha, alpha,//
		};
		mColorBuffer.put(color);
		mColorBuffer.position(0);

		RectF t_r = mTexture.getTextRect();

		float[] f2 = {
				//
				t_r.left, t_r.top,//
				t_r.right, t_r.top, //
				t_r.left, t_r.bottom,//
				t_r.right, t_r.bottom, };

		mTextureBufferBuffer.put(f2);
		mTextureBufferBuffer.position(0);

		return true;
	}
}
