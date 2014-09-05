
package com.test.gl_draw.d2;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;

import com.test.gl_draw.gl_base.FrameBuffer;

public class FrameBuffer2D extends FrameBuffer {

    @Override
    public void SetUpScene(GL10 gl) {
        float w = getWidth();
        float h = getHeight();

        GLES20.glViewport(0, 0, (int) w, (int) h);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glOrthof(-w / 2.0f, w / 2.0f, -h / 2.0f, h / 2.0f, -1,
                1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }
}
