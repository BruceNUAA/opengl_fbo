package com.test.gl_draw;

import junit.framework.Assert;
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

	private boolean mIsRunning = false;
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

	public boolean isRunning() {
		return mIsRunning;
	}

	public float[] getAnimationArgs() {
		return new float[] { mStart, mEnd, mDuration };
	}

	public void setAnimationArgs(float[] args) {
		Assert.assertTrue(args.length >= 3);
		
		mStart = args[0];
		mEnd = args[1];
		mDuration = (long) args[2];
		mIsRunning = false;
	}

	public float getAnimationPos() {
		if (mIsRunning == false)
			return 0;

		return (mCurrentTime - mStartedTime) / (float) mDuration;
	}

	public float getAnimationValue() {
		if (mIsRunning == false)
			return 0;

		return (mStart - mEnd) * getAnimationPos() + mEnd;
	}

	public void start() {
		mIsRunning = true;
		mStartedTime = SystemClock.uptimeMillis();
		mCurrentTime = mStartedTime;
		if (mListener == null)
			return;

		Render.RegistTimer(this);
	}

	public void stop() {
		if (mListener == null)
			return;

		mIsRunning = false;
		mListener.OnAnimationEnd();

		Render.UnRegistTimer(this);
	}

	@Override
	public void OnTick() {
		if (!mIsRunning || mListener == null)
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
