
package com.test.gl_draw.gl_base;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.util.Pair;

import com.test.gl_draw.utils.GLHelper20;

public class GLObject extends NonThreadSafe {

    // 确保BeforeThreadCall和AfterThreadCall成对调用
    private List<Pair<Integer, String>> mCallStackTrace = new ArrayList<Pair<Integer, String>>();
    private List<Long> mCallTime = new ArrayList<Long>();

    private Throwable mThrowable = new Throwable();

    private boolean mShowDebugTime = GLConfigure.getInstance().enableGLViewTimeLog();

    public void SetShowDebugTime(boolean show) {
        mShowDebugTime = show;
    }

    @Override
    public boolean enableThreadCheck() {
        return GLConfigure.getInstance().enableGLThreadCheck();
    }

    public void BeforeThreadCall() {
        super.ThreadCheck();

        if (!GLConfigure.getInstance().enableGLViewErrorCheck() && !mShowDebugTime)
            return;

        checkCallBefore();
    }

    public void AfterThreadCall() {

        if (!GLConfigure.getInstance().enableGLViewErrorCheck() && !mShowDebugTime)
            return;

        checkCallAfter();

        GLHelper20.checkGLError();
    }

    private void checkCallBefore() {

        mThrowable.fillInStackTrace();
        StackTraceElement[] call_stack = mThrowable.getStackTrace();

        int inspact_place_deep = 3;

        if (call_stack == null || call_stack.length < inspact_place_deep)
            return;

        int call_deep = call_stack.length;

        int n = mCallStackTrace.size();

        if (n > 0) {
            Pair<Integer, String> last_call = mCallStackTrace.get(n - 1);
            if (last_call.first >= call_deep) {
                throwException(last_call.second);
                return;
            }
        }

        StringBuilder info = new StringBuilder();

        if (GLConfigure.getInstance().enableGLViewErrorCheck()) {
            StackTraceElement inspact_place = call_stack[inspact_place_deep - 1];

            info.append(getClass().getSimpleName());
            info.append(":");
            info.append(inspact_place.getMethodName());
            info.append("(");
            info.append(inspact_place.getLineNumber());
            info.append("）");

        }

        mCallStackTrace.add(new Pair<Integer, String>(call_deep, info
                .toString()));

        mCallTime.add(System.nanoTime());
    }

    private void checkCallAfter() {
        mThrowable.fillInStackTrace();
        StackTraceElement[] call_stack = mThrowable.getStackTrace();
        StackTraceElement inspact_place = null;

        int inspact_place_deep = 3;

        if (call_stack == null || call_stack.length < inspact_place_deep)
            return;

        inspact_place = call_stack[inspact_place_deep - 1];

        StringBuilder info = new StringBuilder();

        int call_deep = call_stack.length;

        int n = mCallStackTrace.size();

        do {
            String error_info = null;

            if (n > 0) {
                Pair<Integer, String> last_call = mCallStackTrace.get(n - 1);
                if (last_call.first != call_deep) {
                    error_info = last_call.second;
                } else {
                    break;
                }
            } else if (GLConfigure.getInstance().enableGLViewErrorCheck()) {
                info.append(getClass().getSimpleName());
                info.append(":");
                info.append(inspact_place.getMethodName());
                info.append("(");
                info.append(inspact_place.getLineNumber());
                info.append("）");

                error_info = info.toString();
            }

            throwException(error_info);
            return;

        } while (false);

        mCallStackTrace.remove(n - 1);

        long this_call_time = System.nanoTime() - mCallTime.get(n - 1);
        mCallTime.remove(n - 1);

        if (mShowDebugTime) {
            info.append(getClass().getSimpleName());
            info.append(":");
            info.append(inspact_place.getMethodName());
            Log.v(info.toString(), "Call Time: " + this_call_time / 1000.0f + "*0.001(ms)");
        }
    }

    private void throwException(String last_call_place) {
        if (!GLConfigure.getInstance().enableGLViewErrorCheck())
            return;

        StringBuffer b = new StringBuffer();
        b.append("\nFunc:[BeforeThreadCall] and Func:[AfterThreadCall] should be call pairly!");
        b.append("\nLast call place: ");
        b.append(last_call_place);
        throw new RuntimeException(b.toString(), mThrowable);
    }
}
