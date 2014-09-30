package com.test.gl_draw.test;

import android.graphics.RectF;

import com.test.gl_draw.glview.GLView;

public class GLDragView extends GLView{
	
	@Override
	public boolean onScroll(float start_x, float start_y, float cur_x,
			float cur_y, float distanceX, float distanceY) {

		for(GLView v : mChildViews) {
			RectF rc = new RectF(v.Bounds());
			rc.offset(-distanceX, -distanceY);
			v.SetBounds(rc);
		}

		return true;

	}
}
