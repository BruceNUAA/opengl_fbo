package com.test.gl_draw.glview;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.GLClipManager;
import com.test.gl_draw.gl_base.GLRender;
import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.igl_draw.IGLView;
import com.test.gl_draw.utils.GLHelper;
import com.test.gl_draw.utils.NonThreadSafe;

public class GLView extends NonThreadSafe implements IGLView {

	public static int sID = 0;

	private RectF mBounds = new RectF();
	private OnTouchListener mTouchLisener = null;

	private OnVisibleChangeListener mVisibleListener = null;

	private boolean mVisible = true;
	private int mID = 0;

	private IGLView mParent = null;
	protected CopyOnWriteArrayList<IGLView> mChildViews = new CopyOnWriteArrayList<IGLView>();

	private TextureDraw mBackgoundDraw = new TextureDraw();

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
		GLRender.RequestRender(true);
	}

	public TextureDraw getBackgoundDraw() {
		return mBackgoundDraw;
	}

	@Override
	public void SetBackgound(int... color) {
		if (color.length != 1 && color.length != 2 && color.length != 4)
			throw new RuntimeException("背景颜色个数设置错误！");

		mBackgoundDraw.SetColor(color);
		InValidate();
	}

	@Override
	public void SetBackgound(Texture texture, boolean destory_texture_when_detach) {
		if (texture.isValid()) {
			mBackgoundDraw.SetTexture(texture, destory_texture_when_detach);
			InValidate();
		}
	}

	@Override
	public void Detach() {
		for (IGLView v : mChildViews)
			v.Detach();
		
		mBackgoundDraw.DetachFromView();
		mBackgoundDraw = null;
	}

	@Override
	public boolean visible() {
		return mVisible;
	}

	public void setOnVisibleListener(OnVisibleChangeListener li) {
		mVisibleListener = li;
	}

	public void SetVisible(boolean visible) {
		if (mVisible == visible)
			return;

		mVisible = visible;

		if (mVisibleListener != null) {
			mVisibleListener.OnVisibleChange(this);
		}
	}

	// 绘制
	@Override
	public void Draw(GL10 gl) {
	    CheckThread();
	    
		if (!visible())
			return;
		
		RectF r = ClipBound();
		
		if (r.isEmpty())
		    return;

		GLClipManager.getInstance().ClipRect(r);
		
		OnDrawBackgound(gl);

		OnDraw(gl);

		GLClipManager.getInstance().DisableClip();
		
		OnDrawChilds(gl);

		CheckThreadError();
	}

	@Override
	public void OnDrawBackgound(GL10 gl) {
		mBackgoundDraw.Draw(gl);
	}

	@Override
	public void OnDraw(GL10 gl) {
	}

	@Override
	public void OnDrawChilds(GL10 gl) {
		if (mChildViews.isEmpty())
			return;

		gl.glPushMatrix();
		gl.glTranslatef(mBounds.left, mBounds.top, 0);

		for (IGLView v : mChildViews) {
			gl.glPushMatrix();

			v.Draw(gl);

			gl.glPopMatrix();
		}

		gl.glPopMatrix();
	}

	//

	public void SetBounds(float... xyzh) {
		if (xyzh.length < 4)
			return;

		mBounds.set(xyzh[0], xyzh[1], xyzh[2] + xyzh[0], xyzh[3] + xyzh[1]);
	}

	@Override
	public void SetBounds(RectF rc) {
		if (rc != null && !rc.equals(mBounds)) {
			RectF tmp = new RectF(mBounds);

			mBounds.set(rc);

			for (IGLView v : mChildViews) {
				v.onParentLayoutChange(this, tmp, mBounds);
			}
			mBackgoundDraw.SetRenderRect(rc);

			InValidate();
		}
	}

	@Override
	public RectF Bounds() {
		return mBounds;
	}

	@Override
	public float getWidth() {
		return mBounds.width();
	}

	@Override
	public float getHeight() {
		return mBounds.height();
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

	public void PtInRender(float[] xy) {
		IGLView parent = Parent();
		while (parent != null) {
			RectF boundF = parent.Bounds();
			xy[0] += boundF.left;
			xy[1] += boundF.top;
			parent = parent.Parent();
		}

	}

	@Override
	public RectF ClipBound() {
		return VisibleBoundsInRender();
	}

	@Override
	public RectF ClipBoundForChildren() {
		return VisibleBoundsInRender();
	}

	@Override
	public void onParentLayoutChange(IGLView parent, RectF old_r, RectF new_r) {
		if (mChildViews.isEmpty())
			return;

		for (IGLView v : mChildViews) {
			v.onParentLayoutChange(this, old_r, new_r);
		}
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
		if (view == null || this.equals(view))
			return;

		view.SetParent(this);

		if (mChildViews.contains(view))
			return;

		mChildViews.add(view);

	}

	@Override
	public void RemoveAllView() {
	    for(IGLView v : mChildViews) 
	        v.Detach();
	    
		mChildViews.clear();
	}

	@Override
	public void RemoveView(IGLView view) {
		if (view == null)
			return;

		view.SetParent(null);

		if (!mChildViews.contains(view))
			return;
		
        view.Detach();
        
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
	public void detachFromThread() {
        super.detachFromThread();
        mBackgoundDraw.detachFromThread();
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

	public void setOnTouchLisener(OnTouchListener touch) {
		mTouchLisener = touch;
	}
}
