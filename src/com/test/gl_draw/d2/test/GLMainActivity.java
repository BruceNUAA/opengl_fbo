package com.test.gl_draw.d2.test;

import com.test.gl_draw.GlView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

public class GLMainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().getDecorView().setBackgroundColor(Color.WHITE);

		setContentView(new GlView(this, null));
	}
}
