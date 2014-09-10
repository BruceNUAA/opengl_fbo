package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

public class GLRotateView extends GLTextureView {

	private float mRotateDegree = 0;

	private float mRotateOriginX = 0;
	private float mRotateOriginY = 0;

	public void setRotateDegree(float d) {
		mRotateDegree = d;
	}

	public void setRotateOrigin(float x, float y) {
		mRotateOriginX = x;
		mRotateOriginY = y;
	}

	// 绘制
	@Override
	public void Draw(GL10 gl) {

		gl.glPushMatrix();
		gl.glEnable(GL10.GL_SCISSOR_TEST);
		if (Parent() == null)
			return;
		
		RectF r = Parent().ClipBound();

		gl.glScissor((int) r.left, sRenderHeight - (int) r.bottom,
				(int) r.width(), (int) r.height());
		gl.glTranslatef(mRotateOriginX, mRotateOriginY, 0);
		gl.glRotatef(mRotateDegree, 0, 0, 1);
		gl.glTranslatef(-mRotateOriginX, -mRotateOriginY, 0);

		OnDrawBackgound(gl);

		OnDraw(gl);

		OnDrawChilds(gl);
		gl.glDisable(GL10.GL_SCISSOR_TEST);
		gl.glPopMatrix();
	}
}
