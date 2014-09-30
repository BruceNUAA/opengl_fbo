package com.test.gl_draw.glview;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.data.Texture;
import com.test.gl_draw.gl_base.GLObject;
import com.test.gl_draw.gl_base.GLRender;
import com.test.gl_draw.utils.helper.BufferUtil;

//           stretch pos
//           pos[0] pos[1]
//              ↓   ↓
//    0---------1-----4-----------5
//    ----------------------------
//    -----A-------B-------C------
//    ----------------------------
//  → 3---------2------7----------6 ← pos[2]
//    ----------------------------
//    -----D-------E-------F------
//    ----------------------------
//  → 14--------15----11---------8 ← pos[3]
//    ----------------------------
//    -----G-------H-------I------
//    ----------------------------
//    13--------12----10---------9
//               ↑   ↑
//            stratch pos
public class NinePatchDraw extends GLObject {
	private Texture mTexture;

	private FloatBuffer mTXCoordBuffer;
	private FloatBuffer mVBuffer;
	private FloatBuffer mColorBuffer;

	private ByteBuffer mIdexBuffer;

	private boolean mShowBorderInside = false;

	private float[] mStretchPos;
	private float[] mBorder;

	private RectF mRect = new RectF();

	private float mCornerRScale = 1;
	
	private float mAlpha = 1;
	
	private boolean mVisible = true;
	
    private boolean mRecyleBitmapWhenDetach = true;

	public NinePatchDraw() {
		int[][] index = new int[][] {
				//
				{ 0, 1, 2 }, { 0, 2, 3 },// A
				{ 1, 4, 7 }, { 1, 7, 2 },// B
				{ 4, 5, 6 }, { 4, 6, 7 },// C
				{ 3, 2, 15 }, { 3, 15, 14 },// D
				{ 2, 7, 11 }, { 2, 11, 15 }, // E
				{ 7, 6, 8 }, { 7, 8, 11 },// F
				{ 14, 15, 12 }, { 14, 12, 13 },// G
				{ 15, 11, 10 }, { 15, 10, 12 },// H
				{ 11, 8, 9 }, { 11, 9, 10 }, // I
		};

		mIdexBuffer = BufferUtil.newByteBuffer(index.length * index[0].length);

		for (int i = 0; i < index.length; i++) {
			for (int j = 0; j < index[i].length; j++) {
				mIdexBuffer.put(i * index[i].length + j, (byte) index[i][j]);
			}
		}
	}

	public void ShowBorderInside(boolean inside) {
		mShowBorderInside = inside;

		UpdateRect();
	}

	public void setAlpha(float alpha) {
	    alpha = Math.max(0, Math.min(1, alpha));
	    
	    if (mAlpha == alpha && mColorBuffer != null)
	        return;
	    
	    mAlpha = alpha;

		float[][] colors = new float[][] {
				// 0, 1, 2, 3
				{ 1, 1, 1, mAlpha },
				{ 1, 1, 1, mAlpha },
				{ 1, 1, 1, mAlpha },
				{ 1, 1, 1, mAlpha },
				// 4, 5, 6, 7
				{ 1, 1, 1, mAlpha }, { 1, 1, 1, mAlpha },
				{ 1, 1, 1, mAlpha },
				{ 1, 1, 1, mAlpha },
				// 8, 9, 10, 11
				{ 1, 1, 1, mAlpha }, { 1, 1, 1, mAlpha }, { 1, 1, 1, mAlpha },
				{ 1, 1, 1, mAlpha },
				// 12, 13, 14, 15
				{ 1, 1, 1, mAlpha }, { 1, 1, 1, mAlpha }, { 1, 1, 1, mAlpha },
				{ 1, 1, 1, mAlpha }, };

		mColorBuffer = BufferUtil.newFloatBuffer(colors.length
				* colors[0].length);

		for (int i = 0; i < colors.length; i++) {
			for (int j = 0; j < colors[i].length; j++) {
				mColorBuffer.put(i * colors[i].length + j, colors[i][j]);
			}
		}

	}

	public void setTexture(Texture texture, float[] stretchPos, float[] border, boolean recyle_bitmap_when_detach) {
		if (texture == null || stretchPos.length < 4)
			return;
		
		mRecyleBitmapWhenDetach = recyle_bitmap_when_detach;

		if (border == null || border.length < 4) {
			border = new float[4];
		}

		mStretchPos = stretchPos.clone();
		mBorder = border.clone();
		
		if(mTexture != null)
		    mTexture.Destory(true);
		
		mTexture = texture;

		UpdateTexture();
	}

	public void setStretchPos(float[] stretchPos) {
		if (stretchPos.length < 4)
			return;

		mStretchPos = stretchPos.clone();
		UpdateTexture();
	}

	public void setRect(RectF rect) {
		if (rect == null || rect.isEmpty())
			return;
		mRect.set(rect);

		UpdateRect();
	}
	
    public void setVisible(boolean visible) {
        if (mVisible == visible)
            return;
        
        mVisible = visible;
        if (mTexture == null)
            return;
        
        if (!mVisible) {
            mTexture.Destory(false);
        } else {
            mTexture.ReloadIfNeed(GLRender.GL());
        }
    }

	public void Draw(GL10 gl) {

		if (mTXCoordBuffer == null || mVBuffer == null || mIdexBuffer == null
				|| mTexture == null || mAlpha == 0 || mVisible == false)
			return;

		if (!mTexture.bind(gl))
			return;

		BeforeThreadCall();
		
		boolean has_color = mColorBuffer != null;

		if (has_color) {
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
		} else {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTXCoordBuffer);

		gl.glDrawElements(GL10.GL_TRIANGLES, mIdexBuffer.capacity(),
				GL10.GL_UNSIGNED_BYTE, mIdexBuffer);
		//
		if (!has_color) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		}

		mTexture.unBind(gl);

		AfterThreadCall();
	}

	public void setCornerRate(float scale) {
		if (mCornerRScale == scale)
			return;

		mCornerRScale = scale;
		UpdateRect();
	}
	
    public void UnloadTexture() {
        if (mTexture != null) {
            mTexture.Destory(false);
        }
    }

    public void DetachFromView() {
        if (mTexture != null) {
            mTexture.Destory(mRecyleBitmapWhenDetach);
            mTexture = null;
        }

        if (mVBuffer != null) {
            mVBuffer.clear();
            mVBuffer = null;
        }

        if (mTXCoordBuffer != null) {
            mTXCoordBuffer.clear();
            mTXCoordBuffer = null;
        }

        if (mColorBuffer != null) {
            mColorBuffer.clear();
            mColorBuffer = null;
        }

        mRect.setEmpty();
    }

	private void UpdateTexture() {
		if (mTexture == null || mStretchPos == null)
			return;

		int[] size = mTexture.getRealSize();
		RectF rect = mTexture.getTextRect();
		float[] pos = mStretchPos.clone();

		pos[0] = pos[0] / size[0];
		pos[1] = pos[1] / size[1];
		pos[2] = pos[2] / size[0];
		pos[3] = pos[3] / size[1];
		float[][] txBuffer = new float[][] {
				// 0, 1, 2, 3
				{ rect.left, rect.top, },
				{ rect.left + pos[0], rect.top },
				{ rect.left + pos[0], rect.top + pos[2] },
				{ rect.left, rect.top + pos[2] },
				// 4, 5, 6, 7
				{ rect.right - pos[1], rect.top, },
				{ rect.right, rect.top },
				{ rect.right, rect.top + pos[2] },
				{ rect.right - pos[1], rect.top + pos[2] },
				// 8, 9, 10, 11
				{ rect.right, rect.bottom - pos[3] },
				{ rect.right, rect.bottom },
				{ rect.right - pos[1], rect.bottom },
				{ rect.right - pos[1], rect.bottom - pos[3] },
				// 12, 13, 14, 15
				{ rect.left + pos[0], rect.bottom, },
				{ rect.left, rect.bottom },
				{ rect.left, rect.bottom - pos[3] },
				{ rect.left + pos[0], rect.bottom - pos[3] },//
		};

		mTXCoordBuffer = BufferUtil.newFloatBuffer(txBuffer.length
				* txBuffer[0].length);

		for (int i = 0; i < txBuffer.length; i++) {
			for (int j = 0; j < txBuffer[i].length; j++) {
				mTXCoordBuffer.put(i * txBuffer[i].length + j, txBuffer[i][j]);
			}
		}
	}

	private void UpdateRect() {
		if (mStretchPos == null || mBorder == null || mRect == null
				|| mRect.isEmpty())
			return;

		float[] pos = mStretchPos.clone();

		for (int i = 0; i < mStretchPos.length; i++) {
			pos[i] *= mCornerRScale;
		}

		float[] border = new float[4];

		if (!mShowBorderInside) {
			border = mBorder.clone();
		}

		float[][] vBuffer = new float[][] {
				// 0, 1, 2, 3
				{ mRect.left - border[0], mRect.top - border[1] },
				{ mRect.left + pos[0] - border[0], mRect.top - border[1] },
				{ mRect.left + pos[0] - border[0],
						mRect.top + pos[2] - border[1] },
				{ mRect.left - border[0], mRect.top + pos[2] - border[1] },
				// 4, 5, 6, 7
				{ mRect.right - pos[1] + border[2], mRect.top - border[1] },
				{ mRect.right + border[2], mRect.top - border[1] },
				{ mRect.right + border[2], mRect.top + pos[2] - border[1] },
				{ mRect.right - pos[1] + border[2],
						mRect.top + pos[2] - border[1] },
				// 8, 9, 10, 11
				{ mRect.right + border[2], mRect.bottom - pos[3] + border[3] },
				{ mRect.right + border[2], mRect.bottom + border[3] },
				{ mRect.right - pos[1] + border[2], mRect.bottom + border[3] },
				{ mRect.right - pos[1] + border[2],
						mRect.bottom - pos[3] + border[3] },
				// 12, 13, 14, 15
				{ mRect.left + pos[0] - border[0], mRect.bottom + border[3] },
				{ mRect.left - border[0], mRect.bottom + border[3] },
				{ mRect.left - border[0], mRect.bottom - pos[3] + border[3] },
				{ mRect.left + pos[0] - border[0],
						mRect.bottom - pos[3] + border[3] },//
		};

		mVBuffer = BufferUtil
				.newFloatBuffer(vBuffer.length * vBuffer[0].length);

		for (int i = 0; i < vBuffer.length; i++) {
			for (int j = 0; j < vBuffer[i].length; j++) {
				mVBuffer.put(i * vBuffer[i].length + j, vBuffer[i][j]);
			}
		}

	}
}
