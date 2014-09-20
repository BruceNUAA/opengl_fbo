package com.test.gl_draw.test;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.TabThumbManager;
import com.test.gl_draw.glview.GLView;
import com.test.gl_draw.glview.NinePatchDraw;

public class GLNinePatchDrawTest extends GLView {
	private NinePatchDraw mNineDraw = new NinePatchDraw();

	public GLNinePatchDrawTest() {
		mNineDraw.ShowBorderInside(true);
	}
	
	@Override
	public void onParentLayoutChange(GLView parent, RectF old_r, RectF new_r) {
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
			TabThumbManager tb = TabThumbManager.getInstance();
			mNineDraw.setRect(tb.getShadowTexture(), tb.getShadowStratchPos(),
					tb.getShadowBorder());
	
			mNineDraw.setRect(rect);
		}

		super.SetBounds(rc);
	}
}
