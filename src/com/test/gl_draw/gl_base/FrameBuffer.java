
package com.test.gl_draw.gl_base;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import junit.framework.Assert;
import android.graphics.RectF;
import android.opengl.GLES20;

import com.test.gl_draw.glview.GLView;
import com.test.gl_draw.glview.TextureDraw;
import com.test.gl_draw.utils.GLHelper;

public class FrameBuffer {

    private TextureDraw mTextureDraw = new TextureDraw();

    private int mFramebuffer;

    private float[] mPVMatrix = new float[32];

    private RectF mRectF = new RectF();

    private float mAlpha = 1;

    private static FrameBuffer sFrameBuffer = null;

    private int mFrameCallStackCound = 0;

    public static FrameBuffer getInstance() {
        if (sFrameBuffer == null) {
            sFrameBuffer = new FrameBuffer();
        }

        return sFrameBuffer;
    }

    private FrameBuffer() {

    }

    public void SetUpScene(GL10 gl) {
        float w = GLView.sRenderWidth;
        float h = GLView.sRenderHeight;

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(0, w, 0, h, 1, -1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        GLHelper.checkGLError();
    }

    public void DrawToLayer(GL10 gl, RectF rc, float alpha) {
        mFrameCallStackCound++;

        if (rc.isEmpty() || mFrameCallStackCound != 1) {
            return;
        }

        BackMatrix();

        mAlpha = alpha;

        do {
            if (mRectF.width() == rc.width() && mRectF.height() == rc.height() &&
                    mTextureDraw.getTexture() != null &&
                    mTextureDraw.getTexture().isValid() &&
                    GLHelper.isFrameBuffer(mFramebuffer)) {
                break;
            }

            Destory(gl);
            mRectF.set(rc);

            mTextureDraw
                    .SetRenderRect(new RectF(0, 0, GLView.sRenderWidth, GLView.sRenderHeight));

            Texture texture = mTextureDraw.getTexture();
            if (texture == null) {
                texture = new Texture();
            }

            if (!texture.Init((int) rc.width(), (int) rc.height())) {
                return;
            } else {
                mTextureDraw.SetTexture(texture);
            }

            Assert.assertTrue(GLHelper.checkIfContextSupportsFrameBufferObject());

            int[] size = texture.getRealSize();

            mFramebuffer = GLHelper.createFrameBuffer(size[0],
                    size[1], texture.getTexture());
        } while (false);

        SetUpScene(gl);

        mTextureDraw.SetAlpha(mAlpha);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFramebuffer);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        GLHelper.checkGLError();
    }

    public void Restore(GL10 gl) {
        mFrameCallStackCound--;

        if (mRectF.isEmpty() || mFrameCallStackCound != 0)
            return;

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        RestoreMatrix(gl);

        gl.glLoadIdentity();
        mTextureDraw.Draw(gl);

        GLHelper.checkGLError();
    }

    private void BackMatrix() {
        GLES20.glGetFloatv(GL11.GL_PROJECTION_MATRIX, mPVMatrix, 0);
        GLES20.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, mPVMatrix, 16);
    }

    private void RestoreMatrix(GL10 gl) {
        gl.glViewport(0, 0, GLView.sRenderWidth, GLView.sRenderHeight);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadMatrixf(mPVMatrix, 0);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadMatrixf(mPVMatrix, 16);
    }

    public void Destory(GL10 gl) {
        if (!GLHelper.isFrameBuffer(mFramebuffer))
            return;

        GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

        gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                mFramebuffer);
        gl11ep.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D,
                0, 0);

        gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
        GLHelper.deleteFrameBuffers(mFramebuffer);

        mFramebuffer = 0;
        GLHelper.checkGLError();
    }
}
