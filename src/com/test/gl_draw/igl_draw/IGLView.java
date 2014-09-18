
package com.test.gl_draw.igl_draw;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.Texture;

public interface IGLView extends ITouchEvent {

    // 事件
    interface OnTouchListener {
        boolean OnClick(IGLView v);
    }

    interface OnVisibleChangeListener {
        void OnVisibleChange(IGLView v);
    }
    
    //
    int id();

    void SetId(int x);

    // 绘制

    boolean visible();
    
    void InValidate();

    void SetBackgound(int... color);

    void SetBackgound(Texture texture, boolean destory_texture_when_detach);

    void Draw(GL10 gl);

    void OnDrawBackgound(GL10 gl);

    void OnDraw(GL10 gl);

    void OnDrawChilds(GL10 gl);

    // 区域

    float getWidth();

    float getHeight();

    void SetBounds(RectF rc);

    RectF Bounds();

    RectF VisibleBoundsInRender();

    RectF ClipBound();
    
    RectF ClipBoundForChildren();

    void Detach();
    
    void onParentLayoutChange(IGLView parent, RectF old_r, RectF new_r);

    // 子View

    boolean HitTest(float x, float y);

    IGLView FindViewByPos(float x, float y);

    IGLView FindViewByID(int id);

    IGLView Parent();

    void SetParent(IGLView parent);

    void AddView(IGLView view);

    void RemoveView(IGLView view);
    
    void RemoveAllView();

}
