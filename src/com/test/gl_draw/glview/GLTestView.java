package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.TabThumbManager;
import com.test.gl_draw.igl_draw.IGLView;

public class GLTestView extends GLView {
	private NinePatchDraw mNineDraw = new NinePatchDraw();
	
	@Override
	public void onParentLayoutChange(IGLView parent, RectF old_r, RectF new_r) {
		SetBounds(new_r);
	}

	@Override
	public void OnDraw(GL10 gl) {
		gl.glDisable(GL10.GL_SCISSOR_TEST);
		mNineDraw.Draw(gl);
		gl.glEnable(GL10.GL_SCISSOR_TEST);
	}

	@Override
	public void SetBounds(RectF rc) {
		if (!Bounds().equals(rc)) {

			RectF rect = new RectF(rc);
			mNineDraw.setRect(
					TabThumbManager.getInstance().getShadowTexture(),
					TabThumbManager.sShadowStratchPos, TabThumbManager.sShadowBorder);
			mNineDraw.setRect(rect);
		}

		super.SetBounds(rc);
	}
}
