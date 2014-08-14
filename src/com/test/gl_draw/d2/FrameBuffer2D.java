package com.test.gl_draw.d2;

import javax.microedition.khronos.opengles.GL10;

import com.test.gl_draw.FrameBuffer;
import com.test.gl_draw.igl_draw.ISprite;

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

	@Override
	public void SetUpScene(GL10 gl, int w, int h) {
		set2DScene(gl, w, h, true);
	}

	@Override
	public void RestoreScene(GL10 gl, int w, int h) {
		set2DScene(gl, w, h, false);
	}
	
	// framebuffer的坐标方向与屏幕默认的相反，所以要特殊处理
	private void set2DScene(GL10 gl, int w, int h, boolean is_frame_buffer) {
		gl.glViewport(0, 0, w, h);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();

		int sign = is_frame_buffer ? -1 : 1;
		gl.glOrthof(-w / 2.0f, w / 2.0f, sign * h / 2.0f, -sign * h / 2.0f, -1,
				1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
	}
}
