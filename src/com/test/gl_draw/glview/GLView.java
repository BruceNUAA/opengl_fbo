
package com.test.gl_draw.glview;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;
import android.opengl.Matrix;

import com.test.gl_draw.data.Texture;
import com.test.gl_draw.gl_base.GLClipManager;
import com.test.gl_draw.gl_base.GLObject;
import com.test.gl_draw.gl_base.GLRender;
import com.test.gl_draw.gl_base.GLShadeManager;
import com.test.gl_draw.igl_draw.ITouchEvent;

public class GLView extends GLObject implements ITouchEvent {

    // 事件
    public interface OnTouchListener {
        boolean OnClick(GLView v);
    }
    
    public static int sID = 0;

    private RectF mBounds = new RectF();
    private OnTouchListener mTouchLisener = null;

    private boolean mVisible = true;
    private int mID = 0;

    private GLView mParent = null;
    protected CopyOnWriteArrayList<GLView> mChildViews = new CopyOnWriteArrayList<GLView>();

    private TextureDraw mBackgoundDraw = new TextureDraw();

    public static int sRenderWidth = 0;
    public static int sRenderHeight = 0;

    private GLView mTouchTargetGlView = null;
    
    private float mAlpha = 1;

    public GLView() {
        mID = sID;
        sID++;
    }

    public int id() {
        return mID;
    }

    public void SetId(int id) {
        mID = id;
    }

    public void InValidate() {
        GLRender.RequestRender(true);
    }

    public TextureDraw getBackgoundDraw() {
        return mBackgoundDraw;
    }

    public void SetBackgound(int... color) {
        if (color.length != 1 && color.length != 2 && color.length != 4)
            throw new RuntimeException("背景颜色个数设置错误！");

        mBackgoundDraw.SetColor(color);
        InValidate();
    }

    public void SetBackgound(Texture texture,
            boolean destory_texture_when_detach) {
            mBackgoundDraw.SetTexture(texture, destory_texture_when_detach);
            InValidate();
    }

    public void SetAlpha(float alpha) {
        mAlpha = Math.max(0, Math.min(1, alpha));
        mBackgoundDraw.SetAlpha(alpha);
    }

    public void Detach() {
        BeforeThreadCall();
        
        for (GLView v : mChildViews)
            v.Detach();

        mBackgoundDraw.DetachFromView();
        
        AfterThreadCall();
    }

    public boolean visible() {
        return mVisible;
    }

    public void SetVisible(boolean visible) {
        if (mVisible == visible)
            return;

        mVisible = visible;

        for (GLView view : mChildViews) {
            view.OnParentVisibleChange(this);
        }
        
        OnVisibleChange(this, visible);
        
        InValidate();
    }
    
    public void OnVisibleChange(GLView view, boolean visible) {
        mBackgoundDraw.setVisible(view.visible());
        
        for (GLView v : mChildViews) {
            v.OnParentVisibleChange(view);
        }
    }
    
    public void OnParentVisibleChange(GLView view) {
        // 如果本身是可见的，父窗口的可见性改变必然会影响到子窗口
        if(visible()) {
            OnVisibleChange(view, view.visible());
        }
    }

    // 绘制
    public void Draw(GL10 gl) {

        if (!visible() || mAlpha == 0)
            return;

        RectF r = ClipBound();

        if (r.isEmpty())
            return;

        BeforeThreadCall();
        
        GLClipManager.getInstance().ClipRect(gl, r);

        OnDrawBackgound(gl);

        OnDraw(gl);

        GLClipManager.getInstance().DisableClip(gl);

        OnDrawChilds(gl);

        AfterThreadCall();
    }

    public void OnDrawBackgound(GL10 gl) {
        mBackgoundDraw.Draw(gl);
    }

    public void OnDraw(GL10 gl) {
    }

    public void OnDrawChilds(GL10 gl) {
        if (mChildViews.isEmpty())
            return;

        BeforeThreadCall();
        
        GLShadeManager shade_mgr = GLShadeManager.getInstance();
        shade_mgr.PushMatrix();
        Matrix.translateM(shade_mgr.getModelMatrix(), 0, mBounds.left, mBounds.top, 0);

        for (GLView v : mChildViews) {
        	shade_mgr.PushMatrix();

            v.Draw(gl);

            shade_mgr.PopMatrix();
        }

        shade_mgr.PopMatrix();
        
        AfterThreadCall();
    }

    //
    public void SetX(float x) {
        RectF rc = new RectF(Bounds());
        rc.offset(x - rc.left, 0);
        SetBounds(rc);
    }
    
    public void SetY(float y) {
        RectF rc = new RectF(Bounds());
        rc.offset(0, y - rc.top);
        SetBounds(rc);
    }

    public void SetBounds(float... xywh) {
        if (xywh.length < 4)
            return;

        RectF rc = new RectF(xywh[0], xywh[1], xywh[2] + xywh[0], xywh[3]
                + xywh[1]);
        SetBounds(rc);
    }

    public void SetBounds(RectF rc) {
        if (rc != null && !rc.equals(mBounds)) {
            RectF tmp = new RectF(mBounds);

            mBounds.set(rc);

            for (GLView v : mChildViews) {
                v.onParentLayoutChange(this, tmp, mBounds);
            }
            mBackgoundDraw.SetRenderRect(rc);

            InValidate();
        }
    }

    public RectF Bounds() {
        return mBounds;
    }

    public float getWidth() {
        return mBounds.width();
    }

    public float getHeight() {
        return mBounds.height();
    }

    public RectF VisibleBoundsInRender() {
        RectF rc = new RectF(mBounds);

        GLView parent = Parent();
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
        GLView parent = Parent();
        while (parent != null) {
            RectF boundF = parent.Bounds();
            xy[0] += boundF.left;
            xy[1] += boundF.top;
            parent = parent.Parent();
        }

    }

    public RectF ClipBound() {
        return VisibleBoundsInRender();
    }

    public RectF ClipBoundForChildren() {
        return VisibleBoundsInRender();
    }

    public float[] getPreferSize() {
        return new float[] {
                Bounds().width(), Bounds().height()
        };
    }

    public void onParentLayoutChange(GLView parent, RectF old_r, RectF new_r) {
        if (mChildViews.isEmpty())
            return;

        for (GLView v : mChildViews) {
            v.onParentLayoutChange(this, old_r, new_r);
        }
    }

    //
    public boolean HitTest(float x, float y) {
        return mBounds.contains(x, y);
    }

    public GLView Parent() {
        return mParent;
    }

    public void SetParent(GLView parent) {
        mParent = parent;
    }

    public GLView FindViewByPos(float x, float y) {

        GLView view = null;

        if (HitTest(x, y)) {
            view = this;
        } else {
            x -= mBounds.left;
            y -= mBounds.top;

            for (GLView v : mChildViews) {
                if (HitTest(x, y)) {
                    view = v;
                    break;
                }
            }
        }
        return view;

    }

    public GLView FindViewByID(int id) {
        GLView view = null;

        if (id() == id) {
            view = this;
        } else {
            for (GLView v : mChildViews) {

                if (v.id() == id) {
                    view = v;
                    break;
                }
            }
        }

        return view;
    }

    public void AddView(GLView view) {
    	
        if (view == null || this.equals(view))
            return;

        view.SetParent(this);

        if (mChildViews.contains(view))
            return;

        mChildViews.add(view);

    }

    public void RemoveAllView() {

        CopyOnWriteArrayList<GLView> tmp = (CopyOnWriteArrayList<GLView>) mChildViews
                .clone();

        mChildViews.clear();

        for (GLView v : tmp)
            v.Detach();

        tmp.clear();
        tmp = null;
    }

    public void RemoveView(GLView view) {
        if (view == null)
            return;

        view.SetParent(null);

        if (!mChildViews.contains(view))
            return;

        mChildViews.remove(view);

        view.Detach();
    }

    // touch
    @Override
    public boolean onSingleTapUp(float x, float y) {
        if (!mVisible)
            return false;
        
        boolean handled = false;
        x -= mBounds.left;
        y -= mBounds.top;
        if (mTouchTargetGlView == null) {
            for (int i = mChildViews.size() - 1; i >= 0; i--) {
                GLView v = mChildViews.get(i);
                if (!v.HitTest(x, y))
                    continue;
                if (v.onSingleTapUp(x, y)) {
                    handled = true;
                    break;
                }
            }
        } else {
            handled = mTouchTargetGlView.onSingleTapUp(x, y);
        }

        return handled;
    }

    @Override
    public boolean onScroll(float start_x, float start_y, float cur_x,
            float cur_y, float distanceX, float distanceY) {
        if (!mVisible)
            return false;
        
        boolean handled = false;
        start_x -= mBounds.left;
        start_y -= mBounds.top;
        cur_x -= mBounds.left;
        cur_y -= mBounds.top;

        if (mTouchTargetGlView == null) {
            for (int i = mChildViews.size() - 1; i >= 0; i--) {
                GLView v = mChildViews.get(i);
                if (!v.HitTest(cur_x, cur_y))
                    continue;
                if (v.onScroll(start_x, start_y, cur_x, cur_y, distanceX,
                        distanceY)) {
                    handled = true;
                    break;
                }
            }
        } else {
            handled = mTouchTargetGlView.onScroll(start_x, start_y, cur_x,
                    cur_y, distanceX, distanceY);
        }

        return handled;

    }

    @Override
    public boolean onLongPress(float x, float y) {
        if (!mVisible)
            return false;
        
        boolean handled = false;
        x -= mBounds.left;
        y -= mBounds.top;

        if (mTouchTargetGlView == null) {
            for (int i = mChildViews.size() - 1; i >= 0; i--) {
                GLView v = mChildViews.get(i);
                if (!v.HitTest(x, y))
                    continue;
                if (v.onLongPress(x, y)) {
                    handled = true;
                    break;
                }
            }
        } else {
            handled = mTouchTargetGlView.onLongPress(x, y);
        }

        return handled;

    }

    @Override
    public boolean onFling(float start_x, float start_y, float cur_x,
            float cur_y, float velocityX, float velocityY) {
        if (!mVisible)
            return false;
        
        boolean handled = false;
        start_x -= mBounds.left;
        start_y -= mBounds.top;
        cur_x -= mBounds.left;
        cur_y -= mBounds.top;
        if (mTouchTargetGlView == null) {
            for (int i = mChildViews.size() - 1; i >= 0; i--) {
                GLView v = mChildViews.get(i);
                if (!v.HitTest(cur_x, cur_y))
                    continue;
                if (v.onFling(start_x, start_y, cur_x, cur_y, velocityX,
                        velocityY)) {
                    handled = true;
                    break;
                }
            }
        } else {
            handled = mTouchTargetGlView.onFling(start_x, start_y, cur_x,
                    cur_y, velocityX, velocityY);
        }

        return handled;

    }

    @Override
    public boolean onUp(float x, float y) {
        if (!mVisible)
            return false;
        
        boolean handled = false;
        float re_x = x - mBounds.left;
        float re_y = y - mBounds.top;

        if (mTouchTargetGlView == null) {
            for (int i = mChildViews.size() - 1; i >= 0; i--) {
                GLView v = mChildViews.get(i);
                if (!v.HitTest(re_x, re_y))
                    continue;

                if (v.onUp(re_x, re_y)) {
                    handled = true;
                    break;
                }
            }
        } else {
            handled = mTouchTargetGlView.onUp(re_x, re_y);

            OnTouchListener li = mTouchTargetGlView.getOnTouchLisener();
            if (!handled && mTouchTargetGlView.HitTest(x, y) && li != null) {
                handled = li.OnClick(mTouchTargetGlView);
            }

            mTouchTargetGlView = null;
        }

        return handled;
    }

    @Override
    public boolean onDown(float x, float y) {
        if (!mVisible)
            return false;
        
        boolean handled = false;
        x -= mBounds.left;
        y -= mBounds.top;

        mTouchTargetGlView = null;

        for (int i = mChildViews.size() - 1; i >= 0; i--) {
            GLView v = mChildViews.get(i);
            if (!v.HitTest(x, y))
                continue;

            if (v.onDown(x, y)) {

                mTouchTargetGlView = v;

                handled = true;
                break;
            } else if (v.getOnTouchLisener() != null) {
                mTouchTargetGlView = v;
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
        if (!mVisible)
            return false;
        
        boolean handled = false;
        x -= mBounds.left;
        y -= mBounds.top;

        if (mTouchTargetGlView == null) {
            for (int i = mChildViews.size() - 1; i >= 0; i--) {
                GLView v = mChildViews.get(i);
                if (!v.HitTest(x, y))
                    continue;
                if (v.onShowPress(x, y)) {
                    handled = true;
                    break;
                }
            }
        } else {
            handled = mTouchTargetGlView.onShowPress(x, y);
        }

        return handled;
    }

    public void setOnTouchLisener(OnTouchListener touch) {
        mTouchLisener = touch;
    }

    public OnTouchListener getOnTouchLisener() {
        return mTouchLisener;
    }
}
