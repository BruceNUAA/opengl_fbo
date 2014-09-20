package com.test.gl_draw.gl_base;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;

import com.test.gl_draw.igl_draw.IScene;
import com.test.gl_draw.igl_draw.ITouchEvent;

public class GLRender implements GLSurfaceView.Renderer {

	// /
	public interface IRenderFrame {
		public void OnFrame(GL10 gl);
	}

	public interface IRenderMsg {

		void onSurfaceCreated();

		void onSurfaceChanged(int w, int h);

		void requestRender(boolean once);
	}

	// static block {
	private static GLRender sRender = null;

	public static boolean isRenderOK() {
		return sRender != null;
	}

	public static void RegistFrameCallback(IRenderFrame iframe) {
		if (!isRenderOK())
			return;

		if (sRender.mRenderFameCallBack.contains(iframe)) {
			return;
		}

		sRender.mRenderFameCallBack.add(iframe);
	}

	public static void UnRegistFrameCallback(IRenderFrame iframe) {
		if (!isRenderOK())
			return;

		if (!sRender.mRenderFameCallBack.contains(iframe)) {
			return;
		}

		sRender.mRenderFameCallBack.remove(iframe);
	}

	public static void RequestRender(final boolean once) {
		if (!isRenderOK())
			return;

		sRender.mMainUIHandler.post(new Runnable() {

			@Override
			public void run() {
				sRender.mIRenderMsg.requestRender(once);
			}
		});
	}

	public static GL10 GL() {
		if (!isRenderOK())
			return null;
		return sRender.mGl;
	}

	public static boolean IsOnGLThread() {
		if (!isRenderOK())
			return false;

		return Thread.currentThread() == sRender.mGLThread;
	}

	// }

	// /
	private IScene mMainScene;
	private ITouchEvent mGestureListener;

	private Handler mMainUIHandler;
	private IRenderMsg mIRenderMsg;

	private CopyOnWriteArrayList<IRenderFrame> mRenderFameCallBack;

	private GL10 mGl;

	private Thread mGLThread;

	//
	public GLRender(IRenderMsg iRenderMsg, IScene scene) {
		Assert.assertTrue(!isRenderOK());

		mMainScene = scene;
		mGestureListener = scene.getEventHandle();

		mMainUIHandler = new Handler(Looper.getMainLooper());
		mIRenderMsg = iRenderMsg;
		mRenderFameCallBack = new CopyOnWriteArrayList<GLRender.IRenderFrame>();
	}

	public IScene getMainScene() {
		return mMainScene;
	}

	public ITouchEvent getGestrueListener() {
		return mGestureListener;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		mGl = gl;
		sRender = this;
		mMainScene.onSurfaceCreated(gl);

		mGLThread = Thread.currentThread();

		mMainUIHandler.post(new Runnable() {

			@Override
			public void run() {
				mIRenderMsg.onSurfaceCreated();
			}
		});

	}

	@Override
	public void onSurfaceChanged(GL10 gl, final int w, final int h) {
		mGl = gl;

		mMainScene.onSurfaceChanged(gl, w, h);

		mMainUIHandler.post(new Runnable() {

			@Override
			public void run() {
				mIRenderMsg.onSurfaceChanged(w, h);
			}
		});

	}

	@Override
	public void onDrawFrame(GL10 gl) {
		for (IRenderFrame iframe : mRenderFameCallBack)
			iframe.OnFrame(gl);

		mMainScene.onDrawFrame(gl);
	}

	public void destory() {
		mMainScene.onDestory();
		mRenderFameCallBack.clear();
		GLRender.sRender = null;

		mGLThread = null;
	}
}
