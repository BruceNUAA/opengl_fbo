package com.test.gl_draw.glview;

import java.nio.FloatBuffer;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.RectF;

import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.utils.BufferUtil;

public class GLView implements IGLView {

	public static int sID = 0;

	private RectF mBounds = new RectF();
	private OnTouchLisener mTouchLisener = null;

	private int mID = 0;

	private IGLView mParent = null;
	protected CopyOnWriteArrayList<IGLView> mChildViews = new CopyOnWriteArrayList<IGLView>();

	protected int[] mBackoundColor;
	private Texture mBackgoundTexture = null;

	protected FloatBuffer mVBuffererticleBuffer = BufferUtil
			.newFloatBuffer(4 * 2);
	protected FloatBuffer mTextureCoordBuffer = BufferUtil
			.newFloatBuffer(4 * 2);
	protected FloatBuffer mColorBuffer = BufferUtil.newFloatBuffer(4 * 4);

	public static int sRenderWidth = 0;
	public static int sRenderHeight = 0;

	public GLView() {
		mID = sID;
		sID++;
	}

	@Override
	public int id() {
		return mID;
	}

	@Override
	public void SetId(int id) {
		mID = id;
	}

	@Override
	public void InValidate() {

	}

	@Override
	public void SetBackgound(int... color) {
		if (color.length != 1 && color.length != 2 && color.length != 4)
			throw new RuntimeException("背景颜色个数设置错误！");

		if (mBackoundColor != color) {
			mBackoundColor = color;
			refreshBKData();
			InValidate();
		}
	}

	@Override
	public void SetBackgound(Texture texture) {
		if (texture.isValid()) {
			if (mBackgoundTexture == null)
				mBackgoundTexture = new Texture();

			mBackgoundTexture.Init(texture);
			InValidate();
		}
	}

	// 绘制
	@Override
	public void Draw(GL10 gl) {
		gl.glEnable(GL10.GL_SCISSOR_TEST);
		RectF r = ClipBound();

		gl.glScissor((int) r.left, sRenderHeight - (int) r.bottom,
				(int) r.width(), (int) r.height());

		OnDrawBackgound(gl);

		OnDraw(gl);

		gl.glDisable(GL10.GL_SCISSOR_TEST);

		OnDrawChilds(gl);
	}

	@Override
	public void OnDrawBackgound(GL10 gl) {

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		if (mBackoundColor != null) {
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
		} else {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}

		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVBuffererticleBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		if (mBackoundColor == null) {
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		}

	}

	@Override
	public void Detach() {
		for(IGLView v : mChildViews)
			v.Detach();
	}
	
	@Override
	public void OnDraw(GL10 gl) {

	}

	@Override
	public void OnDrawChilds(GL10 gl) {
		for (IGLView v : mChildViews) {
			gl.glPushMatrix();

			gl.glTranslatef(mBounds.left, mBounds.top, 0);

			v.Draw(gl);

			gl.glPopMatrix();
		}
	}

	//

	@Override
	public void SetBounds(RectF rc) {
		if (rc != null && !rc.equals(mBounds)) {
			mBounds.set(rc);
			refreshPosData();
			InValidate();
		}
	}

	@Override
	public RectF Bounds() {
		return mBounds;
	}

	@Override
	public RectF VisibleBoundsInRender() {
		RectF rc = new RectF(mBounds);

		IGLView parent = Parent();
		while (parent != null && !rc.isEmpty()) {
			RectF boundF = parent.Bounds();
			rc.offset(boundF.left, boundF.top);
			if (!rc.intersect(boundF))
				rc.setEmpty();
			parent = parent.Parent();
		}

		return rc;
	}
	
	@Override
	public RectF ClipBound() {
		return VisibleBoundsInRender();
	}

	//
	@Override
	public boolean HitTest(float x, float y) {
		return mBounds.contains(x, y);
	}

	@Override
	public IGLView Parent() {
		return mParent;
	}

	@Override
	public void SetParent(IGLView parent) {
		mParent = parent;
	}

	@Override
	public IGLView FindViewByPos(float x, float y) {

		IGLView view = null;

		if (HitTest(x, y)) {
			view = this;
		} else {
			x -= mBounds.left;
			y -= mBounds.top;

			for (IGLView v : mChildViews) {
				if (HitTest(x, y)) {
					view = v;
					break;
				}
			}
		}
		return view;

	}

	@Override
	public IGLView FindViewByID(int id) {
		IGLView view = null;

		if (id() == id) {
			view = this;
		} else {
			for (IGLView v : mChildViews) {

				if (v.id() == id) {
					view = v;
					break;
				}
			}
		}

		return view;
	}

	@Override
	public void AddView(IGLView view) {
		if (view == null)
			return;

		view.SetParent(this);

		if (mChildViews.contains(view))
			return;

		mChildViews.add(view);

	}

	@Override
	public void RemoveView(IGLView view) {
		if (view == null)
			return;

		view.SetParent(null);

		if (!mChildViews.contains(view))
			return;

		mChildViews.remove(view);
	}

	// touch
	@Override
	public boolean onSingleTapUp(float x, float y) {

		boolean handled = false;
		x -= mBounds.left;
		y -= mBounds.top;

		for (int i = mChildViews.size() - 1; i >= 0; i--) {
			IGLView v = mChildViews.get(i);
			if (!v.HitTest(x, y))
				continue;
			if (v.onSingleTapUp(x, y)) {
				handled = true;
				break;
			}
		}

		return handled;
	}

	@Override
	public boolean onScroll(float start_x, float start_y, float cur_x,
			float cur_y, float distanceX, float distanceY) {
		boolean handled = false;
		start_x -= mBounds.left;
		start_y -= mBounds.top;
		cur_x -= mBounds.left;
		cur_y -= mBounds.top;

		for (int i = mChildViews.size() - 1; i >= 0; i--) {
			IGLView v = mChildViews.get(i);
			if (!v.HitTest(cur_x, cur_y))
				continue;
			if (v.onScroll(start_x, start_y, cur_x, cur_y, distanceX, distanceY)) {
				handled = true;
				break;
			}
		}

		return handled;

	}

	@Override
	public boolean onLongPress(float x, float y) {
		boolean handled = false;
		x -= mBounds.left;
		y -= mBounds.top;

		for (int i = mChildViews.size() - 1; i >= 0; i--) {
			IGLView v = mChildViews.get(i);
			if (!v.HitTest(x, y))
				continue;
			if (v.onLongPress(x, y)) {
				handled = true;
				break;
			}
		}

		return handled;

	}

	@Override
	public boolean onFling(float start_x, float start_y, float cur_x,
			float cur_y, float velocityX, float velocityY) {
		boolean handled = false;
		start_x -= mBounds.left;
		start_y -= mBounds.top;
		cur_x -= mBounds.left;
		cur_y -= mBounds.top;

		for (int i = mChildViews.size() - 1; i >= 0; i--) {
			IGLView v = mChildViews.get(i);
			if (!v.HitTest(cur_x, cur_y))
				continue;
			if (v.onFling(start_x, start_y, cur_x, cur_y, velocityX, velocityY)) {
				handled = true;
				break;
			}
		}

		return handled;

	}

	@Override
	public boolean onUp(float x, float y) {
		boolean handled = false;
		x -= mBounds.left;
		y -= mBounds.top;

		for (int i = mChildViews.size() - 1; i >= 0; i--) {
			IGLView v = mChildViews.get(i);
			if (!v.HitTest(x, y))
				continue;
			if (v.onUp(x, y)) {
				handled = true;
				break;
			}
		}

		if (!handled && mTouchLisener != null) {
			handled = mTouchLisener.OnClick(this);
		}

		return handled;
	}

	@Override
	public boolean onDown(float x, float y) {
		boolean handled = false;
		x -= mBounds.left;
		y -= mBounds.top;

		for (int i = mChildViews.size() - 1; i >= 0; i--) {
			IGLView v = mChildViews.get(i);
			if (!v.HitTest(x, y))
				continue;
			if (v.onDown(x, y)) {
				handled = true;
				break;
			}
		}

		return handled;
	}

	@Override
	public boolean onShowPress(float x, float y) {
		boolean handled = false;
		x -= mBounds.left;
		y -= mBounds.top;

		for (int i = mChildViews.size() - 1; i >= 0; i--) {
			IGLView v = mChildViews.get(i);
			if (!v.HitTest(x, y))
				continue;
			if (v.onShowPress(x, y)) {
				handled = true;
				break;
			}
		}

		return handled;
	}
	
	public void setOnTouchLisener(OnTouchLisener touch) {
		mTouchLisener = touch;
	}

	private void refreshPosData() {
		float[] pos = {
				//
				mBounds.left, mBounds.top, //
				mBounds.right, mBounds.top, //
				mBounds.left, mBounds.bottom, //
				mBounds.right, mBounds.bottom, //
		};
		mVBuffererticleBuffer.put(pos);
		mVBuffererticleBuffer.position(0);
	}
	
	private void refreshBKData() {

		float rgba[][] = null;
		if (mBackoundColor.length == 1) {
			rgba = new float[][] { { Color.red(mBackoundColor[0]) / 255.0f,
					Color.green(mBackoundColor[0]) / 255.0f,
					Color.blue(mBackoundColor[0]) / 255.0f,
					Color.alpha(mBackoundColor[0]) / 255.0f, } };

		} else if (mBackoundColor.length == 2) {
			rgba = new float[][] {
					{ Color.red(mBackoundColor[0]) / 255.0f,
							Color.green(mBackoundColor[0]) / 255.0f,
							Color.blue(mBackoundColor[0]) / 255.0f,
							Color.alpha(mBackoundColor[0]) / 255.0f, },
					{ Color.red(mBackoundColor[1]) / 255.0f,
							Color.green(mBackoundColor[1]) / 255.0f,
							Color.blue(mBackoundColor[1]) / 255.0f,
							Color.alpha(mBackoundColor[1]) / 255.0f, }, };

		} else if (mBackoundColor.length == 4) {
			rgba = new float[][] {
					{ Color.red(mBackoundColor[0]) / 255.0f,
							Color.green(mBackoundColor[0]) / 255.0f,
							Color.blue(mBackoundColor[0]) / 255.0f,
							Color.alpha(mBackoundColor[0]) / 255.0f, },
					{ Color.red(mBackoundColor[1]) / 255.0f,
							Color.green(mBackoundColor[1]) / 255.0f,
							Color.blue(mBackoundColor[1]) / 255.0f,
							Color.alpha(mBackoundColor[1]) / 255.0f, },
					{ Color.red(mBackoundColor[2]) / 255.0f,
							Color.green(mBackoundColor[2]) / 255.0f,
							Color.blue(mBackoundColor[2]) / 255.0f,
							Color.alpha(mBackoundColor[2]) / 255.0f, },
					{ Color.red(mBackoundColor[3]) / 255.0f,
							Color.green(mBackoundColor[3]) / 255.0f,
							Color.blue(mBackoundColor[3]) / 255.0f,
							Color.alpha(mBackoundColor[3]) / 255.0f, }, };
		}

		for (int i = 0; i < rgba.length; i++) {
			for (int j = 0; j < 4 / rgba.length; j++) {
				int p = i * 4 / rgba.length + j;
				for (int k = 0; k < rgba[i].length; k++) {
					mColorBuffer.put(p * 4 + k, rgba[i][k]);
				}
			}
		}
	}
}
