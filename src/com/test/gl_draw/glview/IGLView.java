package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.igl_draw.ITouchEvent;

public interface IGLView extends ITouchEvent {

	// 事件
	interface OnTouchLisener {
		boolean OnClick(IGLView v);
	}

	//
	int id();

	void SetId(int x);

	// 绘制

	void InValidate();

	void SetBackgound(int... color);

	void SetBackgound(Texture texture);

	void Draw(GL10 gl);

	void OnDrawBackgound(GL10 gl);

	void OnDraw(GL10 gl);

	void OnDrawChilds(GL10 gl);

	// 区域
	void SetBounds(RectF rc);

	RectF Bounds();

	RectF VisibleBoundsInRender();
	
	RectF ClipBound();
	
	void Detach();

	// 子View

	boolean HitTest(float x, float y);

	IGLView FindViewByPos(float x, float y);

	IGLView FindViewByID(int id);

	IGLView Parent();

	void SetParent(IGLView parent);

	void AddView(IGLView view);

	void RemoveView(IGLView view);

}
