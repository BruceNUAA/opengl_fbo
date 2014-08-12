package com.test.gl_draw;

import javax.microedition.khronos.opengles.GL10;

public interface ISprite {

	void onSurfaceCreated(GL10 gl);
	void onSurfaceChanged(GL10 gl, int w, int h);
	
	void onPreDrawFrame(GL10 gl);
	void onDrawFrame(GL10 gl); 
	void onPostDrawFrame(GL10 gl);
}
