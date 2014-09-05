
package com.test.gl_draw.gl_base;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import junit.framework.Assert;

import com.test.gl_draw.igl_draw.ISprite;
import com.test.gl_draw.utils.GLHelper;

public abstract class FrameBuffer {
    protected Texture mTexture = new Texture();

    private int[] mDesireSize = {
            0, 0
    };
    private int[] mRealSize = {
            0, 0
    };

    private int[] mFramebuffer = {
            0
    };

    private ISprite[] mISprites;

    public void setSurfaceWidth(int w, int h) {

        mDesireSize[0] = w;
        mDesireSize[1] = h;

        mRealSize[0] = w;
        mRealSize[1] = h;
    }

    public Texture getTexture() {
        return mTexture;
    }

    public void setRenderSprite(ISprite... sprites) {
        mISprites = sprites;
    }

    public float getWidth() {
        return mRealSize[0];
    }

    public float getHeight() {
        return mRealSize[1];
    }

    public float[] getOrigin() {
        return new float[] {
                mRealSize[0] / 2.0f, mRealSize[1] / 2.0f
        };
    }

    public void MakeFBOTexture() {
        GL10 gl = GLRender.GL();
        if (gl == null)
            return;
        
        GLHelper.checkGLError();
        if (!GLHelper.checkIfContextSupportsNPOT()) {
            mRealSize[0] = (int) GLHelper.cellPowerOf2(mDesireSize[0]);
            mRealSize[1] = (int) GLHelper.cellPowerOf2(mDesireSize[1]);
        }

        Assert.assertTrue(GLHelper.checkIfContextSupportsFrameBufferObject());
        GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

        if (!mTexture.Init(mDesireSize[0], mDesireSize[1], true)) {
            return;
        }

        mFramebuffer[0] = GLHelper.createFrameBuffer(mRealSize[0],
                mRealSize[1], mTexture.getTexture());

        gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                mFramebuffer[0]);
        SetUpScene(gl);

        for (ISprite i : mISprites) {
            i.onDrawFrame(gl);
        }

        gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);

        GLHelper.checkGLError();
    }

    public void Destory() {
        GL10 gl = GLRender.GL();
        if (gl == null)
            return;

        GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;

        gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                mFramebuffer[0]);
        gl11ep.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D,
                0, 0);

        gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
        GLHelper.deleteFrameBuffers(mFramebuffer);

        mFramebuffer[0] = 0;
        mISprites = null;
        GLHelper.checkGLError();

        // 暂时置空
        mTexture = null;
    }

    public abstract void SetUpScene(GL10 gl);
}
