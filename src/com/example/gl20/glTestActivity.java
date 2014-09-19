package com.example.gl20;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.example.gl_fbo.R;

public class glTestActivity extends Activity {

	private GLSurfaceView glView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create a GLSurfaceView instance and set it
		// as the ContentView for this Activity.
		glView = new MyGLSurfaceView(this);
		setContentView(glView);
	}
}

class MyGLSurfaceView extends GLSurfaceView {

	public MyGLSurfaceView(Context context) {
		super(context);
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
		// Render the view only when there is a change in the drawing data
		// Set the Renderer for drawing on the GLSurfaceView

		if (true) {
			setEGLConfigChooser(8, 8, 8, 8, 0, 0);
		} else {
			setEGLConfigChooser(new EGLConfigChooser() {
				@Override
				public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
					int[] attrList = new int[] { //
					EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT, //
							EGL10.EGL_DEPTH_SIZE, 0, //
							EGL10.EGL_BUFFER_SIZE, 0,//
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
		}
		ToolsUtil.checkGLError();
		setRenderer(new TestRenderer(context));
		setZOrderOnTop(true);
		ToolsUtil.checkGLError();
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this.setBackgroundResource(R.drawable.bg);
	}
}
