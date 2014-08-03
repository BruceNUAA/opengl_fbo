package com.example.gl_fbo;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.os.SystemClock;

public class Texture2D2 {
	private float maxU = 500.0f;
	private float maxV = 500.0f;

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
	ByteBuffer colorBuffer;
	FloatBuffer coordBuffer;

	public Texture2D2(float w, float h) {
		maxU = w;
		maxV = h;
	}

	float mStep = 0.005f;
	float mV = 0;

	public void draw(GL10 gl, float xx, float yy) {

		mV += mStep;
		if (mV > 1 || mV < 0) {
			mStep *= -1;
		    mV += mStep;
		}

		{
			float x = xx;
			float y = yy;

			float[] f1 = new float[] { x, y, x + maxU, y + 0, x + 0, y + maxV,
					x + maxU, y + maxV, };

			verticleBuffer = BufferUtil.newFloatBuffer(f1.length);
			verticleBuffer.put(f1);
			verticleBuffer.position(0);

			byte t = (byte) (mV  * 255);
			byte[] color = { (byte) t, (byte) t, (byte) t, (byte) t, // 1,
																			// 1,
																			// 1,
																			// mF,//
																			// x
																			// +
																			// 1,
																			// y
																			// +
																			// 1,
					(byte) 0, (byte) 0, (byte) t, t, // 1, 1, 1, mF,// x + 1,
														// y,
					(byte) 0, (byte) t, (byte) 0, t, // 1, 1, 1, mF,// x, y +
														// 1,
					(byte) t, (byte) 0, (byte) 0, t, // 1, 1, 1, mF,// x, y,
			};

			colorBuffer = BufferUtil.newByteBuffer(color.length);
			colorBuffer.put(color);

			colorBuffer.position(0);

			float[] f2 = new float[] { 0, 0, 1, 0, 0, 1, 1, 1, };
			coordBuffer = BufferUtil.newFloatBuffer(f2.length);
			coordBuffer.put(f2);
			coordBuffer.position(0);
		}
		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, verticleBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, coordBuffer);
		gl.glColorPointer(4, GL10.GL_UNSIGNED_BYTE, 0, colorBuffer);

		gl.glLoadIdentity();

		gl.glRotatef(mV * 360f, 0.0f, 0.0f, -1.0f);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}

}
