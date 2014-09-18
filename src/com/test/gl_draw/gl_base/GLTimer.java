
package com.test.gl_draw.gl_base;

import javax.microedition.khronos.opengles.GL10;

import junit.framework.Assert;
import android.animation.TimeInterpolator;
import android.os.SystemClock;
import android.view.animation.LinearInterpolator;

import com.test.gl_draw.utils.NonThreadSafe;

public class GLTimer extends NonThreadSafe implements GLRender.IRenderFrame {
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

    private TimeInterpolator mInterpolator = null;

    public static GLTimer ValeOf(float a, float b, long duration,
            OnAnimatListener li) {
        GLTimer timer = new GLTimer();

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
        return new float[] {
                mStart, mEnd, mDuration
        };
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

        return mInterpolator.getInterpolation((mCurrentTime - mStartedTime) / (float) mDuration);
    }

    public float getAnimationValue() {
        if (mIsRunning == false)
            return 0;

        return (mEnd - mStart) * getAnimationPos() + mStart;
    }

    public void setInterpolator(TimeInterpolator value) {
        CheckThread();
        
        if (value != null) {
            mInterpolator = value;
        } else {
            mInterpolator = new LinearInterpolator();
        }
    }

    public void start() {
        CheckThread();
        
        mIsRunning = true;
        mStartedTime = SystemClock.uptimeMillis();
        mCurrentTime = mStartedTime;
        if (mListener == null)
            return;

        GLRender.RegistFrameCallback(this);
        GLRender.RequestRender(false);
    }

    public void stop() {
        CheckThread();
        
        GLRender.RequestRender(true);
        
        if (mListener == null)
            return;

        mIsRunning = false;
        mListener.OnAnimationEnd();

        GLRender.UnRegistFrameCallback(this);
    }

    @Override
    public void OnFrame(GL10 gl) {
        CheckThread();
        
        if (!mIsRunning || mListener == null)
            return;

        long current = SystemClock.uptimeMillis();

        if (current > mStartedTime + mDuration) {
            stop();
        } else {
            if (mCurrentTime == mStartedTime) {
                mListener.OnAnimationStart();
            }

            float last_pos = mInterpolator.getInterpolation((mCurrentTime - mStartedTime)
                    / (float) mDuration);
            float new_pos = mInterpolator.getInterpolation((current - mStartedTime)
                    / (float) mDuration);

            last_pos = (mEnd - mStart) * last_pos + mStart;
            new_pos =  (mEnd - mStart) * new_pos + mStart;
            mListener.OnAnimationUpdate(last_pos, new_pos);
        }

        mCurrentTime = current;
    }
}
