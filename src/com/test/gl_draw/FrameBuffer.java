package com.test.gl_draw;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import junit.framework.Assert;

public class FrameBuffer implements Render.IRenderFrame {
	protected Texture mTexture = new Texture();

	private int[] mDesireSize = { 0, 0 };
	private int[] mRealSize = { 0, 0 };

	private int[] mRenderSize = { 0, 0 };

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
		Render.RegistFrameCallback(this);
	}

	private void DrawOnFirstFrame(GL10 gl) {
		if (!utils.checkIfContextSupportsNPOT(gl)) {
			mRealSize[0] = (int) utils.cellPowerOf2(mDesireSize[0]);
			mRealSize[1] = (int) utils.cellPowerOf2(mDesireSize[0]);
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

		set2DScene(gl, (int) mRealSize[0], (int) mRealSize[1], true);

		for (ISprite i : mISprites) {
			i.onDrawFrame(gl);
		}

		gl.glFlush();

		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);

		set2DScene(gl, (int) mRenderSize[0], (int) mRenderSize[1], false);
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
		}

		mNextFrame--;
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
