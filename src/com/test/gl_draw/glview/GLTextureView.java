package com.test.gl_draw.glview;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.utils.BufferUtil;
import com.test.gl_draw.utils.GLHelper;

public class GLTextureView extends GLView {
	private Texture mTexture;

	public void SetTexture(Texture texture) {
		if (!texture.isValid())
			return;

		if (mTexture == null)
			mTexture = texture;
		else
			mTexture.Init(texture);
	}

	@Override
	public void OnDraw(GL10 gl) {
		if (!mTexture.isValid())
			return;

		initPos();

		gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.getTexture());
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVBuffererticleBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureCoordBuffer);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		//
		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
	}

	void initPos() {

		RectF rc = new RectF(Bounds());
		int[] size = mTexture.getTextSize();
		// 1:1居中对齐
		if (rc.width() < size[0] || rc.height() < size[1]) {
			float w = 0;
			float h = 0;
			if (rc.width() / rc.height() < size[0] / (float) size[1]) {
				w = rc.width();
				h = size[1] * w / size[0];
			} else {
				h = rc.height();
				w = size[0] * h / size[1];
			}

			rc.set(rc.left + (rc.width() - w) / 2, rc.top + (rc.height() - h)
					/ 2, rc.left + (rc.width() + w) / 2, rc.top
					+ (rc.height() + h) / 2);
		}

		float[] pos = {
				//
				rc.left, rc.top,//
				rc.right, rc.top, //
				rc.left, rc.bottom,//
				rc.right, rc.bottom, };
		mVBuffererticleBuffer.put(pos);
		mVBuffererticleBuffer.position(0);

		RectF t_r = mTexture.getTextRect();
		float[] f2 = {
				//
				t_r.left, t_r.top,//
				t_r.right, t_r.top, //
				t_r.left, t_r.bottom,//
				t_r.right, t_r.bottom, };

		mTextureCoordBuffer.put(f2);
		mTextureCoordBuffer.position(0);
	}
}
