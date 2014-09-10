package com.test.gl_draw.d2;

import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;
import android.R.bool;
import android.util.Log;

import com.test.gl_draw.gl_base.SpriteManager;
import com.test.gl_draw.igl_draw.ITouchEvent;
import com.test.gl_draw.igl_draw.IScene;
import com.test.gl_draw.igl_draw.ISprite;
import com.test.gl_draw.utils.GLHelper;

public class MainScene2D implements ITouchEvent, IScene {
	public MainScene2D() {
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceCreated(GL10 gl) {
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
	}

	@Override
	public ITouchEvent getEventHandle() {
		return this;
	}

	@Override
	public boolean onDown(float x, float y) {
		Log.d("IGLGestureListener:",
				Thread.currentThread().getStackTrace()[2].toString());
		return false;
	}

	@Override
	public boolean onShowPress(float x, float y) {
		Log.d("IGLGestureListener:",
				Thread.currentThread().getStackTrace()[2].toString());
		return false;
	}

	@Override
	public boolean onSingleTapUp(float x, float y) {
		Log.d("IGLGestureListener:",
				Thread.currentThread().getStackTrace()[2].toString());
		return false;
	}

	@Override
	public boolean onScroll(float start_x, float start_y, float cur_x,
			float cur_y, float distanceX, float distanceY) {
		Log.d("IGLGestureListener:",
				Thread.currentThread().getStackTrace()[2].toString());
		return false;

	}

	@Override
	public boolean onLongPress(float x, float y) {
		Log.d("IGLGestureListener:",
				Thread.currentThread().getStackTrace()[2].toString());
		return false;
	}

	@Override
	public boolean onFling(float start_x, float start_y, float cur_x, float cur_y,
			float velocityX, float velocityY) {
		Log.d("IGLGestureListener:",
				Thread.currentThread().getStackTrace()[2].toString());
		return false;
	}

	@Override
	public boolean onUp(float x, float y) {
		Log.d("IGLGestureListener:",
				Thread.currentThread().getStackTrace()[2].toString());
		return false;
	}

	@Override
	public void onDestory() {
	}
}
