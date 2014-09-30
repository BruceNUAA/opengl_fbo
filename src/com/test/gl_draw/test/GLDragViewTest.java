package com.test.gl_draw.test;

import com.test.gl_draw.glview.GLView;


public class GLDragViewTest extends GLDragView {
	@Override
	public boolean onScroll(float start_x, float start_y, float cur_x,
			float cur_y, float distanceX, float distanceY) {

		for (GLView v : mChildViews) {
			if (v instanceof GLRotateViewTest) {
				GLRotateViewTest g = (GLRotateViewTest) v;
				g.setRotateOrigin(g.Bounds().centerX(), g.Bounds().centerY());
				float l = (float)Math.sqrt(distanceX * distanceX + distanceY
						* distanceY);
				float d = l / 5 + g.getRotateDegree();

				g.setRotateDegree(d);
			}
		}

		return true;

	}
}
