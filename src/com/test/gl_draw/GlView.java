package com.test.gl_draw;

import junit.framework.Assert;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.gl_fbo.R;
import com.test.gl_draw.d2.MainScene2D;
import com.test.gl_draw.igl_draw.IGLGestureListener;

public class GlView extends GLSurfaceView implements Render.IRenderMsg,
		View.OnTouchListener, GestureDetector.OnGestureListener {

	private Render mRender;
	private GestureDetector mGestureDector;

	private IGLGestureListener mIGLGestureListener;

	public GlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);
		mGestureDector = new GestureDetector(context, this);
		MainScene2D scene2d = new MainScene2D();
		
		mRender = new Render(context.getApplicationContext(), this, scene2d, scene2d);

		mIGLGestureListener = mRender.getGestrueListener();
		setEGLConfigChooser(8, 8, 8, 8, 0, 0);

		setRenderer(mRender);
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		setZOrderOnTop(true);
		setBackgroundResource(R.drawable.bg);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
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
		mRender.destory();
		super.onDetachedFromWindow();
	}

	@Override
	public void onSurfaceCreated() {
		Assert.assertTrue(Looper.getMainLooper().getThread() == Thread
				.currentThread());
		queueEvent(new Runnable() {

			@Override
			public void run() {
				mRender.test();
			}
		});
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

}
