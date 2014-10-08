
package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.GLClipManager;
import com.test.gl_draw.gl_base.GLConfigure;
import com.test.gl_draw.gl_base.GLObject;
import com.test.gl_draw.gl_base.GLShadeManager;
import com.test.gl_draw.igl_draw.IScene;
import com.test.gl_draw.igl_draw.ITouchEvent;
import com.test.gl_draw.test.GLViewTest;

public class GLRootScene extends GLObject implements IScene {
    private GLView mRootView = new GLView();
    
    @Override
    public void onSurfaceCreated(GL10 gl) {
        GLConfigure.getInstance().Init(gl);
        GLShadeManager.getInstance().SceneCreate(gl);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
    	 
        BeforeThreadCall(); 

        GLView.sRenderWidth = w;
        GLView.sRenderHeight = h;
     
        GLShadeManager.getInstance().SceneChange(gl, w, h);
      
        GLClipManager.getInstance().setScreenSize(gl, false, 0, 0, w, h);

        mRootView.SetBounds(new RectF(0, 0, w, h));
        
        GLViewTest.test1(rootview());
        AfterThreadCall(); 
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        BeforeThreadCall();
        GLShadeManager.getInstance().SceneFrameCome(gl);
   
        mRootView.Draw(gl);
      
        AfterThreadCall();
    }

    @Override
    public void onDestory() {
        mRootView.Detach();
    }

    @Override
    public ITouchEvent getEventHandle() {
        return mRootView;
    }

    @Override
    public void setVisible(boolean visible) {
       mRootView.SetVisible(visible);
    }

    public GLView rootview() {
        return mRootView;
    }
    
    @Override 
    public void detachFromThread() {
		super.detachFromThread();
		mRootView.detachFromThread();
	}
}
