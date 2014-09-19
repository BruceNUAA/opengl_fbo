package com.test.gl_draw.test;

import javax.microedition.khronos.opengles.GL10;

import com.test.gl_draw.gl_base.FrameBuffer;
import com.test.gl_draw.glview.GLRotateView;

public class GLRotateViewTest extends GLRotateView {
	// 绘制
		@Override
		public void Draw(GL10 gl) {
			
			FrameBuffer.getInstance().DrawToLayer(gl, 0.5f);
			
			super.Draw(gl);
			
			FrameBuffer.getInstance().Restore(gl);
		}
}
