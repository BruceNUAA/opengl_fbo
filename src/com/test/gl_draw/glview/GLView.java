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
	private CopyOnWriteArrayList<IGLView> mChildViews = new CopyOnWriteArrayList<IGLView>();

	private int mBackoundColor = Color.WHITE;
	private Texture mTexture = null;

	private FloatBuffer mVBuffererticleBuffer = BufferUtil
			.newFloatBuffer(4 * 2);
	private FloatBuffer mColorBuffer = BufferUtil.newFloatBuffer(4 * 4);

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
	public void SetBackgound(int color) {
		if (mBackoundColor != color) {
			mBackoundColor = color;
			InValidate();
		}
	}

	@Override
	public void SetBackgound(Texture texture) {
		if (texture.isValid()) {
			if (mTexture == null)
				mTexture = new Texture();

			mTexture.Init(texture);
			InValidate();
		}
	}

	// 绘制
	@Override
	public void Draw(GL10 gl) {
		refreshData();
		OnDrawBackgound(gl);

		OnDraw(gl);

		OnDrawChilds(gl);
	}

	@Override
	public void OnDrawBackgound(GL10 gl) {
		/*
		 * gl.glClearStencil(0); gl.glClear(GL10.GL_COLOR_BUFFER_BIT |
		 * GL10.GL_DEPTH_BUFFER_BIT |GL10.GL_STENCIL_BUFFER_BIT);
		 * 
		 * gl.glMatrixMode(GL10.GL_MODELVIEW); gl.glLoadIdentity();
		 * 
		 * gl.glEnable(GL10.GL_STENCIL_TEST); gl.glStencilFunc(GL10.GL_NEVER,
		 * 0x0, 0x0); gl.glStencilOp(GL10.GL_INCR,GL10.GL_INCR, GL10.GL_INCR);
		 */
		refreshData();

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
		gl.glVertexPointer(2, GL10.GL_FLOAT, 0, mVBuffererticleBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		/*
		 * //在模板缓冲区绘制(因为模板测试失败不能在颜色缓冲区写入) gl.glBegin(GL10.GL_LINE_STRIP); for
		 * (double angle = 0.0; angle < 400.0; angle += 0.1) {
		 * gl.glVertex3d(dRadius * cos(angle), dRadius * sin(angle), 0.0);
		 * dRadius *= 1.002; } gl.glEnd();
		 * 
		 * //现在与颜色缓冲区在模板缓冲区对应处有线的地方不会绘制 glStencilFunc(GL10.GL_NOTEQUAL, 0x1,
		 * 0x1); glStencilOp(GL10.GL_KEEP, GL10.GL_KEEP, GL10.GL_KEEP);
		 */
	}

	@Override
	public void OnDraw(GL10 gl) {

	}

	@Override
	public void OnDrawChilds(GL10 gl) {
		for (IGLView v : mChildViews) {
			gl.glPushMatrix();

			RectF bounds = v.Bounds();
			gl.glTranslatef(bounds.left, bounds.top, 0);

			v.Draw(gl);

			gl.glPopMatrix();
		}
	}

	//

	@Override
	public void SetBounds(RectF rc) {
		if (rc != null && !rc.equals(mBounds)) {
			mBounds.set(rc);
			InValidate();
		}
	}

	@Override
	public RectF Bounds() {
		return mBounds;
	}

	@Override
	public RectF BoundsInParent() {
		// TODO Auto-generated method stub
		return null;
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
	public void onSingleTapUp(float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(float start_x, float start_y, float cur_x,
			float cur_y, float distanceX, float distanceY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLongPress(float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFling(float start_x, float start_y, float cur_x, float cur_y,
			float velocityX, float velocityY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUp(float x, float y) {
		if (mTouchLisener != null && HitTest(x, y)) {
			mTouchLisener.OnClick(this);
		}

	}

	@Override
	public void onDown(float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onShowPress(float x, float y) {
		// TODO Auto-generated method stub

	}

	private void refreshData() {
		float[] pos = {
				//
				mBounds.left, mBounds.top, //
				mBounds.right, mBounds.top, //
				mBounds.left, mBounds.bottom, //
				mBounds.right, mBounds.bottom, //
		};
		mVBuffererticleBuffer.put(pos);
		mVBuffererticleBuffer.position(0);
		/*
		 * float[] color = { //
		 * 
		 * 0x00 / 255.0f, 0x26 / 255.0f, 0x4c / 255.0f, alpha,// 0x00 / 255.0f,
		 * 0x26 / 255.0f, 0x4c / 255.0f, alpha,// 0xa4 / 255.0f, 0xb9 / 255.0f,
		 * 0xcf / 255.0f, alpha,// 0xa4 / 255.0f, 0xb9 / 255.0f, 0xcf / 255.0f,
		 * alpha,// };
		 */
		float r = Color.red(mBackoundColor) / 255.0f;
		float g = Color.green(mBackoundColor) / 255.0f;
		float b = Color.blue(mBackoundColor) / 255.0f;
		float a = Color.alpha(mBackoundColor) / 255.0f;
		float[] color = {
				//
				r, g, b, a,//
				r, g, b, a,//

				r, g, b, a,//

				r, g, b, a,//

		};
		mColorBuffer.put(color);
		mColorBuffer.position(0);
	}

}
