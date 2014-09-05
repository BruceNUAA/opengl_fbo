
package com.test.gl_draw.d2;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.igl_draw.ISprite;
import com.test.gl_draw.utils.BufferUtil;
import com.test.gl_draw.utils.GLHelper;

public class Sprite2D implements ISprite {

    private IDataProvider mDataProvider;

    private FloatBuffer mVBuffererticleBuffer;
    private FloatBuffer mTextureBufferBuffer;
    private FloatBuffer mColorBuffer;

    public Sprite2D() {
        mVBuffererticleBuffer = BufferUtil.newFloatBuffer(4 * 2);
        mColorBuffer = BufferUtil.newFloatBuffer(4 * 4);
        mTextureBufferBuffer = BufferUtil.newFloatBuffer(4 * 2);
    }

    public void ChangeTexture(Texture texture) {
        if (texture == null)
            return;
    }

    @Override
    public void setDataProvider(IDataProvider provider) {
        mDataProvider = provider;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (!RefreshData(gl))
            return;

        gl.glPushMatrix();
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);

        float[] origin = mDataProvider.getOrigin();
        float[] rotate_origin = mDataProvider.getRotateOrigin();
        GLHelper.checkGLError();
        // calc pos
        gl.glTranslatef(rotate_origin[0], rotate_origin[1], 0);
        gl.glRotatef(mDataProvider.getRotateDegree(), 0, 0, 1);
        gl.glTranslatef(-rotate_origin[0], -rotate_origin[1], 0);
        gl.glTranslatef(origin[0], origin[1], 0);

        // draw
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mDataProvider.getRenderTexture().getTexture());
        GLHelper.checkGLError();
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVBuffererticleBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBufferBuffer);

        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
        //
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
        gl.glPopMatrix();
        GLHelper.checkGLError();
    }

    private boolean RefreshData(GL10 gl) {
        if (mDataProvider == null || !mDataProvider.isVisible())
            return false;

        float[] rect = mDataProvider.getRenderRect();
        float[] origin = mDataProvider.getOrigin();
        float[] rotate_origin = mDataProvider.getRotateOrigin();
        float alpha = 0.1f;//mDataProvider.getAlpha()/2;

        if (rect.length < 4 || origin.length < 2 || rotate_origin.length < 2)
            return false;

        if(mDataProvider.getRenderTexture() == null || !mDataProvider.getRenderTexture().isValid())
            return false;

        GLHelper.checkGLError();
        float[] pos = {
                //
                rect[0], rect[1],//
                rect[2], rect[1],//
                rect[0], rect[3],//
                rect[2], rect[3],//
        };
        mVBuffererticleBuffer.put(pos);
        mVBuffererticleBuffer.position(0);

        float[] color = {
                //
                1, 1, 1, alpha,//
                1, 1, 1, alpha,//
                1, 1, 1, alpha,//
                1, 1, 1, alpha,//
        };
        mColorBuffer.put(color);
        mColorBuffer.position(0);

        RectF t_r = mDataProvider.getRenderTexture().getTextRect();

        float[] f2 = {
                //
                t_r.left, t_r.top,//
                t_r.right, t_r.top, //
                t_r.left, t_r.bottom,//
                t_r.right, t_r.bottom,
        };

        mTextureBufferBuffer.put(f2);
        mTextureBufferBuffer.position(0);

        return true;
    }
}
