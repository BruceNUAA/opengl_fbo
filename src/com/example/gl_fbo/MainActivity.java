package com.example.gl_fbo;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.EGLConfigChooser;
import android.os.Bundle;

public class MainActivity extends Activity {

	private GLSurfaceView mGLSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().getDecorView().setBackgroundColor(Color.WHITE);
		mGLSurfaceView = new GLSurfaceView(this);
		setContentView(mGLSurfaceView);

		mGLSurfaceView.setEGLConfigChooser(new EGLConfigChooser() {
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

		mGLSurfaceView.setRenderer(new Renderer(this));

		mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

		mGLSurfaceView.setZOrderOnTop(true);
		mGLSurfaceView.setBackgroundResource(R.drawable.bg);
	}

}
