package com.example.gl_fbo;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.test.gl_draw.Texture;
import com.test.gl_draw.utils;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class Texture2D {
	private int mWidth;
	private int mHeight;
	private float maxU = 2.0f;
	private float maxV = 2.0f;

	private Bitmap mBitmap = null;
	private Texture mTexture = new Texture();

	public void delete(GL10 gl) {
		mTexture.UnLoad(gl);

		if (mBitmap != null) {
			if (mBitmap.isRecycled())
				mBitmap.recycle();
			mBitmap = null;
		}
	}

	FloatBuffer verticleBuffer;
	FloatBuffer coordBuffer;

	public Texture2D(Bitmap bmp) {
		mWidth = bmp.getWidth();
		mHeight = bmp.getHeight();
		maxU = mWidth;
		maxV = mHeight;
		mBitmap = bmp;
	}

	public void bind(GL10 gl) {
		if (mTexture.getTexture() == 0) {
			mTexture.Init(gl, mBitmap);
		}
		utils.checkGLError(gl);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.getTexture());
	}

	public void draw(GL10 gl, float xx, float yy) {
		{
			float x = xx;
			float y = yy;

			float[] f1 = new float[] { x, y, 0, x + maxU, y + 0, 0, x + 0,
					y + maxV, 0, x + maxU, y + maxV, 0, };
			verticleBuffer = BufferUtil.newFloatBuffer(f1.length);
			verticleBuffer.put(f1);
			verticleBuffer.position(0);

			RectF t_r = mTexture.getTextRect();

			float[] f2 = new float[] {
					//
					t_r.left, t_r.top,//
					t_r.right, t_r.top, //
					t_r.left, t_r.bottom,//
					t_r.right, t_r.bottom, };
			coordBuffer = BufferUtil.newFloatBuffer(f2.length);
			coordBuffer.put(f2);
			coordBuffer.position(0);
		}
		gl.glEnable(GL10.GL_TEXTURE_2D);
		utils.checkGLError(gl);
		gl.glEnable(GL10.GL_BLEND);

		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		this.bind(gl);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticleBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, coordBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
	}
}
