package com.test.gl_draw.glview;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.utils.BufferUtil;

public class GLTextureView extends GLView {
	private Texture mTexture;

	protected FloatBuffer mTXVBuffer = BufferUtil
			.newFloatBuffer(4 * 2);
	protected FloatBuffer mTXCoordBuffer = BufferUtil
			.newFloatBuffer(4 * 2);
	
	public void SetTexture(Texture texture) {
		if (!texture.isValid())
			return;

		if (mTexture == null)
			mTexture = texture;
		else
			mTexture.Init(texture);
		
		refreshTextureData(Bounds());
	}

	@Override
	public void SetBounds(RectF rc) {
		super.SetBounds(rc);
		refreshTextureData(rc);
	}
	
	@Override
	public void OnDraw(GL10 gl) { 
		if (!mTexture.isValid())
			return;

		if (mBackoundColor != null) {
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
		} else {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}

		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.getTexture());
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mTXVBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTXCoordBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		//
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
	}

	public void refreshTextureData(RectF rect) {

		RectF rc = new RectF(rect);
		int[] size = mTexture.getTextSize();

		float w = size[0];
		float h = size[1];
		// 1:1居中对齐
		if (rc.width() < size[0] || rc.height() < size[1]) {

			if (rc.width() / rc.height() < size[0] / (float) size[1]) {
				w = rc.width();
				h = size[1] * w / size[0];
			} else {
				h = rc.height();
				w = size[0] * h / size[1];
			}

		}

		rc.set(rc.left + (rc.width() - w) / 2, rc.top + (rc.height() - h) / 2,
				rc.left + (rc.width() + w) / 2, rc.top + (rc.height() + h) / 2);

		float[] pos = {
				//
				rc.left, rc.top,//
				rc.right, rc.top, //
				rc.left, rc.bottom,//
				rc.right, rc.bottom, };
		mTXVBuffer.put(pos);
		mTXVBuffer.position(0);

		RectF t_r = mTexture.getTextRect();
		float[] f2 = {
				//
				t_r.left, t_r.top,//
				t_r.right, t_r.top, //
				t_r.left, t_r.bottom,//
				t_r.right, t_r.bottom, };

		mTXCoordBuffer.put(f2);
		mTXCoordBuffer.position(0);
	}
}
