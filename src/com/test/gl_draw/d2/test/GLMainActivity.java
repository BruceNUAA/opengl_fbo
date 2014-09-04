package com.test.gl_draw.d2.test;

import android.app.Activity;
import android.graphics.Color;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.example.gl_fbo.R;
import com.test.gl_draw.GlView;
import com.test.gl_draw.utils.utils;

public class GLMainActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().getDecorView().setBackgroundColor(Color.WHITE);
		setContentView(new GlView(this, null));
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		EglHelper.getInstance().init();
		if(true)
			return;
		
		//utils.checkEGLContextOK();
		 {
			findViewById(R.id.txt).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//EglHelper.getInstance().init();
					utils.checkEGLContextOK();
					Toast.makeText(GLMainActivity.this, GLES20.glGetString(GLES20.GL_VENDOR), 299).show();
				}
			});
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	
}
