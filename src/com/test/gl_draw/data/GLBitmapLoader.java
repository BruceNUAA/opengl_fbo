
package com.test.gl_draw.data;

import java.io.InputStream;
import java.nio.ByteBuffer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import com.example.gl_fbo.R;
import com.test.gl_draw.gl_base.GLRender;

import de.matthiasmann.twl.utils.PNGDecoder;

public class GLBitmapLoader {

    //
    // Tab缩略图的padding
    //

    private static float sXHDPI_DENSITY = 2;

    private static float[] sShadowBorder = {
            28, 18, 30, 39
    };

    private static float[] sShadowStratchPos = {
            56, 57, 50, 51
    };

    private static GLBitmapLoader sTabThumbManager;

    private Resources mResources;
    private Context mContext;
    private Bitmap mClose;
    private Bitmap mShadow;
    private Bitmap mMultiTabBottomShadow;
    private Bitmap mNormalModeIcon;
    private Bitmap mIncognitoModeIcon;
    private Bitmap mAddressBar;
    private Bitmap mToolBar;

    private Bitmap mIncognitoModeTipIcon;

    private Bitmap mNewTabIcon;

    public static GLBitmapLoader getInstance() {
        if (sTabThumbManager == null) {
            sTabThumbManager = new GLBitmapLoader();
        }
        return sTabThumbManager;
    }

    public void startup(Context cx) {
        mContext = cx;
        mResources = mContext.getResources();

        init();
    }

    public void cleanup() {
        mClose = null;
        mShadow = null;
        mMultiTabBottomShadow = null;
        mNormalModeIcon = null;
        mIncognitoModeIcon = null;
        mAddressBar = null;
        mToolBar = null;
    }

    public Bitmap getNormalModeIcon() {
        return mNormalModeIcon;
    }

    public Bitmap getIncognitoModeTipIcon() {
        return mIncognitoModeTipIcon;
    }

    public Bitmap getIncognitoModeIcon() {
        return mIncognitoModeIcon;
    }

    public Bitmap getMultiTabBottomShadow() {
        return mMultiTabBottomShadow;
    }

    public Bitmap getClose() {
        return mClose;
    }

    public Bitmap getShadow() {
        return mShadow;
    }

    public Bitmap getAddressBar() {
        GLRender.CheckOnGLThread();

        return mAddressBar;
    }

    public Bitmap getToolBar() {
        GLRender.CheckOnGLThread();

        return mToolBar;
    }

    public Bitmap getNewTabIcon() {
        return mNewTabIcon;
    }

    public float[] getShadowBorder() {
        float[] v = sShadowBorder.clone();

        float scale = mResources.getDisplayMetrics().density / sXHDPI_DENSITY;
        for (int i = 0; i < v.length; i++) {
            v[i] *= scale;
        }
        return v;
    }

    public float[] getShadowStratchPos() {
        float[] v = sShadowStratchPos.clone();

        float scale = mResources.getDisplayMetrics().density / sXHDPI_DENSITY;

        for (int i = 0; i < v.length; i++) {
            v[i] *= scale;
        }
        return v;
    }

    public void UpdateAddressBarAndToolbarThumb() {
        mAddressBar = null;//getViewBitmap(mMainController.getAddressBar());
        mToolBar = null;//getViewBitmap(mMainController.getToolBar());
    }

    //
    public Bitmap ResizeBitmap(Bitmap b, int new_w, int new_h) {
        Bitmap resized_b = null;

        if (new_w < b.getWidth() || new_h < b.getHeight()) {
            throw new RuntimeException("resizeBitmap error!");
        }

        try {
            // ******************* Note: ****************
            // 在低端机上，类似RGB_565等的非整个字节的像素格式，转纹理时会非常耗时。。。
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

    public Bitmap singleLineTextToBitmap(String text, int color, float max_width, float text_size) {
        Bitmap bitmap = null;
        try {
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(color);
            textPaint.setAntiAlias(true);
            textPaint.setSubpixelText(true);
            textPaint.setTextSize(text_size);// 设置字体大小
            // CalligraphyUtils.applyFontToPaint(mContext, textPaint);

            String new_text = text;

            if (max_width > 0) {
                new_text = TextUtils.ellipsize(text, textPaint, max_width,
                        TextUtils.TruncateAt.END).toString();
            }
            
            Rect text_rect = new Rect();

            textPaint.getTextBounds(new_text, 0, new_text.length(), text_rect);
            if (text_rect.isEmpty())
                return null;

            bitmap = Bitmap.createBitmap(text_rect.width(), text_rect.height(),
                    Config.ARGB_4444);

            Canvas c = new Canvas(bitmap);
            c.drawText(new_text, -text_rect.left, -text_rect.top, textPaint);
        } catch (Exception e) {
        } catch (Error e) {
        }
        return bitmap;

    }

    public Bitmap MultiLineTextToBitmap(String text, int color, float max_width, float text_size) {
        Bitmap bitmap = null;
        try {
            TextPaint textPaint = new TextPaint();
            textPaint.setColor(color);
            textPaint.setAntiAlias(true);
            textPaint.setSubpixelText(true);
            textPaint.setTextSize(text_size);// 设置字体大小
            // CalligraphyUtils.applyFontToPaint(mContext, textPaint);
            
            if (max_width == 0) {
                max_width = textPaint.measureText(text);
            }

            StaticLayout layout = new
                    StaticLayout(text, textPaint, (int) max_width, Alignment.ALIGN_CENTER, 1.0F,
                            0.0F, true);

            bitmap = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(),
                    Config.ARGB_4444);
            Canvas c = new Canvas(bitmap);
            layout.draw(c);

        } catch (Exception e) {
        } catch (Error e) {
        }

        return bitmap;
    }

    private Bitmap getViewBitmap(View v) {
        if (v == null || v.getWidth() == 0 || v.getHeight() == 0)
            return null;

        Bitmap cache = null;
        try {
            cache = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Config.ARGB_8888);
            Canvas c = new Canvas(cache);
            v.draw(c);

            cache = Bitmap.createScaledBitmap(cache, v.getWidth() / 2, v.getHeight() / 2, false);
        } catch (Exception e) {
        } catch (Error e) {
        } finally {
        }

        return cache;
    }

    private void init() {
        mNormalModeIcon = BitmapFactory.decodeResource(mResources,
                R.drawable.multi_tabs_inormal_mode);

        mIncognitoModeIcon = BitmapFactory.decodeResource(mResources,
                R.drawable.multi_tabs_incognito_mode);
        mMultiTabBottomShadow = BitmapFactory.decodeResource(
                mResources, R.drawable.mitl_tab_bottom_shadow);
        
        mShadow = BitmapFactory.decodeResource(mResources,
                R.drawable.muti_tab_shadow);
        mIncognitoModeTipIcon = BitmapFactory.decodeResource(mResources,
                R.drawable.incognito_mode_icon);

        if (false) {
        	mClose = BitmapFactory.decodeResource(mResources, R.drawable.muti_tab_close);
        	mNewTabIcon = BitmapFactory.decodeResource(mResources, R.drawable.kui_toolbar_new_tab);
        } else {
        	mClose = loadBitmap(R.drawable.muti_tab_close);
        	mNewTabIcon = loadBitmap(R.drawable.kui_toolbar_new_tab);
        } 
    }

    private Bitmap loadBitmap(int resid) {
       
        Bitmap bitmap = null;
        ByteBuffer buf = null;
        bitmap = BitmapFactory.decodeResource(mResources, resid);
        try {
            InputStream in = mResources.openRawResource(resid);
            
            PNGDecoder decoder = new PNGDecoder(in);
            
        
            int w = decoder.getWidth();
            int h = decoder.getHeight();
            Bitmap.Config config = Bitmap.Config.ARGB_8888;
            
            buf = ByteBuffer.allocateDirect(w * h * 4);
            
            decoder.decode(buf, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
            buf.flip();
            
            bitmap = Bitmap.createBitmap( w, h, config);
            bitmap.copyPixelsFromBuffer(buf);
            
            float scale = mResources.getDisplayMetrics().density / sXHDPI_DENSITY;
            if (scale != 1) {
              //  bitmap = Bitmap.createScaledBitmap(bitmap, (int)(w * scale), (int)(h * scale), true);
            }
            in.close();
        } catch (Exception e) {
            bitmap = null;
        } finally {
        }

        return bitmap;
    }
}
