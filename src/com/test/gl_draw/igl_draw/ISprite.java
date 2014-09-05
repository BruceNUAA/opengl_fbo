package com.test.gl_draw.igl_draw;

import com.test.gl_draw.gl_base.Texture;

import javax.microedition.khronos.opengles.GL10;

public interface ISprite {
	public interface IDataProvider {
	    Texture getRenderTexture();

		float[] getRenderRect();

		float[] getOrigin();

		float[] getRotateOrigin();

		float getRotateDegree();

		float getAlpha();
		
		boolean isVisible();
	}

	void setDataProvider(IDataProvider provider);

	void onSurfaceChanged(GL10 gl, int w, int h);

	void onDrawFrame(GL10 gl);
}
