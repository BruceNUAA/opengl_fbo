package com.test.gl_draw;

public interface IGLGestureListener {
	void onDown(float x, float y);

	void onShowPress(float x, float y);

	void onSingleTapUp(float x, float y);

	void onScroll(float start_x, float start_y, float cur_x, float cur_y,
			float distanceX, float distanceY);

	void onLongPress(float x, float y);

	void onFling(float start_x, float start_y, float cur_x, float cur_y,
			float velocityX, float velocityY);

	void onUp(float x, float y);
}
