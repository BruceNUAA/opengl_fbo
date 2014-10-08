
package com.test.gl_draw.data;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.test.gl_draw.gl_base.GLClipManager;
import com.test.gl_draw.gl_base.GLShadeManager;
import com.test.gl_draw.glview.GLView;
import com.test.gl_draw.glview.TextureDraw;
import com.test.gl_draw.utils.GLHelper20;

public class GLFrameBuffer extends GLResource {

    private TextureDraw mTextureDraw = new TextureDraw();

    private int mFramebuffer;

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

        GLES20.glBindFramebuffer(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mFramebuffer);

        GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);

        AfterThreadCall();
    }

    public void Restore(GL10 gl) {

        mFrameCallStackCount--;

        if (mFrameCallStackCount < 0) {
            throw new RuntimeException();
        } else if (mFrameCallStackCount > 0) {
            return;
        }

        BeforeThreadCall();

        GLES20.glBindFramebuffer(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);

        RestoreScene(gl);

        AfterThreadCall();
    }
    
    @Override
    public void unload() {
        if (GLHelper20.deleteFrameBuffers(mFramebuffer)) {
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
                && GLHelper20.isFrameBuffer(mFramebuffer)) {
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

        mFramebuffer = GLHelper20.createFrameBuffer(
                texture.getRealSize(), texture.getValidTexture(gl));
        
        didLoad(System.nanoTime() - t);

    }

    private void SetUpScene(GL10 gl) {	
    	GLShadeManager shade_mgr = GLShadeManager.getInstance();
	
    	shade_mgr.PushMatrix(true);

        int[] texture_size = mTextureDraw.getTexture().getRealSize();

        float offset_x = (texture_size[0] - GLView.sRenderWidth) / 2.0f;
        float offset_y = (texture_size[1] - GLView.sRenderHeight) / 2.0f;

        GLClipManager.getInstance().setScreenSize(gl, true, offset_x, offset_y,
                texture_size[0], texture_size[1]);

        GLES20.glViewport(
                //
                (int) offset_x, (int) offset_y, (int) GLView.sRenderWidth,
                (int) GLView.sRenderHeight);

        Matrix.orthoM(shade_mgr.getProjectionMatrix(), 0, 
        		0, GLView.sRenderWidth, 0, GLView.sRenderHeight, 1, -1);
    }

    private void RestoreScene(GL10 gl) {
    	GLShadeManager shade_mgr = GLShadeManager.getInstance();
    	
        GLES20.glViewport(0, 0, GLView.sRenderWidth, GLView.sRenderHeight);

        GLClipManager.getInstance().setScreenSize(gl, false, 0, 0,
                GLView.sRenderWidth, GLView.sRenderHeight);

        shade_mgr.PopMatrix(true);

    	Matrix.setIdentityM(shade_mgr.getModelMatrix(), 0);
    	
        mTextureDraw.Draw(gl);
    }
}
