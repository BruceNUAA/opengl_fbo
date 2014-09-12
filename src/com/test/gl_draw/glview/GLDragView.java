package com.test.gl_draw.glview;

import android.graphics.RectF;

import com.test.gl_draw.igl_draw.IGLView;

public class GLDragView extends GLView{
	
	@Override
	public boolean onScroll(float start_x, float start_y, float cur_x,
			float cur_y, float distanceX, float distanceY) {

		for(IGLView v : mChildViews) {
			RectF rc = new RectF(v.Bounds());
			rc.offset(-distanceX, -distanceY);
			v.SetBounds(rc);
		}

		return true;

	}
}
