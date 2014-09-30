
package com.test.gl_draw.data;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.GLClipManager;
import com.test.gl_draw.gl_base.GLConfigure;
import com.test.gl_draw.gl_base.GLRender;
import com.test.gl_draw.glview.GLView;
import com.test.gl_draw.glview.TextureDraw;
import com.test.gl_draw.utils.GLHelper;

public class GLFrameBuffer extends GLResource {

    private TextureDraw mTextureDraw = new TextureDraw();

    private int mFramebuffer;

    private float[] mPVMatrix = new float[32];

    private RectF mRectF = new RectF();

    private static GLFrameBuffer sFrameBuffer = null;

    private int mFrameCallStackCount = 0;

    public static GLFrameBuffer getInstance() {
        if (sFrameBuffer == null) {
            sFrameBuffer = new GLFrameBuffer();
        }

        return sFrameBuffer;
    }

    private GLFrameBuffer() {

    }
    
    public void DrawToLayer(GL10 gl, float alpha) {
        if (!GLConfigure.getInstance().isSupportFBO())
            return;

        mFrameCallStackCount++;

        if (mFrameCallStackCount != 1) {
            return;
        }

        if (alpha == 0)
            return;
        
        BeforeThreadCall();

        ReloadIfNeed(gl);

        mTextureDraw.getTexture().ReloadIfNeed(gl);

        SetUpScene(gl);

        mTextureDraw.SetAlpha(alpha);

        GL11ExtensionPack gl11 = (GL11ExtensionPack) gl;

        gl11.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mFramebuffer);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        AfterThreadCall();
    }

    public void Restore(GL10 gl) {

        if (!GLConfigure.getInstance().isSupportFBO())
            return;
        
        mFrameCallStackCount--;

        if (mFrameCallStackCount < 0) {
            throw new RuntimeException();
        } else if (mFrameCallStackCount > 0) {
            return;
        }

        BeforeThreadCall();

        GL11ExtensionPack gl11 = (GL11ExtensionPack) gl;

        gl11.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);

        RestoreScene(gl);

        AfterThreadCall();
    }
    
    @Override
    public void unload() {
        if (GLHelper.deleteFrameBuffers(GLRender.GL(), mFramebuffer)) {
            didUnload();
            
            mFramebuffer = 0;
        }
    }

    public void Destory() {
        BeforeThreadCall();

        unload();

        mTextureDraw.DetachFromView();

        AfterThreadCall();
    }

    private void ReloadIfNeed(GL10 gl) {

        if (mRectF.width() == GLView.sRenderWidth &&
                mRectF.height() == GLView.sRenderHeight &&
                mTextureDraw.getTexture() != null
                && GLHelper.isFrameBuffer(gl, mFramebuffer)) {
            return;
        }
        
        long t = System.nanoTime();
        
        mRectF.set(0, 0, GLView.sRenderWidth,
                GLView.sRenderHeight);

        Destory();

        Texture texture = mTextureDraw.getTexture();
        if (texture == null) {
            texture = new Texture();
        }

        if (!texture.Init((int) mRectF.width(), (int) mRectF.height())) {
            return;
        } else {
            mTextureDraw.SetTexture(texture, true);
            texture = mTextureDraw.getTexture();
        }

        mTextureDraw.SetRenderRect(new RectF(0, 0, GLView.sRenderWidth,
                GLView.sRenderHeight));

        mFramebuffer = GLHelper.createFrameBuffer(gl,
                texture.getRealSize(), texture.getValidTexture(gl));
        
        didLoad(System.nanoTime() - t);

    }

    private void SetUpScene(GL10 gl) {
        GL11 gl11 = (GL11) gl;

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
