
package com.test.gl_draw.d2;

import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;
import android.util.Log;

import com.test.gl_draw.gl_base.SpriteManager;
import com.test.gl_draw.igl_draw.IGLGestureListener;
import com.test.gl_draw.igl_draw.IScene;
import com.test.gl_draw.igl_draw.ISprite;
import com.test.gl_draw.utils.GLHelper;

public class MainScene2D implements IGLGestureListener, IScene {
    private SpriteManager mSpriteManger = new SpriteManager();

    private int mWidth = -1;
    private int mHeight = -1;

    public MainScene2D() {
    }

    @Override
    public float getWidth() {
        Assert.assertTrue(mWidth != -1);

        return mWidth;
    }

    @Override
    public float getHeight() {
        Assert.assertTrue(mHeight != -1);

        return mHeight;
    }

    @Override
    public void onSurfaceCreated(GL10 gl) {
    }

    @Override
    public float[] getOrigin() {
        return new float[] {
                mWidth / 2.0f, mHeight / 2.0f
        };
    }

    @Override
    public void SetUpScene(GL10 gl) {

        float[] origin = getOrigin();

        gl.glViewport(0, 0, mWidth, mHeight);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glOrthof(-origin[0], getWidth() - origin[0], getHeight() - origin[1], -origin[1], -1, 1);
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        // *** 启动该标记，在三星手机上会花屏 ****
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        // **********************************
        gl.glShadeModel(GL10.GL_SMOOTH); 
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        GLHelper.checkGLError();
    }

    @Override
    public SpriteManager getSpriteManager() {
        return mSpriteManger;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        mWidth = w;
        mHeight = h;
        for (ISprite iSprite : mSpriteManger) {
            iSprite.onSurfaceChanged(gl, w, h);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        SetUpScene(gl);

        gl.glLoadIdentity();
        gl.glClearColor(0, 0, 0, 0);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

        DrawBackgound(gl);
        
        onDrawElement(gl);
    }
    
    @Override
    public void onDrawElement(GL10 gl) {
        for (ISprite iSprite : mSpriteManger) {
            iSprite.onDrawFrame(gl);
        }
    }

    @Override
    public void DrawBackgound(GL10 gl) {

    }

    @Override
    public void onDown(float x, float y) {
        Log.d("IGLGestureListener:",
                Thread.currentThread().getStackTrace()[2].toString());
    }

    @Override
    public void onShowPress(float x, float y) {
        Log.d("IGLGestureListener:",
                Thread.currentThread().getStackTrace()[2].toString());
    }

    @Override
    public void onSingleTapUp(float x, float y) {
        Log.d("IGLGestureListener:",
                Thread.currentThread().getStackTrace()[2].toString());
    }

    @Override
    public void onScroll(float start_x, float start_y, float cur_x,
            float cur_y, float distanceX, float distanceY) {
        Log.d("IGLGestureListener:",
                Thread.currentThread().getStackTrace()[2].toString());

    }

    @Override
    public void onLongPress(float x, float y) {
        Log.d("IGLGestureListener:",
                Thread.currentThread().getStackTrace()[2].toString());
    }

    @Override
    public void onFling(float start_x, float start_y, float cur_x, float cur_y,
            float velocityX, float velocityY) {
        Log.d("IGLGestureListener:",
                Thread.currentThread().getStackTrace()[2].toString());
    }

    @Override
    public void onUp(float x, float y) {
        Log.d("IGLGestureListener:",
                Thread.currentThread().getStackTrace()[2].toString());
    }

    @Override
    public void onDestory() {
        mWidth = -1;
        mHeight = -1;
    }
}
