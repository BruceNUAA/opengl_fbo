package com.example.gl_fbo;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

public class Texture2D {
	private int mWidth;
	private int mHeight;
	private float maxU = 2.0f;
	private float maxV = 2.0f;

	private Bitmap mBitmap = null;
	private int textureId = 0;

	public void delete(GL10 gl) {
		if (textureId != 0) {
			gl.glDeleteTextures(1, new int[] { textureId }, 0);
			textureId = 0;
		}

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
		if (textureId == 0) {
			int[] textures = new int[1];
			gl.glGenTextures(1, textures, 0);
			textureId = textures[0];

			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

			gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
					GL10.GL_LINEAR);

			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
			mBitmap.recycle();
			mBitmap = null;
		}

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
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

			float[] f2 = new float[] { 0, 0, 1, 0, 0, 1, 1, 1, };
			coordBuffer = BufferUtil.newFloatBuffer(f2.length);
			coordBuffer.put(f2);
			coordBuffer.position(0);
		}
		gl.glEnable(GL10.GL_TEXTURE_2D);
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
