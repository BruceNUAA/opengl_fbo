package com.test.gl_draw;

import junit.framework.Assert;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Looper;
import android.util.AttributeSet;

import com.example.gl_fbo.R;

public class GlView extends GLSurfaceView implements Render.IRenderMsg {

	private Render mRender;

	public GlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRender = new Render(context.getApplicationContext(), this);
		setEGLConfigChooser(8, 8, 8, 8, 0, 0);

		setRenderer(mRender);
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		setZOrderOnTop(true);
		setBackgroundResource(R.drawable.bg);
	}

	@Override
	public void onSurfaceCreated() {
		Assert.assertTrue(Looper.getMainLooper().getThread() == Thread
				.currentThread());
		queueEvent(new Runnable() {

			@Override
			public void run() {
				mRender.test();
			}
		});
	}

	@Override
	public void onSurfaceChanged(int w, int h) {
		Assert.assertTrue(Looper.getMainLooper().getThread() == Thread
				.currentThread());
	}

	@Override
	public void requestRender(boolean once) {
		if (once) {
			setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		} else {
			setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		}

		requestRender();
	}

}
