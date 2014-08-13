package com.test.gl_draw;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Looper;

import com.example.gl_fbo.R;
import com.test.gl_draw.test.TestSprite1;
import com.test.gl_draw.test.TestSprite2D;

public class Render implements GLSurfaceView.Renderer {

	// static block {
	private static Render sRender = null;

	public static void RegistTimer(GLTimer timer) {
		if (sRender == null)
			return;
		if (sRender.mTimer.contains(timer)) {
			return;
		}

		sRender.mTimer.add(timer);
	}

	public static void UnRegistTimer(GLTimer timer) {
		if (sRender == null)
			return;

		if (!sRender.mTimer.contains(timer)) {
			return;
		}

		sRender.mTimer.remove(timer);
	}

	public static void RequestRender(final boolean once) {
		if (sRender == null)
			return;

		sRender.mMainUIHandler.post(new Runnable() {

			@Override
			public void run() {
				sRender.mIRenderMsg.requestRender(once);
			}
		});
	}

	// }

	// /
	private MainScene2D mMainScene;

	private Handler mMainUIHandler;
	private IRenderMsg mIRenderMsg;

	private CopyOnWriteArrayList<ITimer> mTimer;

	private Context mAPPContext = null;

	private int mRenderW = 0;
	private int mRenderH = 0;

	//
	public Render(Context app_context, IRenderMsg iRenderMsg) {
		Assert.assertTrue(sRender == null);
		mAPPContext = app_context;

		sRender = this;
		mMainScene = new MainScene2D();

		mMainUIHandler = new Handler(Looper.getMainLooper());
		mIRenderMsg = iRenderMsg;
		mTimer = new CopyOnWriteArrayList<Render.ITimer>();
	}

	public MainScene2D getMainScene() {
		return mMainScene;
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		mMainScene.onSurfaceCreated(gl, config);

		mMainUIHandler.post(new Runnable() {

			@Override
			public void run() {
				mIRenderMsg.onSurfaceCreated();
			}
		});
	}

	@Override
	public void onSurfaceChanged(GL10 gl, final int w, final int h) {
		mRenderW = w;
		mRenderH = h;

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
		for (ITimer iTimer : mTimer)
			iTimer.OnTick();
		mMainScene.onDrawFrame(gl);
	}

	public void destory() {
		mTimer.clear();
		Render.sRender = null;
	}

	public void test() {
		Context context = Render.sRender.mAPPContext;
		if (true) {
			Bitmap test_img = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.img);
			Bitmap test_img2 = BitmapFactory.decodeResource(
					context.getResources(), R.drawable.port_img);

			RectF render_rect = new RectF(0, 0, mRenderW, mRenderH);

			TestSprite2D testSprite2D = new TestSprite2D(render_rect, test_img,
					test_img2);
			
			for (ISprite i : testSprite2D.getSprite()) {
				Render.sRender.getMainScene().getSpriteManager().adddSprite(i);
			}

		} else {
			if (true) {
				Bitmap test_img = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.img);

				Render.sRender.getMainScene().getSpriteManager()
						.adddSprite(new TestSprite1(test_img));

			}

			if (true) {
				Bitmap test_img = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.port_img);

				Render.sRender.getMainScene().getSpriteManager()
						.adddSprite(new TestSprite1(test_img));

			}
			if (true) {
				Bitmap test_img = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.ic_launcher);

				Render.sRender.getMainScene().getSpriteManager()
						.adddSprite(new TestSprite1(test_img));

			}
		}
	}

	// /
	public interface ITimer {
		public void OnTick();
	}

	public interface IRenderMsg {

		void onSurfaceCreated();

		void onSurfaceChanged(int w, int h);

		void requestRender(boolean once);
	}
}
