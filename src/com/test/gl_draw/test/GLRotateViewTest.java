package com.test.gl_draw.test;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.FrameBuffer;
import com.test.gl_draw.glview.GLRotateView;

public class GLRotateViewTest extends GLRotateView {
	// 绘制
		@Override//
		public void Draw(GL10 gl) {
			float alpha = 0.5f;//1-  Math.abs(getRotateDegree()/180)*10f;
			
			FrameBuffer.getInstance().DrawToLayer(gl, alpha);
			
			super.Draw(gl);
			
			FrameBuffer.getInstance().Restore(gl);
		}
		
		@Override
		public void SetBounds(RectF rc) {
			super.SetBounds(rc);
			
			setRotateOrigin(rc.centerX(), rc.centerY());
		}
		
}
