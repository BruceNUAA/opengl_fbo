
package com.test.gl_draw.glview;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.Texture;

public class GLIndictorView extends GLTextureView {

    private float mPos;
    private float mLen;

    public void SetTexture(Texture texture) {
        throw new RuntimeException("Cann't be called!");
    }

    public void SetFillMode(TextureDraw.FillMode mode) {
        throw new RuntimeException("Cann't be called!");
    }

    public void SetPos(float pos, float len) {
        if (mPos == pos && mLen == len && !Bounds().isEmpty())
            return;

        mPos = pos;
        mLen = len;
        updatePos();
    }

    @Override
    public void SetBounds(RectF rc) {
        super.SetBounds(rc);
        updatePos();
    }
    
    private void updatePos() {
        if (Bounds().isEmpty())
            return;

        RectF rc = new RectF(Bounds());

        rc.left = mPos;
        rc.right = rc.left + mLen;
        
        getDraw().SetRenderRect(rc);
    }
}
