package com.test.gl_draw;

import android.os.SystemClock;

public class GLTimer implements Render.ITimer {
	public interface OnAnimatListener {
		void OnAnimationStart();

		void OnAnimationUpdate(float last_v, float new_v);

		void OnAnimationEnd();
	}

	private float mStart;
	private float mEnd;
	private long mDuration;
	private OnAnimatListener mListener;

	private boolean isRunning = false;
	private long mStartedTime;
	private long mCurrentTime;

	public static GLTimer ValeOf(float a, float b, long duration,
			OnAnimatListener li) {
		GLTimer timer = new GLTimer();

		timer.mStart = a;
		timer.mEnd = b;
		timer.mDuration = duration;
		timer.mListener = li;
		return timer;
	}

	public float getAnimationPos() {
		if (isRunning == false)
			return 0;

		return (mCurrentTime - mStartedTime) / (float) mDuration;
	}

	public void start() {
		isRunning = true;
		mStartedTime = SystemClock.uptimeMillis();
		mCurrentTime = mStartedTime;
		if (mListener == null)
			return;

		Render.RegistTimer(this);
	}

	public void stop() {
		if (mListener == null)
			return;

		isRunning = false;
		mListener.OnAnimationEnd();

		Render.UnRegistTimer(this);
	}

	@Override
	public void OnTick() {
		if (!isRunning || mListener == null)
			return;

		long current = SystemClock.uptimeMillis();

		if (current > mStartedTime + mDuration) {
			stop();
		} else {
			if (mCurrentTime == mStartedTime) {
				mListener.OnAnimationStart();
			}

			float last_pos = (mCurrentTime - mStartedTime) / (float) mDuration;
			float new_pos = (current - mStartedTime) / (float) mDuration;

			mListener.OnAnimationUpdate((mStart - mEnd) * last_pos + mEnd,
					(mStart - mEnd) * new_pos + mEnd);
		}

		mCurrentTime = current;
	}
}
