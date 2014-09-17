
package com.test.gl_draw.gl_base;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import com.test.gl_draw.utils.GLHelper;

public class Texture implements Cloneable {

    private enum TextureType {
        BITMAP,
        EMPTY_RECT,
    }

    private TextureType mType = TextureType.BITMAP;

    private Bitmap mBitmap;

    private String mStringTxt = null;

    private RectF mTextRectF = new RectF();
    private int mTexture = 0;

    private int mTextureOriginW = 0;
    private int mTextureOriginH = 0;

    private int[] mRealSize = {
            0, 0
    };
    
    private int sMaxTryCound = 3;
    
    private int mTryCount = sMaxTryCound;
    
    public Texture() {
        
    }
    
    public Texture(Texture t) {
        Init(t);
    }

    public void Init(Texture texture) {
        UnLoad();

        mType = texture.mType;
        mBitmap = texture.mBitmap;

        mTryCount = sMaxTryCound;
        
        mStringTxt = texture.mStringTxt;

        mTextRectF.set(texture.mTextRectF);
        mTexture = texture.mTexture;

        mTextureOriginW = texture.mTextureOriginW;
        mTextureOriginH = texture.mTextureOriginH;

        mRealSize = texture.mRealSize.clone();
    }

    public boolean Init(String text, boolean force, float text_size) {
        return Init(text, force, text_size, 0);
    }

    public boolean Init(String text, boolean force, float text_size, float max_width) {
        if (text == null || text.isEmpty() || text.equals(mStringTxt))
            return false;

        mStringTxt = new String(text);

        Bitmap bitmap = textToString(mStringTxt, max_width, text_size);

        if (bitmap == null)
            return false;

        return Init(bitmap);
    }

    public boolean Init(Bitmap b) {
        if (b == null) {
            return false;
        }

        if (isValid() && b.sameAs(mBitmap))
            return true;

        mType = TextureType.BITMAP;
        mBitmap = b;

        mTryCount = sMaxTryCound;
        
        UnLoad();

        mTextureOriginW = b.getWidth();
        mTextureOriginH = b.getHeight();

        int new_w = mTextureOriginW;
        int new_h = mTextureOriginH;

        if (!GLHelper.checkIfContextSupportsNPOT()) {
            new_w = (int) GLHelper.cellPowerOf2(mTextureOriginW);
            new_h = (int) GLHelper.cellPowerOf2(mTextureOriginH);
        }

        mRealSize[0] = new_w;
        mRealSize[1] = new_h;

        // ********* Note: ************
        // MASS算法只针对设置的颜色格式，对透明图暂时边框减1来处理
        float map_w = (mTextureOriginW - (b.hasAlpha() ? 1 : 0)) / (float) new_w;
        float map_h = (mTextureOriginH - (b.hasAlpha() ? 1 : 0)) / (float) new_h;
        float map_x = (1 - map_w) / 2;
        float map_y = (1 - map_h) / 2;

        mTextRectF.set(map_x, map_y, map_x + map_w, map_y + map_h);

        if (new_w != mTextureOriginW || new_h != mTextureOriginH) {
            Bitmap resizedBitmap = resizeBitmap(b, new_w, new_h);

            mTexture = GLHelper.loadTexture(resizedBitmap);

            resizedBitmap.recycle();
        } else {
            mTexture = GLHelper.loadTexture(b);
        }

        GLHelper.checkGLError();
        return GLHelper.isTexture(mTexture);
    }

    public boolean Init(int w, int h) {
        if (mTextureOriginW == w && mTextureOriginH == h && isValid())
            return true;

        mType = TextureType.EMPTY_RECT;

        UnLoad();

        mTextureOriginW = w;
        mTextureOriginH = h;

        int new_w = mTextureOriginW;
        int new_h = mTextureOriginH;

        if (!GLHelper.checkIfContextSupportsNPOT()) {
            new_w = (int) GLHelper.cellPowerOf2(mTextureOriginW);
            new_h = (int) GLHelper.cellPowerOf2(mTextureOriginH);
        }

        mRealSize[0] = new_w;
        mRealSize[1] = new_h;

        float map_w = w / (float) new_w;
        float map_h = h / (float) new_h;
        float map_x = (1 - map_w) / 2;
        float map_y = (1 - map_h) / 2;

        mTextRectF.set(map_x, map_y, map_x + map_w, map_y + map_h);

        mTexture = GLHelper.createTargetTexture(new_w, new_h);
        return GLHelper.isTexture(mTexture);
    }

    public boolean isValid() {
        if (mType == TextureType.BITMAP && mBitmap == null || mTextureOriginW == 0
                || mTextureOriginH == 0) {
            return false;
        }
        return GLHelper.isTexture(mTexture);
    }

    public void UnLoad() {
        GLHelper.checkEGLContextOK();

        if (mTexture != 0 && GLHelper.isTexture(mTexture)) {
            GLHelper.deleteTargetTexture(mTexture);
            mTexture = 0;
        }
    }

    public RectF getTextRect() {
        return mTextRectF;
    }

    public int[] getTextSize() {
        return new int[] {
                mTextureOriginW, mTextureOriginH
        };
    }

    public int[] getRealSize() {
        return mRealSize;
    }

    public int getTexture() {
        return mTexture;
    }

    public boolean ReloadIfNeed() {
     
        if (!isValid() && mTryCount -- > 0) {
            if (mType == TextureType.BITMAP && mBitmap != null) {
                Init(mBitmap);
            } else {
                Init(mTextureOriginW, mTextureOriginH);
            }
            
            Log.e("Texture", "try load - error:" + mTryCount + " | " + isValid());
        }
        
        return isValid();
    }

    public boolean bind() {

        if (!ReloadIfNeed()) {
            return false;
        }

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
        
        GLHelper.checkGLError();
        return true;
    }
    
    public void unBind() {
        if (isValid())
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        
        GLHelper.checkGLError();
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

    private Bitmap textToString(String text, float max_width, float text_size) {
        Bitmap bitmap = null;
        try {
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(Color.WHITE);
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(text_size);// 设置字体大小

            String new_text = text;

            if (max_width > 0) {
                new_text = TextUtils.ellipsize(text, textPaint, max_width,
                        TextUtils.TruncateAt.END).toString();
            }
            new_text = text;
            Rect text_rect = new Rect();

            textPaint.getTextBounds(new_text, 0, new_text.length(), text_rect);
            if (text_rect.isEmpty())
                return null;

            bitmap = Bitmap.createBitmap(text_rect.width(), text_rect.height(),
                    Config.ARGB_8888);

            Canvas c = new Canvas(bitmap);
            c.drawText(new_text, -text_rect.left, -text_rect.top, textPaint);
        } catch (Exception e) {
        } catch (Error e) {
        }
        return bitmap;

    }
}
