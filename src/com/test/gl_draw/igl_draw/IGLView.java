
package com.test.gl_draw.igl_draw;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.glview.GLView;

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
    
    void onParentLayoutChange(GLView parent, RectF old_r, RectF new_r);

    // 子View

    boolean HitTest(float x, float y);

    GLView FindViewByPos(float x, float y);

    GLView FindViewByID(int id);

    GLView Parent();

    void SetParent(GLView parent);

    void AddView(GLView view);

    void RemoveView(GLView view);
    
    void RemoveAllView();

}
