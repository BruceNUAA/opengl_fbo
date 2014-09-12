package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.igl_draw.IGLView;

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

	@Override
	public void SetBounds(RectF rc) {
		super.SetBounds(rc);

		mRotateDegree = (float) (Math
				.asin((rc.centerX() - GLView.sRenderWidth / 2) / mRotateOriginY)
				/ Math.PI * 180);

		rc.set(-rc.width() / 2, -rc.height() / 2, rc.width() / 2,
				rc.height() / 2);
		getDraw().SetRenderRect(rc);
		getBackgoundDraw().SetRenderRect(rc);
	}

	@Override
	public void AddView(IGLView view) {
		throw new RuntimeException();
	}

	@Override
	public void RemoveView(IGLView view) {
		throw new RuntimeException();
	}
	
	// 绘制
	@Override
	public void Draw(GL10 gl) {
		gl.glPushMatrix();
		gl.glEnable(GL10.GL_SCISSOR_TEST);
		if (Parent() == null)
			return;

		RectF r = Parent().ClipBound();

		RectF r2 = Parent().Bounds();
		mRotateOriginX = r2.width() / 2;
		mRotateOriginY = r2.height() * 1.5f;

		gl.glScissor((int) r.left, sRenderHeight - (int) r.bottom,
				(int) r.width(), (int) r.height());
		gl.glTranslatef(mRotateOriginX, mRotateOriginY, 0);
		gl.glRotatef(mRotateDegree, 0, 0, 1);
		gl.glTranslatef(-mRotateOriginX, -mRotateOriginY, 0);

		gl.glTranslatef(r2.width() / 2, r2.height() / 2, 0);
		OnDrawBackgound(gl);

		OnDraw(gl);
		gl.glTranslatef(-r2.width() / 2, -r2.height() / 2, 0);

		gl.glDisable(GL10.GL_SCISSOR_TEST);
		gl.glPopMatrix();

	}
}
