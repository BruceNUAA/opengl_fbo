package com.test.gl_draw.test;

import android.graphics.RectF;

import com.test.gl_draw.KApplication;
import com.test.gl_draw.gl_base.GLTimer;
import com.test.gl_draw.glview.GLTextureView;
import com.test.gl_draw.igl_draw.IGLView;
import com.test.gl_draw.utils.helper.DebugToast;
import com.test.gl_draw.utils.helper.ThreadUtils;

public class GLZoomView extends GLTextureView implements
		GLTimer.OnAnimatListener {

	private RectF mStartRect = new RectF();
	private RectF mEndRect = new RectF();

	private GLTimer mTimer = null;

	IGLView.OnTouchListener mTouchLisener = new IGLView.OnTouchListener() {

		@Override
		public boolean OnClick(final IGLView v) {
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
	}

	@Override
	public void OnAnimationStart() {

	}

	@Override
	public void OnAnimationUpdate(float last_v, float new_v) {

		float l = mStartRect.left * (1 - new_v) + mEndRect.left * new_v;
		float t = mStartRect.top * (1 - new_v) + mEndRect.top * new_v;
		float r = mStartRect.right * (1 - new_v) + mEndRect.right * new_v;
		float b = mStartRect.bottom * (1 - new_v) + mEndRect.bottom * new_v;

		getBackgoundDraw().SetAlpha(1 - new_v);
		SetBounds(l, t, r - l, b - t);
	}

	@Override
	public void OnAnimationEnd() {
		SetBounds(mStartRect);
		getBackgoundDraw().SetAlpha(1);
	}

	private void start() {
		if (mTimer != null && mTimer.isRunning()) {
			mTimer.stop();
		}

		mTimer = GLTimer.ValeOf(0, 1, 1000, this);
		mTimer.start();
	}

}
