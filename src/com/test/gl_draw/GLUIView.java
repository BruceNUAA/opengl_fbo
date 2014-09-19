
package com.test.gl_draw;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import junit.framework.Assert;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.test.gl_draw.gl_base.GLRender;
import com.test.gl_draw.gl_base.MultisampleConfigChooser;
import com.test.gl_draw.glview.GLRootScene;
import com.test.gl_draw.glview.GLView;
import com.test.gl_draw.igl_draw.IGLDispatchEvent;
import com.test.gl_draw.igl_draw.ITouchEvent;
import com.test.gl_draw.utils.helper.ThreadUtils;

public class GLUIView extends GLSurfaceView implements GLRender.IRenderMsg,
        View.OnTouchListener, GestureDetector.OnGestureListener, IGLDispatchEvent {

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

        configureSurface(context);

        setOnTouchListener(this);

        postGLViewInitTask();

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
        sMultiWindowView = this;
        Assert.assertTrue(Looper.getMainLooper().getThread() == Thread
                .currentThread());

        for (Runnable r : mRendeInitEvent) {
            queueEvent(r);
        }

        mRendeInitEvent.clear();
    }

    @Override
    public void onSurfaceChanged(int w, int h) {
        Assert.assertTrue(Looper.getMainLooper().getThread() == Thread
                .currentThread());
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

    private boolean isOnGLThread() {
        return Looper.getMainLooper().getThread() != Thread
                .currentThread();
    }

    @Override
    public void doGLTask(final Runnable r) {
        if (r == null)
            return;

        if (isOnGLThread()) {
            r.run();
        } else {
            if (!GLRender.isRenderOK() && !mRendeInitEvent.contains(r)) {
                mRendeInitEvent.add(r);
            } else {
                queueEvent(r);
            }
        }
    }

    @Override
    public void doUITask(Runnable r) {
        if (r == null)
            return;

        ThreadUtils.postOnUiThread(r);
    }

    private void configureSurface(Context context) {
        mGestureDector = new GestureDetector(context, this);
        mRender = new GLRender(this, mRootScene);
 
        if (true) {
            setEGLConfigChooser(new MultisampleConfigChooser());
        } else {
            setEGLConfigChooser(new EGLConfigChooser() {
                @Override
                public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
                    
                    int[] attrList = new int[] {
                            //
                            EGL10.EGL_SURFACE_TYPE, EGL10.EGL_WINDOW_BIT, //
                            EGL10.EGL_DEPTH_SIZE, 0, //
                            EGL10.EGL_BUFFER_SIZE, 0,//
                            EGL10.EGL_SAMPLE_BUFFERS, 1,//
                           EGL10.EGL_SAMPLES, 4, //
                            EGL10.EGL_NONE
                            //
                    };

                    EGLConfig[] configOut = new EGLConfig[1];
                    int[] configNumOut = new int[1];
                    egl.eglChooseConfig(display, attrList, configOut, 1,
                            configNumOut);

                    return configOut[0];
                }
            });

        }
       
        setRenderer(mRender);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        setPreserveEGLContextOnPause(true);
        setZOrderOnTop(true);
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
