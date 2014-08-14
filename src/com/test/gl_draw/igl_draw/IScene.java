package com.test.gl_draw.igl_draw;

import javax.microedition.khronos.opengles.GL10;

import com.test.gl_draw.SpriteManager;

public interface IScene {

	void SetUpScene(GL10 gl, int w, int h);

	SpriteManager getSpriteManager();
	
	void onSurfaceCreated(GL10 gl);

	void onSurfaceChanged(GL10 gl, int w, int h);

	void onDrawFrame(GL10 gl);
}
