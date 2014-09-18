
package com.test.gl_draw.gl_base;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.graphics.RectF;
import android.opengl.GLES20;

import com.test.gl_draw.glview.GLView;
import com.test.gl_draw.glview.TextureDraw;
import com.test.gl_draw.utils.GLHelper;
import com.test.gl_draw.utils.NonThreadSafe;

public class FrameBuffer extends NonThreadSafe {

    private TextureDraw mTextureDraw = new TextureDraw();

    private int mFramebuffer;

    private float[] mPVMatrix = new float[32];

    private RectF mRectF = new RectF();

    private static FrameBuffer sFrameBuffer = null;

    private int mFrameCallStackCount = 0;

    public static FrameBuffer getInstance() {
        if (sFrameBuffer == null) {
            sFrameBuffer = new FrameBuffer();
            sFrameBuffer.mRectF.set(0, 0, GLView.sRenderWidth,
                    GLView.sRenderHeight);
        }

        return sFrameBuffer;
    }

    private FrameBuffer() {

    }

    public void DrawToLayer(GL10 gl, float alpha) {

        CheckThread();

        mFrameCallStackCount++;

        if (mFrameCallStackCount != 1) {
            return;
        }

        do {
            if (mTextureDraw.getTexture() != null
                    && mTextureDraw.getTexture().isValid()
                    && GLHelper.isFrameBuffer(mFramebuffer)) {
                break;
            }

            Destory(gl);

            mTextureDraw.SetRenderRect(new RectF(0, 0, GLView.sRenderWidth,
                    GLView.sRenderHeight));

            Texture texture = mTextureDraw.getTexture();
            if (texture == null) {
                texture = new Texture();
            }

            if (!texture.Init((int) mRectF.width(), (int) mRectF.height())) {
                return;
            } else {
                mTextureDraw.SetTexture(texture);
            }

            mFramebuffer = GLHelper.createFrameBuffer(texture.getRealSize(),
                    texture.getTexture());
        } while (false);

        SetUpScene(gl);

        mTextureDraw.SetAlpha(alpha);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        GLHelper.checkGLError();
    }

    public void Restore(GL10 gl) {
        CheckThread();

        mFrameCallStackCount--;

        if (mFrameCallStackCount != 0)
            return;

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        RestoreScene(gl);

        GLHelper.checkGLError();
    }

    public void Destory(GL10 gl) {
        CheckThread();

        GLHelper.deleteFrameBuffers(mFramebuffer);

        mFramebuffer = 0;

        GLHelper.checkGLError();
    }

    private void SetUpScene(GL10 gl) {
        GLES20.glGetFloatv(GL11.GL_PROJECTION_MATRIX, mPVMatrix, 0);
        GLES20.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, mPVMatrix, 16);

        int[] texture_size = mTextureDraw.getTexture().getRealSize();

        float offset_x = (texture_size[0] - GLView.sRenderWidth) / 2.0f;
        float offset_y = (texture_size[1] - GLView.sRenderHeight) / 2.0f;

        GLClipManager.getInstance().setScreenSize(true, offset_x, offset_y, texture_size[0],
                texture_size[1]);

        gl.glViewport(
                //
                (int) offset_x,
                (int) offset_y,
                (int) GLView.sRenderWidth,
                (int) GLView.sRenderHeight);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0, GLView.sRenderWidth, 0, GLView.sRenderHeight, 1, -1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        GLHelper.checkGLError();
    }

    private void RestoreScene(GL10 gl) {

        gl.glViewport(0, 0, GLView.sRenderWidth, GLView.sRenderHeight);

        GLClipManager.getInstance().setScreenSize(false, 0, 0, GLView.sRenderWidth,
                GLView.sRenderHeight);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadMatrixf(mPVMatrix, 0);

        gl.glMatrixMode(GL10.GL_MODELVIEW);

        gl.glLoadIdentity();

        mTextureDraw.Draw(gl);

        gl.glLoadMatrixf(mPVMatrix, 16);
    }
}
