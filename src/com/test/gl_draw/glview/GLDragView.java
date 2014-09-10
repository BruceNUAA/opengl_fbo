package com.test.gl_draw.glview;

import com.test.gl_draw.utils.DLog;

public class GLDragView extends GLView{
	@Override
	public boolean onScroll(float start_x, float start_y, float cur_x,
			float cur_y, float distanceX, float distanceY) {

		DLog.e("scroll", Float.toString(distanceX) + "/" + Float.toString(distanceY));
		for(IGLView v : mChildViews) {
			v.Bounds().offset(-distanceX, -distanceY);
		}

		return true;

	}
}
