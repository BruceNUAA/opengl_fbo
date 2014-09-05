
package com.test.gl_draw.gl_base;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.test.gl_draw.utils.GLHelper;

public class Texture {

    private RectF mTextRectF = new RectF();
    private int[] mTexture = {
            0
    };
    private int mTextureOriginW = 0;
    private int mTextureOriginH = 0;

    // 仅用来判断纹理是否被重复加载
    private Bitmap mTextureBitmap = null;

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public void Init(Texture texture) {
        UnLoad();
        
        mTextRectF.set(texture.mTextRectF);
        mTexture[0] = texture.mTexture[0];
        mTextureOriginW = texture.mTextureOriginW;
        mTextureOriginH = texture.mTextureOriginH;
        mTextureBitmap = texture.mTextureBitmap;
    }

    public boolean Init(Bitmap b, boolean force) {
        if (b == null) {
            return false;
        }
        
        if (!force && GLHelper.isTexture(mTexture[0]))
            return true;

        UnLoad();

        mTextureBitmap = b;

        mTextureOriginW = b.getWidth();
        mTextureOriginH = b.getHeight();

        int new_w = mTextureOriginW;
        int new_h = mTextureOriginH;

        if (!GLHelper.checkIfContextSupportsNPOT()) {
            new_w = (int) GLHelper.cellPowerOf2(mTextureOriginW);
            new_h = (int) GLHelper.cellPowerOf2(mTextureOriginH);
        }

        float map_w = b.getWidth() / (float) new_w;
        float map_h = b.getHeight() / (float) new_h;
        float map_x = (1 - map_w) / 2;
        float map_y = (1 - map_h) / 2;

        mTextRectF.set(map_x, map_y, map_x + map_w, map_y + map_h);

        if (new_w != mTextureOriginW || new_h != mTextureOriginH) {
            Bitmap resizedBitmap = resizeBitmap(b, new_w, new_h);

            mTexture[0] = GLHelper.loadTexture(resizedBitmap);

            resizedBitmap.recycle();
        } else {
            mTexture[0] = GLHelper.loadTexture(b);
        }

        return GLHelper.isTexture(mTexture[0]);
    }

    public boolean Init(int w, int h, boolean force) {
        if (!force && GLHelper.isTexture(mTexture[0]))
            return true;
        
        UnLoad();

        mTextureOriginW = w;
        mTextureOriginH = h;

        int new_w = mTextureOriginW;
        int new_h = mTextureOriginH;

        if (!GLHelper.checkIfContextSupportsNPOT()) {
            new_w = (int) GLHelper.cellPowerOf2(mTextureOriginW);
            new_h = (int) GLHelper.cellPowerOf2(mTextureOriginH);
        }

        float map_w = w / (float) new_w;
        float map_h = h / (float) new_h;
        float map_x = (1 - map_w) / 2;
        float map_y = (1 - map_h) / 2;

        mTextRectF.set(map_x, map_y, map_x + map_w, map_y + map_h);

        mTexture[0] = GLHelper.createTargetTexture(new_w, new_h);
        return GLHelper.isTexture(mTexture[0]);
    }

    public boolean isValid() {
        return GLHelper.isTexture(mTexture[0]);
    }
    
    public void UnLoad() {
        mTextureBitmap = null;

        GLHelper.checkEGLContextOK();

        if (mTexture[0] != 0 && GLHelper.isTexture(mTexture[0])) {
            GLHelper.deleteTargetTexture(mTexture);
            mTexture[0] = 0;
        }
    }

    public RectF getTextRect() {
        return mTextRectF;
    }

    public int getTexture() {
        return mTexture[0];
    }

    public int[] getTextSize() {
        return new int[] {
                mTextureOriginW, mTextureOriginH
        };
    }

    //
    private Bitmap resizeBitmap(Bitmap b, int new_w, int new_h) {
        Bitmap resized_b = null;

        if (new_w < b.getWidth() || new_h < b.getHeight()) {
            throw new RuntimeException("resizeBitmap error!");
        }

        try {
            resized_b = Bitmap.createBitmap(new_w, new_h, Config.ARGB_4444);

            Canvas c = new Canvas(resized_b);
            RectF dst = new RectF((new_w - b.getWidth()) / 2.0f,
                    (new_h - b.getHeight()) / 2.0f,
                    (new_w + b.getWidth()) / 2.0f,
                    (new_h + b.getHeight()) / 2.0f);

            c.drawBitmap(b, null, dst, null);

        } catch (Exception e) {
            return null;
        } catch (Error e) {
            return null;
        }
        return resized_b;
    }
}