
package com.test.gl_draw;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.test.gl_draw.gl_base.GLConfigure;
import com.test.gl_draw.gl_base.GLLogWrapper;
import com.test.gl_draw.gl_base.GLRender;
import com.test.gl_draw.gl_base.MultisampleConfigChooser;
import com.test.gl_draw.glview.GLRootScene;
import com.test.gl_draw.glview.GLView;
import com.test.gl_draw.igl_draw.ITouchEvent;

public class GLUIView extends GLSurfaceView implements GLRender.IRenderMsg,
        View.OnTouchListener, GestureDetector.OnGestureListener {

    // static
    private static GLUIView sMultiWindowView = null;

    public static boolean PostRenderEvent(Runnable r) {
        if (sMultiWindowView == null)
            return false;

        sMultiWindowView.doGLTask(r);
        return true;
    }

    //

    private GLRender mRender;
    private GestureDetector mGestureDector;
    private GLRootScene mRootScene = new GLRootScene();
    private ITouchEvent mIGLGestureListener;

    private List<Runnable> mRendeInitEvent = new ArrayList<Runnable>();

    public GLUIView(Context context, AttributeSet attrs) {
        super(context, attrs);

        sMultiWindowView = this;
        
        configureSurface(context);

        setOnTouchListener(this);

        postGLViewInitTask();

    }

    public GLView getGLRootView() {
        return mRootScene.rootview();
    }

    public void PostRenderRunnable(Runnable runnable) {
        queueEvent(runnable);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mIGLGestureListener == null)
            return false;

        boolean hr = mGestureDector.onTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_UP) {
            final float x = event.getX();
            final float y = event.getY();
            queueEvent(new Runnable() {

                @Override
                public void run() {
                    mIGLGestureListener.onUp(x, y);
                }
            });
        }

        return hr;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();

        queueEvent(new Runnable() {

            @Override
            public void run() {
                mIGLGestureListener.onDown(x, y);
            }
        });

        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();

        queueEvent(new Runnable() {

            @Override
            public void run() {
                mIGLGestureListener.onShowPress(x, y);
            }
        });

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();

        queueEvent(new Runnable() {

            @Override
            public void run() {
                mIGLGestureListener.onSingleTapUp(x, y);
            }
        });

        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2,
            final float distanceX, final float distanceY) {
        final float x1 = e1.getX();
        final float y1 = e1.getY();

        final float x2 = e2.getX();
        final float y2 = e2.getY();

        queueEvent(new Runnable() {

            @Override
            public void run() {
                mIGLGestureListener.onScroll(x1, y1, x2, y2, distanceX,
                        distanceY);
            }
        });

        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        final float x = e.getX();
        final float y = e.getY();

        queueEvent(new Runnable() {

            @Override
            public void run() {
                mIGLGestureListener.onLongPress(x, y);
            }
        });
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
            final float velocityX, final float velocityY) {
        final float x1 = e1.getX();
        final float y1 = e1.getY();

        final float x2 = e2.getX();
        final float y2 = e2.getY();

        queueEvent(new Runnable() {

            @Override
            public void run() {
                mIGLGestureListener.onFling(x1, y1, x2, y2, velocityX,
                        velocityY);

            }
        });

        return false;
    }

    // /////////
    @Override
    protected void onDetachedFromWindow() {
        sMultiWindowView = null;
        mRender.destory();
        super.onDetachedFromWindow();
    }

    @Override
    public void onSurfaceCreated() {
    	GLRender.CheckOnGLThread();
    }

    @Override
    public void onSurfaceChanged(int w, int h) {
        for (Runnable r : mRendeInitEvent) {
            r.run(); 
         }

         mRendeInitEvent.clear();
    }

    @Override
    public void requestRender(boolean once) {
        if (once) {
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        } else {
            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        }

        requestRender();
    }

    public void doGLTask(final Runnable r) {
        if (r == null)
            return;

        if (!GLRender.IsOnGLThread()) {
            if (!GLRender.isRenderOK() && !mRendeInitEvent.contains(r)) {
                mRendeInitEvent.add(r);
            } else {
                queueEvent(r);
            }
        } else {
            r.run();
        }
    }

    private void configureSurface(Context context) {
        mGestureDector = new GestureDetector(context, this);
        mRender = new GLRender(this, mRootScene);

        setEGLConfigChooser(new MultisampleConfigChooser());

        setGLWrapper(new GLLogWrapper(
                GLConfigure.getInstance().enableGLCallLog(), 
                GLConfigure.getInstance().enableGLErrorCheck()));

        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        setPreserveEGLContextOnPause(true);
    }

    private void postGLViewInitTask() {
        final GLView rootView = mRootScene.rootview();
        rootView.detachFromThread();
        mRootScene.detachFromThread();

        Runnable gl_init_task = new Runnable() {
            public void run() {

                mIGLGestureListener = mRender.getGestrueListener();
            }
        };

        mRendeInitEvent.add(gl_init_task);
    }
}
