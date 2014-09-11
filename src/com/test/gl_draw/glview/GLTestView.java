
package com.test.gl_draw.glview;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.TabThumbManager;
import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.igl_draw.IGLView;
import com.test.gl_draw.utils.BufferUtil;

//           stratch pos
//           pos[0] pos[1]
//              ↓   ↓
//    0---------1---4-----------5
//    ---------------------------
//    ---------------------------
//    ---------------------------
//  → 3----------2---7----------6 ← pos[2]
//    ---------------------------
//  → 14---------15--11---------8 ← pos[3]
//    ---------------------------
//    ---------------------------
//    13---------12--10---------9
//               ↑   ↑
//            stratch pos
public class GLTestView extends GLView {
    Texture mTexture = null;

    FloatBuffer mTXCoordBuffer = BufferUtil.newFloatBuffer(16 * 2);
    FloatBuffer mVBuffer = BufferUtil.newFloatBuffer(16 * 2);

    ByteBuffer mIdexBuffer = BufferUtil.newByteBuffer(9 * 4);

    @Override
    public void onParentLayoutChange(IGLView parent, RectF old_r, RectF new_r) {
        SetBounds(new_r);
    }

    @Override
    public void OnDraw(GL10 gl) {
        setData(Bounds());

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture.getTexture());
        gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTXCoordBuffer);

        gl.glDrawElements(GL10.GL_TRIANGLE_FAN, mIdexBuffer.capacity(), GL10.GL_UNSIGNED_BYTE,
                mIdexBuffer);
        //
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
    }

    @Override
    public void SetBounds(RectF rc) {
        if (!Bounds().equals(rc)) {
            setData(rc);
        }

        super.SetBounds(rc);
    }

    private void loadTexture() {
        if (mTexture != null && mTexture.isValid())
            return;

        mTexture = TabThumbManager.getInstance().getShadowTexture();
        int[] size = mTexture.getRealSize();
        RectF rect = mTexture.getTextRect();
        float[] pos = TabThumbManager.sShadowStratchPos.clone();

        pos[0] = pos[0] / size[0];
        pos[1] = pos[1] / size[1];
        pos[2] = pos[2] / size[0];
        pos[3] = pos[3] / size[1];
        float[][] txBuffer = new float[][] {
                // 0, 1, 2, 3
                {
                        rect.left, rect.top,
                }, {
                        pos[0], rect.top
                }, {
                        pos[0], pos[2]
                }, {
                        rect.left, pos[2]
                },
                // 4, 5, 6, 7
                {
                        rect.right - pos[1], rect.top,
                }, {
                        rect.right, rect.top
                }, {
                        rect.right, pos[2]
                }, {
                        rect.right - pos[1], pos[2]
                },
                // 8, 9, 10, 11
                {
                        rect.right, rect.bottom - pos[3],
                }, {
                        rect.right, rect.bottom
                }, {
                        rect.right - pos[1], rect.bottom
                }, {
                        rect.right - pos[1], rect.bottom - pos[3]
                },
                // 12, 13, 14, 15
                {
                        pos[0], rect.bottom,
                }, {
                        rect.left, rect.bottom
                }, {
                        rect.left, rect.bottom - pos[3]
                }, {
                        pos[0], rect.bottom - pos[3]
                },//
        };

        for (int i = 0; i < txBuffer.length; i++) {
            for (int j = 0; j < txBuffer[i].length; j++) {
                mTXCoordBuffer.put(i * txBuffer[i].length + j, txBuffer[i][j]);
            }
        }

        int[][] index = new int[][] {
                {
                        0, 1, 2, 3
                }, {
                        1, 4, 7, 2
                }, {
                        4, 5, 6, 7
                },
                {
                        3, 2, 15, 14
                }, {
                        2, 7, 11, 15
                }, {
                        7, 6, 8, 11
                },
                {
                        14, 15, 12, 13
                }, {
                        15, 11, 10, 12
                }, {
                        11, 8, 9, 10
                },
        };
        if (false) {

            index = new int[][] {
                    {
                        11, 8, 9, 10
                },
            };
        }

        mIdexBuffer = BufferUtil.newByteBuffer(index.length * index[0].length);

        for (int i = 0; i < index.length; i++) {
            for (int j = 0; j < index[i].length; j++) {
                mIdexBuffer.put(i * index[i].length + j, (byte) index[i][j]);
            }
        }
    }

    private void setData(RectF rect) {
        loadTexture();

        float[] pos = TabThumbManager.sShadowStratchPos.clone();

        float[][] vBuffer = new float[][] {
                // 0, 1, 2, 3
                {
                        rect.left, rect.top,
                }, {
                        pos[0], rect.top
                }, {
                        pos[0], pos[2]
                }, {
                        rect.left, pos[2],
                },
                // 4, 5, 6, 7
                {
                        pos[1], rect.top,
                }, {
                        rect.right, rect.top
                }, {
                        rect.right, pos[2]
                }, {
                        pos[1], pos[2],
                },
                // 8, 9, 10, 11
                {
                        rect.right, pos[3],
                }, {
                        rect.right, rect.bottom
                }, {
                        pos[1], rect.bottom
                }, {
                        pos[1], pos[3],
                },
                // 12, 13, 14, 15
                {
                        pos[0], rect.bottom,
                }, {
                        rect.left, rect.bottom
                }, {
                        rect.left, pos[3]
                }, {
                        pos[0], pos[3],
                },//
        };

        for (int i = 0; i < vBuffer.length; i++) {
            for (int j = 0; j < vBuffer[i].length; j++) {
                mVBuffer.put(i * vBuffer[i].length + j, vBuffer[i][j]);
            }
        }

    }
}
