package com.test.gl_draw.gl_base;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.graphics.RectF;

import com.test.gl_draw.glview.GLView;
import com.test.gl_draw.glview.TextureDraw;
import com.test.gl_draw.utils.GLHelper;

public class FrameBuffer extends GLThreadSafe {

	private TextureDraw mTextureDraw = new TextureDraw();

	private int mFramebuffer;

	private float[] mPVMatrix = new float[32];

	private RectF mRectF = new RectF();

	private static FrameBuffer sFrameBuffer = null;

	private int mFrameCallStackCount = 0;

	public static FrameBuffer getInstance() {
		if (sFrameBuffer == null) {
			sFrameBuffer = new FrameBuffer();
		}

		return sFrameBuffer;
	}

	private FrameBuffer() {

	}

	public void DrawToLayer(GL10 gl, float alpha) {

		if (!GLConfigure.getInstance().isSupportFBO(gl))
			return;

		mFrameCallStackCount++;

		if (mFrameCallStackCount != 1) {
			return;
		}
		
		BeforeThreadCall();

		GL11ExtensionPack gl11 = (GL11ExtensionPack) gl;

		do {
			if (mRectF.width() == GLView.sRenderWidth &&
			        mRectF.height() == GLView.sRenderHeight &&
			        mTextureDraw.getTexture() != null
					&& mTextureDraw.getTexture().isValid()
					&& GLHelper.isFrameBuffer(gl, mFramebuffer)) {
				break;
			}

			 mRectF.set(0, 0, GLView.sRenderWidth,
	                    GLView.sRenderHeight);
			 
			Destory(gl);

			Texture texture = mTextureDraw.getTexture();
			if (texture == null) {
				texture = new Texture();
			}

			if (!texture.Init((int) mRectF.width(), (int) mRectF.height())) {
			    AfterThreadCall();
				return;
			} else {
				mTextureDraw.SetTexture(texture, true);
			}

            mTextureDraw.SetRenderRect(new RectF(0, 0, GLView.sRenderWidth,
                    GLView.sRenderHeight));

			mFramebuffer = GLHelper.createFrameBuffer(gl,
					texture.getRealSize(), texture.getTexture());

		} while (false);

		SetUpScene(gl);

		mTextureDraw.SetAlpha(alpha);

		gl11.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mFramebuffer);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		AfterThreadCall();
	}

	public void Restore(GL10 gl) {

		mFrameCallStackCount--;

		if (mFrameCallStackCount != 0)
			return;
		
		BeforeThreadCall();

		GL11ExtensionPack gl11 = (GL11ExtensionPack) gl;

		gl11.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);

		RestoreScene(gl);

		AfterThreadCall();
	}

	public void Destory(GL10 gl) {
		BeforeThreadCall();

		GLHelper.deleteFrameBuffers(gl, mFramebuffer);

		mTextureDraw.DetachFromView();
		
		mFramebuffer = 0;

		AfterThreadCall();
	}

	private void SetUpScene(GL10 gl) {
	    GL11 gl11 = (GL11)gl;
	    
	    gl11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, mPVMatrix, 0);
	    gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, mPVMatrix, 16);

		int[] texture_size = mTextureDraw.getTexture().getRealSize();

		float offset_x = (texture_size[0] - GLView.sRenderWidth) / 2.0f;
		float offset_y = (texture_size[1] - GLView.sRenderHeight) / 2.0f;

		GLClipManager.getInstance().setScreenSize(gl, true, offset_x, offset_y,
				texture_size[0], texture_size[1]);

		gl.glViewport(
				//
				(int) offset_x, (int) offset_y, (int) GLView.sRenderWidth,
				(int) GLView.sRenderHeight);
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(0, GLView.sRenderWidth, 0, GLView.sRenderHeight, 1, -1);
		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}

	private void RestoreScene(GL10 gl) {

		gl.glViewport(0, 0, GLView.sRenderWidth, GLView.sRenderHeight);

		GLClipManager.getInstance().setScreenSize(gl, false, 0, 0,
				GLView.sRenderWidth, GLView.sRenderHeight);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadMatrixf(mPVMatrix, 0);

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glLoadIdentity();

		mTextureDraw.Draw(gl);

		gl.glLoadMatrixf(mPVMatrix, 16);
	}
}
