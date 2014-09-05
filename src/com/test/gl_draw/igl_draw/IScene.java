
package com.test.gl_draw.igl_draw;

import com.test.gl_draw.gl_base.SpriteManager;

import javax.microedition.khronos.opengles.GL10;

public interface IScene {

    void SetUpScene(GL10 gl);

    SpriteManager getSpriteManager();

    void onSurfaceCreated(GL10 gl);

    void onSurfaceChanged(GL10 gl, int w, int h);

    void onDrawFrame(GL10 gl);
    
    void onDrawElement(GL10 gl);

    void onDestory();

    void DrawBackgound(GL10 gl);

    float[] getOrigin();

    float getWidth();

    float getHeight();
}
