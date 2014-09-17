package com.test.gl_draw.d2.test;

import android.graphics.RectF;
import android.util.Log;

import com.test.gl_draw.d2.Sprite2D;
import com.test.gl_draw.d2.SpriteDataProvider2D;
import com.test.gl_draw.gl_base.GLTimer;
import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.igl_draw.ISprite;

public class TestFrameBuffer2D implements GLTimer.OnAnimatListener {

	private GLTimer mTimer;

	private Sprite2D mSpriteFBO = new Sprite2D();

	private SpriteDataProvider2D mSpriteDataProviderFBO = new SpriteDataProvider2D();

	private Sprite2D mSprite2d = new Sprite2D();
	private Sprite2D mSprite2d2 = new Sprite2D();

	private SpriteDataProvider2D mSpriteDataProvider = new SpriteDataProvider2D();
	private SpriteDataProvider2D mSpriteDataProvider2 = new SpriteDataProvider2D();

	private RectF mRenderRectF = new RectF();
	//
	private int mFrameCount = 0;
	private int mTestDuration = 2000;

	public boolean mTestFrameBuffer = false;

	public TestFrameBuffer2D(RectF render_rect, Texture texture1,
			Texture texture2) {
		mRenderRectF.set(render_rect);

		{
			mSpriteDataProvider.setOrigin(0, 0);
			mSpriteDataProvider.setTexture(texture1);
			mSpriteDataProvider.setAlpha(1);

			int[] size = texture1.getTextSize();
			mSpriteDataProvider.setRect(-size[0] / 2, -size[1] / 2, size[0],
					size[1]);
			mSprite2d.setDataProvider(mSpriteDataProvider);
		}

		{
			// mSpriteDataProvider2.setOrigin(0, 100);
			mSpriteDataProvider2.setTexture(texture2);
			mSpriteDataProvider2.setAlpha(1);

			if (!mTestFrameBuffer) {
				mSpriteDataProviderFBO.setRotateOrigin(0, 800);
			}

			int[] size = texture2.getTextSize();
			mSpriteDataProvider2.setRect(-size[0] / 2, -size[1] / 2, size[0],
					size[1]);
			mSprite2d2.setDataProvider(mSpriteDataProvider2);
		}

		if (mTestFrameBuffer) {
			// mSpriteDataProvider2.setOrigin(0, 100);
			mSpriteDataProviderFBO.setAlpha(1);

			mSpriteDataProviderFBO.setRotateOrigin(0, 800);

			float w = mRenderRectF.width() * 0.8f;
			float h = mRenderRectF.height() * 0.8f;

			mSpriteDataProviderFBO.setRect(-w / 2, -h / 2, w, h);

			mSpriteFBO.setDataProvider(mSpriteDataProviderFBO);

		}

		StartTimer();
	}

	public ISprite[] getSprite() {
		return mTestFrameBuffer ? new ISprite[] { mSpriteFBO } : new ISprite[] {
				mSprite2d, mSprite2d2 };
	}

	@Override
	public void OnAnimationStart() {
		mFrameCount = 0;
	}

	@Override
	public void OnAnimationUpdate(float last_v, float new_v) {
		mFrameCount++;

		if (mTestFrameBuffer) {
			float[] rect = mSpriteDataProviderFBO.getRenderRect();
			float x = mTimer.getAnimationValue() * mRenderRectF.width()
					- (rect[2] - rect[0]) / 2;

			float y = (rect[3] - rect[1]) / 2;
			// mSpriteDataProvider2.setOrigin(x, y);
			mSpriteDataProviderFBO
					.setRotateDegree(mTimer.getAnimationValue() * 10);
			mSpriteDataProviderFBO.setAlpha((1 - Math.abs(mTimer
					.getAnimationValue())) * 0.5f + 0.5f);
		} else {
			{
				mSpriteDataProvider
						.setRotateDegree(mTimer.getAnimationValue() * 180);
				mSpriteDataProvider.setAlpha(mTimer.getAnimationValue());
			}
			{

				float[] rect = mSpriteDataProvider2.getRenderRect();
				float x = mTimer.getAnimationValue() * mRenderRectF.width()
						- (rect[2] - rect[0]) / 2;

				float y = (rect[3] - rect[1]) / 2;
				// mSpriteDataProvider2.setOrigin(x, y);
				mSpriteDataProvider2
						.setRotateDegree(mTimer.getAnimationValue() * 10);
				mSpriteDataProvider2.setAlpha((1 - Math.abs(mTimer
						.getAnimationValue())) * 0.5f + 0.5f);
			}
		}
	}

	@Override
	public void OnAnimationEnd() {

		Log.w("TestSprite:", mTestDuration + ":\t" + mFrameCount + "-- \t");

		StartTimer();
	}

	private void StartTimer() {
		if (mTimer == null) {
			mTimer = GLTimer.ValeOf(1, -1f, mTestDuration, this);
		} else {

			float[] args = mTimer.getAnimationArgs();

			mTimer = GLTimer.ValeOf(args[1], args[0], (long) args[2], this);
		}

		mTimer.start();
	}
}
