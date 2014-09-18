package com.test.gl_draw.igl_draw;


public interface ITouchEvent {
	
	boolean onDown(float x, float y);

	boolean onShowPress(float x, float y);

	boolean onSingleTapUp(float x, float y);

	boolean onScroll(float start_x, float start_y, float cur_x, float cur_y,
			float distanceX, float distanceY);

	boolean onLongPress(float x, float y);

	boolean onFling(float start_x, float start_y, float cur_x, float cur_y,
			float velocityX, float velocityY);

	boolean onUp(float x, float y);
}
