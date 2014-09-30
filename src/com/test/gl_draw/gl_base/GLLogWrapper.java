
package com.test.gl_draw.gl_base;

import java.io.Writer;

import javax.microedition.khronos.opengles.GL;

import android.opengl.GLDebugHelper;
import android.opengl.GLSurfaceView;
import android.util.Log;

public class GLLogWrapper implements GLSurfaceView.GLWrapper {

    private boolean mEnableGLCallLog = false;

    private boolean mEnableGLErrorCheck = false;

    public GLLogWrapper(boolean enable_call_log, boolean eanble_call_check) {
        mEnableGLCallLog = enable_call_log;
        mEnableGLErrorCheck = eanble_call_check;
    }

    @Override
    public GL wrap(GL gl) {

        if (!mEnableGLCallLog && !mEnableGLErrorCheck) {
            return gl;
        } else {

            int configFlags = 0;

            if (mEnableGLErrorCheck) {
                configFlags = GLDebugHelper.CONFIG_CHECK_GL_ERROR
                        | GLDebugHelper.CONFIG_CHECK_THREAD | GLDebugHelper.ERROR_WRONG_THREAD
                        | GLDebugHelper.CONFIG_LOG_ARGUMENT_NAMES;
            }

            Writer log = null;
            if (mEnableGLCallLog) {
                log = new LogWriter();
            }

            return GLDebugHelper.wrap(gl, configFlags, log);
        }

    }

    class LogWriter extends Writer {

        @Override
        public void close() {
            flushBuilder();
        }

        @Override
        public void flush() {
            flushBuilder();
        }

        @Override
        public void write(char[] buf, int offset, int count) {
            for (int i = 0; i < count; i++) {
                char c = buf[offset + i];
                if (c == '\n') {
                    flushBuilder();
                }
                else {
                    mBuilder.append(c);
                }
            }
        }

        private void flushBuilder() {
            if (mBuilder.length() > 0) {

                Log.v("GLSurfaceView", mBuilder.toString());
                mBuilder.delete(0, mBuilder.length());
            }
        }

        private StringBuilder mBuilder = new StringBuilder();
    }

}
