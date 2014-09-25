
package com.test.gl_draw.igl_draw;

import javax.microedition.khronos.opengles.GL10;

public interface IScene {

    void onSurfaceCreated(GL10 gl);

    void onSurfaceChanged(GL10 gl, int w, int h);

    void onDrawFrame(GL10 gl);
    
    void onDestory();
    
    void setVisible(boolean visible);

    ITouchEvent getEventHandle();
}
