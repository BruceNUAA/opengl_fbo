package com.test.gl_draw;

import javax.microedition.khronos.opengles.GL10;

public class FrameBuffer2D extends FrameBuffer {

	public Sprite2D mSprite2D;

	public void setRenderSprite2D(Sprite2D sprite, ISprite... sprites) {
		mSprite2D = sprite;
		super.setRenderSprite(sprites);
	}

	@Override
	public void OnFrame(GL10 gl) {
		super.OnFrame(gl);
		
		mSprite2D.ChangeTexture(gl, mTexture);
	}

}
