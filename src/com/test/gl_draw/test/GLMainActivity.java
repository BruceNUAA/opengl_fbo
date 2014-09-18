package com.test.gl_draw.test;

import android.app.Activity;
import android.os.Bundle;

import com.test.gl_draw.GLUIView;

public class GLMainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new GLUIView(this, null));
	}
}
