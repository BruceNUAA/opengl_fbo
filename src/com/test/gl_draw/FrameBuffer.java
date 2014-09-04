package com.test.gl_draw;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import junit.framework.Assert;

import com.test.gl_draw.d2.test.EglHelper;
import com.test.gl_draw.igl_draw.IFrameBuffer;
import com.test.gl_draw.igl_draw.ISprite;
import com.test.gl_draw.utils.utils;

public class FrameBuffer implements IFrameBuffer {
	protected Texture mTexture = new Texture();

	private int[] mDesireSize = { 0, 0 };
	private int[] mRealSize = { 0, 0 };

	private float[] mRenderSize = { 0, 0 };

	private int[] mFramebuffer = { 0 };

	private ISprite[] mISprites;

	private int mNextFrame = 1;

	public void setSurfaceWidth(int w, int h, int render_w, int render_h) {

		mDesireSize[0] = w;
		mDesireSize[1] = h;

		mRenderSize[0] = render_w;
		mRenderSize[1] = render_h;

		mRealSize[0] = w;
		mRealSize[1] = h;
	}

	public void setRenderSprite(ISprite... sprites) {
		mISprites = sprites;

		mNextFrame = 1;
		GL10 gl10 = (GL10)EglHelper.getInstance().getGL();
		OnFrame(gl10);
		OnFrame(gl10);
	}

	private void DrawOnFirstFrame(GL10 gl) {
		if (!utils.checkIfContextSupportsNPOT(gl)) {
			mRealSize[0] = (int) utils.cellPowerOf2(mDesireSize[0]);
			mRealSize[1] = (int) utils.cellPowerOf2(mDesireSize[1]);
		}

		Assert.assertTrue(utils.checkIfContextSupportsFrameBufferObject(gl));

		if (!mTexture.Init(gl, mDesireSize[0], mDesireSize[1])) {
			return;
		}

		gl.glPushMatrix();

		mFramebuffer[0] = utils.createFrameBuffer(gl, mRealSize[0],
				mRealSize[1], mTexture.getTexture());

		GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				mFramebuffer[0]);

		SetUpScene(gl, (int) mRealSize[0], (int) mRealSize[1]);

		for (ISprite i : mISprites) {
			i.onDrawFrame(gl);
		}

		gl.glFlush();

		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);

		RestoreScene(gl, (int) mRenderSize[0], (int) mRenderSize[1]);
		gl.glPopMatrix();

		utils.checkGLError(gl);
	}

	private void UnloadOnNextFrame(GL10 gl) {
		GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				mFramebuffer[0]);
		gl11ep.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D,
				0, 0);
		utils.checkGLError(gl);

		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
		utils.deleteFrameBuffers(gl, mFramebuffer);

		mFramebuffer[0] = 0;
		mISprites = null;

		// 暂时置空
		mTexture = null;
	}

	@Override
	public void OnFrame(GL10 gl) {
		if (mNextFrame == 1) {
			DrawOnFirstFrame(gl);
		} else if (mNextFrame == 0) {
			UnloadOnNextFrame(gl);
			Render.UnRegistFrameCallback(this);
		} else {
			Assert.assertTrue(false);
		}

		mNextFrame--;
	}

	@Override
	public void SetUpScene(GL10 gl, int w, int h) {
		Assert.assertTrue(false);
	}

	@Override
	public SpriteManager getSpriteManager() {
		Assert.assertTrue(false);
		return null;
	}

	@Override
	public void onSurfaceCreated(GL10 gl) {
		Assert.assertTrue(false);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int w, int h) {
		Assert.assertTrue(false);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		Assert.assertTrue(false);
	}

	@Override
	public void RestoreScene(GL10 gl, int w, int h) {
		Assert.assertTrue(false);
	}

}
