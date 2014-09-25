
package com.test.gl_draw.utils;

import javax.microedition.khronos.opengles.GL10;

import com.example.gl_fbo.BuildConfig;

public class NonThreadSafe {
    private Thread mThread = null;

    public NonThreadSafe() {
        ensureThreadIdAssigned();
    }

    private void ensureThreadIdAssigned() {
        if (this.mThread == null) {
            this.mThread = Thread.currentThread();
        }
    }

    public void CheckThread() {
        if (!BuildConfig.DEBUG)
            return;

        ensureThreadIdAssigned();
        if (mThread != Thread.currentThread()) {
            throw new RuntimeException("Call on invalided thread!");
        }
    }
    
    public void CheckThreadError(GL10 gl) {
    	if (!BuildConfig.DEBUG)
    		return;
    	if (gl == null) {
    	    GLHelper20.checkGLError();
    	} else {
    	    GLHelper.checkGLError(gl);
    	}
    }

    public void detachFromThread() {
        mThread = null;
    }
}
