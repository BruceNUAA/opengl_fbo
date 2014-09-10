package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;

import com.example.gl_fbo.R;
import com.test.gl_draw.KApplication;
import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.igl_draw.IScene;
import com.test.gl_draw.igl_draw.ITouchEvent;
import com.test.gl_draw.utils.DLog;
import com.test.gl_draw.utils.GLHelper;

public class GLRootScene implements IScene {
	private GLView mRootView = new GLView();

	@Override
	public void onSurfaceCreated(GL10 gl) {
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		GLView.sRenderWidth = w;
		GLView.sRenderHeight = h;

		mRootView.SetBackgound(0xff00264c, 0xffa4b9cf);
		mRootView.SetBounds(new RectF(0, 0, w, h));

		gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, w, h, 0, 1, -1);
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

		test();
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
		IGLView.OnTouchLisener touch = new IGLView.OnTouchLisener() {
			@Override
			public boolean OnClick(IGLView v) {
				DLog.e("Test", "Touch ID = " + v.id());
				return true;
			}
		};

		GLView view = new GLDragView();
		view.setOnTouchLisener(touch);
		view.SetBounds(new RectF(30.0f, 200, 600.0f, 800.0f));
		view.SetBackgound(0x3fff0000);
		mRootView.AddView(view);

		Context cx = KApplication.sApplication;

		Bitmap b = null;
		Texture texture = null;
		b = BitmapFactory.decodeResource(cx.getResources(), R.drawable.img);
		GLTextureView view5 = new GLTextureView();
		texture = new Texture();
		texture.Init(b, false);
		b.recycle();
		view5.SetTexture(texture);
		view5.setOnTouchLisener(touch);
		view5.SetBounds(new RectF(30.0f, 00, 300.0f, 450.0f));
		view5.SetBackgound(0x0fffffff);
		view.AddView(view5);

		b = BitmapFactory
				.decodeResource(cx.getResources(), R.drawable.port_img);
		Texture texture2 = new Texture();
		texture2.Init(b, false);
		b.recycle();
		GLTextureView view6 = new GLTextureView();
		view6.SetTexture(texture2);
		view6.setOnTouchLisener(touch);
		view6.SetBounds(new RectF(300.0f, 00, 600.0f, 450.0f));
		view6.SetBackgound(0x0fffffff);
		view.AddView(view6);

		GLView view2 = new GLView();
		view2.setOnTouchLisener(touch);
		view2.SetBounds(new RectF(30.0f, 15, 300.0f, 500.0f));
		view2.SetBackgound(0x2f0000ff);
		view.AddView(view2);

		GLView view3 = new GLView();
		view3.setOnTouchLisener(touch);
		view3.SetBounds(new RectF(00.0f, 0, 300.0f, 200.0f));
		view3.SetBackgound(0x2fffffff);
		view.AddView(view3);

		GLView view4 = new GLView();
		view4.setOnTouchLisener(touch);
		view4.SetBounds(new RectF(30.0f, 150, 300.0f, 500.0f));
		view4.SetBackgound(0xffff0000);
		view3.AddView(view4);
	}
}
