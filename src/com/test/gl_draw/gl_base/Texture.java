
package com.test.gl_draw.gl_base;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.test.gl_draw.utils.GLHelper;
import com.test.gl_draw.utils.NonThreadSafe;

public class Texture extends NonThreadSafe {

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
        CheckThread();

        if (this == texture)
            return;

        Destory(mBitmap != null && !mBitmap.sameAs(texture.mBitmap));

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
    
    public boolean Init(String text, int color, float text_size) {
        return Init(text, color, text_size, 0, false);
    }

    public boolean Init(String text, int color, float text_size, boolean is_multi_line) {
        return Init(text, color, text_size, 0, is_multi_line);
    }
    
    public boolean Init(String text, int color, float text_size, float max_width) {
        return Init(text, color, text_size, max_width, false);
    }

    public boolean Init(String text, int color, float text_size, float max_width, boolean is_multi_line) {
        CheckThread();

        if (text == null || text.isEmpty() || text.equals(mStringTxt))
            return false;

        mStringTxt = new String(text);
        
        Bitmap bitmap = null;
        
        if (is_multi_line) {
            bitmap = multiLineTextToBitmap(mStringTxt, color, max_width, text_size);
        } else {
            bitmap = singleLineTextToString(mStringTxt, color, max_width, text_size);
        }

        if (bitmap == null)
            return false;

        return Init(bitmap);
    }

    public boolean Init(Bitmap b) {
        CheckThread();

        GL10 gl = GLRender.GL();
        if (b == null) {
            return false;
        }

        if (isValid() && b.sameAs(mBitmap))
            return true;

        mType = TextureType.BITMAP;

        Destory(!b.sameAs(mBitmap));

        mBitmap = b;

        mTryCount = sMaxTryCound;

        mTextureOriginW = b.getWidth();
        mTextureOriginH = b.getHeight();

        int new_w = mTextureOriginW;
        int new_h = mTextureOriginH;

        if (!GLConfigure.getInstance().isSupportNPOT(gl)) {
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

            mTexture = GLHelper.loadTexture(gl, resizedBitmap);
            
            resizedBitmap.recycle();
        } else {
            mTexture = GLHelper.loadTexture(gl, b);
        }

        CheckThreadError(gl);
        return GLHelper.isTexture(gl, mTexture);
    }

    public boolean Init(int w, int h) {
        if (w == 0 || h == 0)
            return false;
        
        CheckThread();

        GL10 gl = GLRender.GL();
        if (mTextureOriginW == w && mTextureOriginH == h && isValid())
            return true;

        mType = TextureType.EMPTY_RECT;

        UnLoad();

        mTextureOriginW = w;
        mTextureOriginH = h;

        int new_w = mTextureOriginW;
        int new_h = mTextureOriginH;

        if (!GLConfigure.getInstance().isSupportNPOT(gl)) {
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

        mTexture = GLHelper.createTargetTexture(gl, new_w, new_h);
        return GLHelper.isTexture(gl, mTexture);
    }

    public boolean isValid() {
        if (mType == TextureType.BITMAP && mBitmap == null || mTextureOriginW == 0
                || mTextureOriginH == 0) {
            return false;
        }
        return GLHelper.isTexture(GLRender.GL(), mTexture);
    }

    public void UnLoad() {

        if (mTexture != 0 && GLHelper.isTexture(GLRender.GL(), mTexture)) {
            GLHelper.deleteTargetTexture(GLRender.GL(), mTexture);
            mTexture = 0;
        }
    }

    public void Destory(boolean recyle_bitmap) {
        UnLoad();

        if (recyle_bitmap && mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    public void Destory() {
        Destory(true);
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

        if (!isValid() && mTryCount-- > 0) {
            if (mType == TextureType.BITMAP && mBitmap != null) {
                Init(mBitmap);
            } else if (mType == TextureType.EMPTY_RECT) {
                Init(mTextureOriginW, mTextureOriginH);
            }
        }

        return isValid();
    }

    public boolean bind(GL10 gl) {

        if (!ReloadIfNeed()) {
            return false;
        }

        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture);

        CheckThreadError(gl);
        return true;
    }

    public void unBind(GL10 gl) {
        if (isValid())
            gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

        CheckThreadError(gl);
    }

    //
    private Bitmap resizeBitmap(Bitmap b, int new_w, int new_h) {
        Bitmap resized_b = null;

        if (new_w < b.getWidth() || new_h < b.getHeight()) {
            throw new RuntimeException("resizeBitmap error!");
        }

        try {
            resized_b = Bitmap.createBitmap(new_w, new_h, b.getConfig());

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

    private Bitmap singleLineTextToString(String text, int color, float max_width, float text_size) {
        Bitmap bitmap = null;
        try {
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(color);
            textPaint.setAntiAlias(true);
            textPaint.setSubpixelText(true);
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
    
    private Bitmap multiLineTextToBitmap(String text, int color, float max_width, float text_size) {
        Bitmap bitmap = null;
        try {
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(color);
            textPaint.setAntiAlias(true);
            textPaint.setSubpixelText(true);
            textPaint.setTextSize(text_size);// 设置字体大小

            if (max_width == 0) {
                max_width = textPaint.measureText(text);
            }

            StaticLayout layout = new
                    StaticLayout(text, textPaint, (int) max_width, Alignment.ALIGN_CENTER, 1.0F,
                            0.0F, true);

            bitmap = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(),
                    Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            layout.draw(c);

        } catch (Exception e) {
        } catch (Error e) {
        }
        
        return bitmap;
    }
}
