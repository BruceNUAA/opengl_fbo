package com.test.gl_draw.test;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.data.GLFrameBuffer;
import com.test.gl_draw.glview.GLRotateView;

public class GLRotateViewTest extends GLRotateView {
	public GLRotateViewTest() {
		
	}
	// 绘制
		@Override//
		public void Draw(GL10 gl) {
			float alpha = 0.1f;//1-  Math.abs(getRotateDegree()/180)*10f;
			
			GLFrameBuffer.getInstance().DrawToLayer(gl, alpha);
			
			super.Draw(gl);

			GLFrameBuffer.getInstance().Restore(gl);
		}
		
		@Override
		public void SetBounds(RectF rc) {
			super.SetBounds(rc);
			
			setRotateOrigin(rc.centerX(), rc.centerY());
		}
		
}
