package com.test.gl_draw;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainScene2D {
	private SpriteManager mSpriteManger = new SpriteManager();

	public MainScene2D() {
	}

	public SpriteManager getSpriteManager() {
		return mSpriteManger;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		for (ISprite iSprite : mSpriteManger) {
			iSprite.onSurfaceCreated(gl);
		}
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
		gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-w / 2.0f, w / 2.0f, h / 2.0f, -h / 2.0f, -1, 1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		for (ISprite iSprite : mSpriteManger) {
			iSprite.onSurfaceChanged(gl, w, h);
		}
	}

	public void onDrawFrame(GL10 gl) {
		gl.glLoadIdentity();
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		for (ISprite iSprite : mSpriteManger) {
			iSprite.onDrawFrame(gl);
		}
	}

}
