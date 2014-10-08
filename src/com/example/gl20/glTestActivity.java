package com.example.gl20;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import com.example.gl_fbo.R;
import com.test.gl_draw.gl_base.MultisampleConfigChooser;

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

		setEGLConfigChooser(new MultisampleConfigChooser());
		ToolsUtil.checkGLError();
		setRenderer(new TestRenderer(context));
		setZOrderOnTop(true);
		ToolsUtil.checkGLError();
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this.setBackgroundResource(R.drawable.bg);
	}
}
