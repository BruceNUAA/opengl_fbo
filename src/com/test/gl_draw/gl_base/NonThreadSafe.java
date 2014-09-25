
package com.test.gl_draw.gl_base;

import java.util.HashSet;
import java.util.Set;

import com.test.gl_draw.utils.GLHelper20;

public class NonThreadSafe {
    private Thread mThread = null;

    // 确保BeforeThreadCall和AfterThreadCall成对调用
    private Set<Integer> mCallCount = new HashSet<Integer>();

    public NonThreadSafe() {
        ensureThreadIdAssigned();
    }

    private void ensureThreadIdAssigned() {
        if (this.mThread == null) {
            this.mThread = Thread.currentThread();
        }
    }

    public void BeforeThreadCall() {
        if (!GLConfigure.getInstance().enableDebug())
            return;

        int call_deep = Thread.currentThread().getStackTrace().length;

        if (mCallCount.contains(call_deep)) {
            throw new RuntimeException("BeforeThreadCall和AfterThreadCall应该成对调用!");
        }

        mCallCount.add(call_deep);

        ensureThreadIdAssigned();
        if (mThread != Thread.currentThread()) {
            throw new RuntimeException("Call on invalided thread!");
        }
    }

    public void AfterThreadCall() {
        if (!GLConfigure.getInstance().enableDebug())
            return;

        int call_deep = Thread.currentThread().getStackTrace().length;

        if (!mCallCount.contains(call_deep)) {
            throw new RuntimeException("BeforeThreadCall和AfterThreadCall应该成对调用!");
        }

        mCallCount.remove(call_deep);

        GLHelper20.checkGLError();
    }

    public void detachFromThread() {
        mThread = null;
    }
}
