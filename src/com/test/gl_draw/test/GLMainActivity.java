package com.test.gl_draw.test;

import android.app.Activity;
import android.os.Bundle;

import com.test.gl_draw.GLUIView;
import com.test.gl_draw.data.GLBitmapLoader;

public class GLMainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLBitmapLoader.getInstance().startup(this);
		setContentView(new GLUIView(this, null));
	}
}
