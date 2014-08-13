package com.test.gl_draw;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import junit.framework.Assert;

public class FrameBuffer implements Render.IRenderFrame {
	protected Texture mTexture = new Texture();

	private int[] mDesireSize = { 0, 0 };
	private int[] mRealSize = { 0, 0 };

	private int[] mFramebuffer = { 0 };

	private ISprite[] mISprites;

	public void setSurfaceWidth(int w, int h) {

		mDesireSize[0] = w;
		mDesireSize[1] = h;

		mRealSize[0] = (int) utils.cellPowerOf2(w);
		mRealSize[1] = (int) utils.cellPowerOf2(h);
	}

	public void setRenderSprite(ISprite... sprites) {
		mISprites = sprites;

		Render.RegistFrameCallback(this);
	}

	public void Unload(GL10 gl, boolean remove_texture) {
		if (mFramebuffer[0] != 0) {
			utils.deleteFrameBuffers(gl, mFramebuffer);
		}

		// 暂时置空
		mTexture = null;
	}

	@Override
	public void OnFrame(GL10 gl) {
		Assert.assertTrue(utils.checkIfContextSupportsFrameBufferObject(gl));

		if (!mTexture.Init(gl, mRealSize[0], mRealSize[1])) {
			return;
		}

		gl.glPushMatrix();
		
		mFramebuffer[0] = utils.createFrameBuffer(gl, mRealSize[0],
				mRealSize[1], mTexture.getTexture());

		GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				mFramebuffer[0]);

		for (ISprite i : mISprites) {
			i.onDrawFrame(gl);
		}

		gl.glFlush();
		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
		
		gl.glPopMatrix();

		Render.UnRegistFrameCallback(this);
		mISprites = null;
	}
}
