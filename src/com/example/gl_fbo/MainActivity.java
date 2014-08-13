package com.example.gl_fbo;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends Activity {

	private GLSurfaceView mGLSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().getDecorView().setBackgroundColor(Color.WHITE);
		mGLSurfaceView = new GLSurfaceView(this);
		setContentView(mGLSurfaceView);

		mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 0, 0);

		mGLSurfaceView.setRenderer(new Renderer(this));

		mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

		mGLSurfaceView.setZOrderOnTop(true);
		mGLSurfaceView.setBackgroundResource(R.drawable.bg);
	}

}
