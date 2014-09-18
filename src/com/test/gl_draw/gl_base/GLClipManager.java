
package com.test.gl_draw.gl_base;

import android.graphics.RectF;
import android.opengl.GLES20;

import com.test.gl_draw.utils.NonThreadSafe;

public class GLClipManager extends NonThreadSafe {

    private static GLClipManager sClipManager = null;

    public static GLClipManager getInstance() {
        if (sClipManager == null) {
            sClipManager = new GLClipManager();
        }
        return sClipManager;
    }

    private RectF mClipRect = new RectF();

    private float[] mAttachScreenSize = new float[2];
    private float[] mOffsetXY = new float[2];
    private boolean mRenderIsFBO = false;

    private GLClipManager() {

    }

    public void setScreenSize(boolean render_is_frame_buffer, float offset_x, float offset_y,
            float screenW, float screenH) {
        CheckThread();

        if (screenW == 0 || screenH == 0)
            return;

        mRenderIsFBO = render_is_frame_buffer;

        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);

        mAttachScreenSize[0] = screenW;
        mAttachScreenSize[1] = screenH;

        mOffsetXY[0] = offset_x;
        mOffsetXY[1] = offset_y;
    }

    public void ClipRect(RectF rc) {
        CheckThread();

        if (rc.isEmpty())
            return;

        mClipRect.set(rc);

        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);

        float x, y;
        
        if (mRenderIsFBO) {
            x = mOffsetXY[0] + mClipRect.left;
            y = mClipRect.top + mOffsetXY[1];
        } else {
            x = mOffsetXY[0] + mClipRect.left;
            y = mAttachScreenSize[1]
                    - mClipRect.bottom - mOffsetXY[1];
        }

        GLES20.glScissor(
                (int) x,
                (int) y,
                (int) mClipRect.width(), (int) mClipRect.height());

    }

    public void DisableClip() {
        CheckThread();

        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
    }
}
