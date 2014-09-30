package com.test.gl_draw.test;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.test.gl_draw.KApplication;
import com.test.gl_draw.data.Texture;
import com.test.gl_draw.gl_base.GLTimer;
import com.test.gl_draw.glview.GLView;
import com.test.gl_draw.glview.NinePatchDraw;
import com.test.gl_draw.utils.helper.DebugToast;
import com.test.gl_draw.utils.helper.ThreadUtils;

public class GLZoomView extends GLView implements GLTimer.OnAnimatListener {

	private RectF mStartRect = new RectF();
	private RectF mEndRect = new RectF();

	private GLTimer mTimer = null;

	private NinePatchDraw mDraw = new NinePatchDraw();

	GLView.OnTouchListener mTouchLisener = new GLView.OnTouchListener() {

		@Override
		public boolean OnClick(final GLView v) {
			ThreadUtils.postOnUiThread(new Runnable() {

				@Override
				public void run() {

					long c = System.currentTimeMillis() % 1000;
					DebugToast.showLong(KApplication.sApplication,
							"GLZoomView:" + v.id() + ":\tTime: " + c);

				}
			});

			GLZoomView.this.start();
			return false;
		}
	};

	public GLZoomView() {
		setOnTouchLisener(mTouchLisener);
	}

	@Override
	public boolean onDown(float x, float y) {
		return true;
	}

	public void SetZoomRect(RectF start, RectF end) {
		mStartRect.set(start);
		mEndRect.set(end);
		int w = (int) Math.min(start.width(), start.height());

		Bitmap bitmap = null;
		try {

			bitmap = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(Color.YELLOW);
			paint.setStyle(Paint.Style.FILL);

			canvas.drawRoundRect(new RectF(0, 0, w, w), w / 2.0f, w / 2.0f,
					paint);
		} catch (Exception e) {

		} catch (Error e) {
		}
		if (bitmap == null)
			return;

		Texture texture = new Texture();
		texture.Init(bitmap);
		mDraw.setTexture(texture, new float[] { w / 2, w / 2, w / 2, w / 2 },
				null, true);
	}

	@Override
	public void OnDraw(GL10 gl) {
		super.OnDraw(gl);
		mDraw.Draw(gl);
	}

	@Override
	public void SetBounds(RectF rc) {
		super.SetBounds(rc);
		mDraw.setRect(rc);
	}

	@Override
	public void OnAnimationStart(float start) {

	}

	@Override
	public void OnAnimationUpdate(float last_v, float new_v) {

		float l = mStartRect.left * (1 - new_v) + mEndRect.left * new_v;
		float t = mStartRect.top * (1 - new_v) + mEndRect.top * new_v;
		float r = mStartRect.right * (1 - new_v) + mEndRect.right * new_v;
		float b = mStartRect.bottom * (1 - new_v) + mEndRect.bottom * new_v;

		mDraw.setAlpha(1 - new_v);
		mDraw.setCornerRate(1 - new_v);
		SetBounds(l, t, r - l, b - t);
	}

	@Override
	public void OnAnimationEnd(float end) {
		SetBounds(mStartRect);
		mDraw.setAlpha(1);
		mDraw.setCornerRate(1);
	}

	private void start() {
		if (mTimer != null && mTimer.isRunning()) {
			mTimer.stop();
		}

		mTimer = GLTimer.ValeOf(0, 1, 2000, this);
		mTimer.setInterpolator(new AccelerateDecelerateInterpolator());
		mTimer.start();
	}

}
