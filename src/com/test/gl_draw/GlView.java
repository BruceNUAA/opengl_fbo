package com.test.gl_draw;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

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

		setEGLConfigChooser(new EGLConfigChooser() {
			@Override
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				int[] attrList = new int[] { //
				EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT, //
						EGL10.EGL_DEPTH_SIZE, 24, //
						EGL10.EGL_BUFFER_SIZE, 32,//
						EGL10.EGL_SAMPLE_BUFFERS, 1,//
						EGL10.EGL_SAMPLES, 4, //
						EGL10.EGL_NONE //
				};

				EGLConfig[] configOut = new EGLConfig[1];
				int[] configNumOut = new int[1];
				egl.eglChooseConfig(display, attrList, configOut, 1,
						configNumOut);

				return configOut[0];
			}
		});

		setRenderer(mRender);
	//	setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
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

}
