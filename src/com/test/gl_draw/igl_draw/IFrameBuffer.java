package com.test.gl_draw.igl_draw;

import javax.microedition.khronos.opengles.GL10;

import com.test.gl_draw.Render;

public interface IFrameBuffer extends Render.IRenderFrame, IScene {
	void RestoreScene(GL10 gl, int w, int h);
}
