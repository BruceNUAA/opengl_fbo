package com.example.gl_fbo;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.test.gl_draw.utils;

class Renderer implements GLSurfaceView.Renderer {
	private boolean mContextSupportsFrameBufferObject;
	private int mTargetTexture;
	private int mFramebuffer;
	private int mFramebufferWidth = 512;
	private int mFramebufferHeight = 512;
	private int mSurfaceWidth;
	private int mSurfaceHeight;

	Texture2D mTxImage;
	Texture2D mTxImage2;
	Texture2D2 mx2d2;

	Renderer(Context c) {
		Bitmap test_img = BitmapFactory.decodeResource(c.getResources(),
				R.drawable.img);

		mTxImage = new Texture2D(test_img);

		Bitmap test_img2 = BitmapFactory.decodeResource(c.getResources(),
				R.drawable.port_img);

		mTxImage2 = new Texture2D(test_img2);
		mx2d2 = new Texture2D2(mFramebufferWidth, mFramebufferHeight);
	}

	/**
	 * Setting this to true will change the behavior of this sample. It will
	 * suppress the normally onscreen rendering, and it will cause the rendering
	 * that would normally be done to the offscreen FBO be rendered onscreen
	 * instead. This can be helpful in debugging the rendering algorithm.
	 */
	private static final boolean DEBUG_RENDER_OFFSCREEN_ONSCREEN = false;

	private int mX = 1;

	public void onDrawFrame(GL10 gl) {
		utils.checkGLError(gl);
		if (mContextSupportsFrameBufferObject) {
			GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;
			if (DEBUG_RENDER_OFFSCREEN_ONSCREEN) {
				drawOffscreenImage(gl, mSurfaceWidth, mSurfaceHeight);

			} else {
				Log.d("0000000000000", mX + "------------------");
			
				if (mX-- > 0) {
					gl11ep.glBindFramebufferOES(
							GL11ExtensionPack.GL_FRAMEBUFFER_OES, mFramebuffer);
					drawOffscreenImage(gl, mFramebufferWidth,
							mFramebufferHeight);		
				}
				gl11ep.glBindFramebufferOES(
						GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
				drawOnscreen(gl, mSurfaceWidth, mSurfaceHeight);
			}
		} else {
			gl.glClearColor(1, 0, 0, 0);
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		}
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		utils.checkGLError(gl);
		mSurfaceWidth = width;
		mSurfaceHeight = height;

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		utils.checkEGLContextOK();

		mContextSupportsFrameBufferObject = utils
				.checkIfContextSupportsFrameBufferObject(gl);
		if (mContextSupportsFrameBufferObject) {
			mTargetTexture = utils.createTargetTexture(gl, mFramebufferWidth,
					mFramebufferHeight);
			mFramebuffer = utils.createFrameBuffer(gl, mFramebufferWidth,
					mFramebufferHeight, mTargetTexture);
		}
	}

	private void drawOnscreen(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-width / 2.0f, width / 2.0f, -height / 2.0f, height / 2.0f,
				1, -1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glLoadIdentity();
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTargetTexture);
		gl.glActiveTexture(GL10.GL_TEXTURE0);

		mx2d2.draw(gl, -mFramebufferWidth / 2.0f, -mFramebufferHeight / 2.f);

	}

	private void drawOffscreenImage(GL10 gl, int width, int height) {

		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-width / 2.0f, width / 2.0f, height / 2.0f, -height / 2.0f,
				1, -1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glClearColor(0, 0, 0, 0.0f);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		gl.glLoadIdentity();
		mTxImage.draw(gl, -width / 2.0f, -height / 2.0f);
		mTxImage2.draw(gl, -100.f, -100);
		{
			
			mTxImage.draw(gl, -width / 2.0f, -height / 2.0f);
			mTxImage2.draw(gl, -100.f, -100);
		}
	}
}