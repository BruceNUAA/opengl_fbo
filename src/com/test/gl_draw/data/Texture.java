
package com.test.gl_draw.data;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.RectF;
import android.opengl.GLES20;

import com.test.gl_draw.gl_base.GLConfigure;
import com.test.gl_draw.utils.GLHelper;
import com.test.gl_draw.utils.GLHelper20;

public class Texture extends GLResource {

    private enum TextureType {
        BITMAP,
        EMPTY_RECT,
        String,
        None,
    }

    private TextureType mType = TextureType.None;

    private Bitmap mBitmap;

    private String mStringTxt = null;

    private RectF mTextRectF = new RectF();
    private int mTexture = 0;

    private int mTextureOriginW = 0;
    private int mTextureOriginH = 0;

    private int[] mRealSize = {
            0, 0
    };

    public Texture() {
    }

    public Texture(Bitmap bitmap) {
        Init(bitmap);
    }

    @Override
    public String getDetailLogMessage() {
        StringBuilder b = new StringBuilder();
        b.append("Type:");
        b.append(mType.toString());
        b.append("<");
        switch (mType) {
            case BITMAP:
            case EMPTY_RECT:
                b.append(mTextureOriginW);
                b.append(",");
                b.append(mTextureOriginH);
                break;
            case String:
                b.append(mStringTxt);
                break;
            default:
                break;
        }
        b.append(">");
        
        return b.toString();
    }
    
    @Override
    public void unload() {
        if (isValid()) {
            GLHelper20.deleteTargetTexture(mTexture);
            mTexture = 0;

            didUnload();
        }
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

    public boolean Init(String text, int color, float text_size, float max_width,
            boolean is_multi_line) {

        if (text == null || text.isEmpty() || text.equals(mStringTxt))
            return false;

        BeforeThreadCall();

        mStringTxt = new String(text);

        Bitmap bitmap = null;

        if (is_multi_line) {
            bitmap = GLBitmapLoader.getInstance().MultiLineTextToBitmap(mStringTxt, color,
                    max_width, text_size);
        } else {
            bitmap = GLBitmapLoader.getInstance().singleLineTextToBitmap(mStringTxt, color,
                    max_width, text_size);
        }

        if (bitmap == null)
            return false;
        
        mType = TextureType.String;

        AfterThreadCall();
        return Init(bitmap);
    }

    public boolean Init(Bitmap b) {

        if (b == null) {
            return false;
        }

        if (GLConfigure.getInstance().enableGLViewErrorCheck()) {
            if (b.getConfig() == Config.RGB_565)
                throw new RuntimeException("在有的手机上，该配置的图片转纹理会很耗时！");
        }

        if (isValid() && b.sameAs(mBitmap))
            return true;

        BeforeThreadCall();

        if (mType == TextureType.None)
            mType = TextureType.BITMAP;

        Destory(!b.sameAs(mBitmap), true);

        mBitmap = b;

        mTextureOriginW = b.getWidth();
        mTextureOriginH = b.getHeight();

        int new_w = mTextureOriginW;
        int new_h = mTextureOriginH;

        if (!GLConfigure.getInstance().isSupportNPOT()) {
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

        if (mRealSize[0] != mTextureOriginW || mRealSize[1] != mTextureOriginH) {
            mBitmap = GLBitmapLoader.getInstance().ResizeBitmap(mBitmap,
                    mRealSize[0], mRealSize[1]);
        }
        
        AfterThreadCall();
        return true;
    }

    public boolean Init(int w, int h) {
        if (w == 0 || h == 0)
            return false;

        if (mTextureOriginW == w && mTextureOriginH == h && isValid())
            return true;

        BeforeThreadCall();

        mType = TextureType.EMPTY_RECT;

        Destory(false, true);

        mTextureOriginW = w;
        mTextureOriginH = h;

        int new_w = mTextureOriginW;
        int new_h = mTextureOriginH;

        if (!GLConfigure.getInstance().isSupportNPOT()) {
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
        
        AfterThreadCall();
        return true;
    }

    private boolean isValid() {
        return GLHelper20.isTexture(mTexture);
    }
    
    private void Destory(boolean recyle_bitmap, boolean force_unload) {
    	  if (GLResourceManager.getInstance().enableUnloadWhenInvaislbe() || force_unload)
              unload();

          if (recyle_bitmap && mBitmap != null && !mBitmap.isRecycled()) {
              mBitmap.recycle();
              mBitmap = null;
          }
    }
    
    public void Destory(boolean recyle_bitmap) {
    	Destory(recyle_bitmap, false);
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

    public int getValidTexture(GL10 gl) {
        if (!isValid()) {
            ReloadIfNeed(gl);
        }

        return mTexture;
    }

    public boolean ReloadIfNeed(GL10 gl) {
   
        if (!isValid()) {
            long t = System.nanoTime();
            
            if ((mType == TextureType.BITMAP || mType == TextureType.String) && mBitmap != null) {
                if (mRealSize[0] != mTextureOriginW || mRealSize[1] != mTextureOriginH) {
                    Bitmap resizedBitmap = GLBitmapLoader.getInstance().ResizeBitmap(mBitmap,
                            mRealSize[0], mRealSize[1]);

                    mTexture = GLHelper20.loadTexture(resizedBitmap);

                    resizedBitmap.recycle();
                } else {
                    mTexture = GLHelper20.loadTexture(mBitmap);
                }
            } else if (mType == TextureType.EMPTY_RECT) {
                mTexture = GLHelper20.createTargetTexture(mRealSize[0], mRealSize[1]);
            }

            if (isValid()) {
                didLoad(System.nanoTime() - t);
            }
        }
        GLHelper20.checkGLError();
        return isValid();
    }

    public boolean bind(GL10 gl) {

        BeforeThreadCall();

        if (ReloadIfNeed(gl)) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexture);
        }

        AfterThreadCall();

        return true;
    }

    public void unBind(GL10 gl) {
    	BeforeThreadCall();

        if (isValid())
        	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        
        AfterThreadCall();
    }

 
}
