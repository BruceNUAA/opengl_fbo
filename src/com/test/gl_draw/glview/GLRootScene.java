package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.R.integer;
import android.graphics.Color;
import android.graphics.RectF;

import com.test.gl_draw.igl_draw.IScene;
import com.test.gl_draw.igl_draw.ITouchEvent;
import com.test.gl_draw.utils.GLHelper;

public class GLRootScene implements IScene {
	private GLView mRootView = new GLView();

	public int mWidth = 0;
	public int mHeight = 0;

	@Override
	public void onSurfaceCreated(GL10 gl) {
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		mWidth = w;
		mHeight = h;
		mRootView.SetBackgound(Color.WHITE);
		mRootView.SetBounds(new RectF(0, 0, w, h));
		test();
		gl.glViewport(0, 0, mWidth, mHeight);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, w, h, 0, -1, 1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		// *** 启动该标记，在三星手机上会花屏 ****
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		// **********************************
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

		GLHelper.checkGLError();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glLoadIdentity();
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		mRootView.Draw(gl);
	}

	@Override
	public void onDestory() {
	}

	@Override
	public ITouchEvent getEventHandle() {
		return mRootView;
	}

	private void test() {
		GLView view = new GLView();
		view.SetBounds(new RectF(10.0f, 10, 300.0f, 300.0f));
		view.SetBackgound(0x3fff0000);
		mRootView.AddView(view);
		
		GLView view2 = new GLView();
		view2.SetBounds(new RectF(10.0f, 10, 300.0f, 300.0f));
		view2.SetBackgound(0x3f0000ff);
		view.AddView(view2);
	}
}
