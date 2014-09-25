
package com.test.gl_draw.gl_base;

import android.animation.TimeInterpolator;
import android.os.SystemClock;
import android.view.animation.LinearInterpolator;

import junit.framework.Assert;

import javax.microedition.khronos.opengles.GL10;

public class GLTimer implements GLRender.IRenderFrame {
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
        GLRender.CheckOnGLThread();
        
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
        GLRender.CheckOnGLThread();
        
        mIsRunning = true;
        mStartedTime = SystemClock.uptimeMillis();
        mCurrentTime = mStartedTime;
        if (mListener == null)
            return;
        
        mListener.OnAnimationStart();

        GLRender.RegistFrameCallback(this);
        GLRender.RequestRender(false);
    }

    public void stop() {
        GLRender.CheckOnGLThread();
        
        GLRender.RequestRender(true);
        
        if (mListener == null)
            return;

        mIsRunning = false;
        mListener.OnAnimationEnd();

        GLRender.UnRegistFrameCallback(this);
    }

    @Override
    public void OnFrame(GL10 gl) {
        GLRender.CheckOnGLThread();
        
        if (!mIsRunning || mListener == null)
            return;

        long current = SystemClock.uptimeMillis();

        if (current >= mStartedTime + mDuration) {
            stop();
        } else {
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
