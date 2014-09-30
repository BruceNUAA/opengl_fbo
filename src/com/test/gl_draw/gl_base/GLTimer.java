package com.test.gl_draw.gl_base;

import javax.microedition.khronos.opengles.GL10;

import android.animation.TimeInterpolator;
import android.os.SystemClock;
import android.view.animation.LinearInterpolator;

public class GLTimer extends GLObject implements GLRender.IRenderFrame {

	public interface OnAnimatListener {
		void OnAnimationStart(float start_v);

		void OnAnimationUpdate(float last_v, float new_v);

		void OnAnimationEnd(float end_v);
	}

	private float mStart;
	private float mEnd;
	private long mDuration;
	private OnAnimatListener mListener;

	private boolean mIsRunning = false;
	private long mStartedTime;
	private long mCurrentTime;

	private TimeInterpolator mInterpolator = null;

	public static GLTimer ValeOf(float a, float b, long duration,
			OnAnimatListener li) {
		GLTimer timer = new GLTimer();

		if (duration == 0) {
		    // 避免NaN
		    duration = 1;
		}
		
		timer.mStart = a;
		timer.mEnd = b;
		timer.mDuration = duration;
		timer.mListener = li;
		timer.setInterpolator(null);
		return timer;
	}

	public boolean isRunning() {
		return mIsRunning;
	}

	public float[] getAnimationArgs() {
		return new float[] { mStart, mEnd, mDuration };
	}

	public float getAnimationPos() {
		if (mIsRunning == false)
			return 0;

		return mInterpolator.getInterpolation((mCurrentTime - mStartedTime)
				/ (float) mDuration);
	}

	public float getAnimationValue() {

		if (mIsRunning == false)
			return 0;

		return (mEnd - mStart) * getAnimationPos() + mStart;
	}

	public void setInterpolator(TimeInterpolator value) {
		GLRender.CheckOnGLThread();

		if (value != null) {
			mInterpolator = value;
		} else {
			mInterpolator = new LinearInterpolator();
		}
	}

	public void start() {
		BeforeThreadCall();

		mIsRunning = true;
		mStartedTime = SystemClock.uptimeMillis();
		mCurrentTime = mStartedTime;
		if (mListener != null) {
			mListener.OnAnimationStart(mStart);

			GLRender.RegistFrameCallback(this);
			GLRender.RequestRender(false);
		}

		AfterThreadCall();
	}

	public void stop() {
		BeforeThreadCall();

		GLRender.RequestRender(true);

		if (mListener != null) {
			mIsRunning = false;
			mListener.OnAnimationEnd(mEnd);

			GLRender.UnRegistFrameCallback(this);
		}

		AfterThreadCall();
	}

	@Override
	public void OnFrame(GL10 gl) {

		if (!mIsRunning || mListener == null)
			return;

		BeforeThreadCall();

		long current = SystemClock.uptimeMillis();

		if (current >= mStartedTime + mDuration) {
		    
		    mCurrentTime = mStartedTime + mDuration;
		    mListener.OnAnimationUpdate(mEnd, mEnd);
			stop();
		} else {
			float last_pos = mInterpolator
					.getInterpolation((mCurrentTime - mStartedTime)
							/ (float) mDuration);
			float new_pos = mInterpolator
					.getInterpolation((current - mStartedTime)
							/ (float) mDuration);

			last_pos = (mEnd - mStart) * last_pos + mStart;
			new_pos = (mEnd - mStart) * new_pos + mStart;
			mListener.OnAnimationUpdate(last_pos, new_pos);
		}

		mCurrentTime = current;

		AfterThreadCall();
	}
}
