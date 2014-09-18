
package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.GLClipManager;
import com.test.gl_draw.igl_draw.IScene;
import com.test.gl_draw.igl_draw.ITouchEvent;
import com.test.gl_draw.test.GLViewTest;
import com.test.gl_draw.utils.GLHelper;

public class GLRootScene implements IScene {
    private GLView mRootView = new GLView();

    @Override
    public void onSurfaceCreated(GL10 gl) {
        GLHelper.checkGLError();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        GLView.sRenderWidth = w;
        GLView.sRenderHeight = h;
        GLClipManager.getInstance().setScreenSize(false, 0, 0, w, h);
        mRootView.SetBounds(new RectF(0, 0, w, h));

        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glOrthof(0, w, h, 0, 1, -1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        GLHelper.checkGLError();
        
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        // **********************************

        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        GLHelper.checkGLError();
        
        GLViewTest.test1(mRootView);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glLoadIdentity();
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        mRootView.Draw(gl);
        GLHelper.checkGLError();
    }

    @Override
    public void onDestory() {
        mRootView.Detach();
    }

    @Override
    public ITouchEvent getEventHandle() {
        return mRootView;
    }

    public GLView rootview() {
        return mRootView;
    }
}
