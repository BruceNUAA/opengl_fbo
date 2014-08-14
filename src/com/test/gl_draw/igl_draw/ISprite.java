package com.test.gl_draw.igl_draw;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

public interface ISprite {
	public interface IDataProvider {
		Bitmap getRenderBitmap();

		float[] getRenderRect();

		float[] getOrigin();

		float[] getRotateOrigin();

		float getRotateDegree();

		float getAlpha();
	}

	void setDataProvider(IDataProvider provider);

	void onSurfaceCreated(GL10 gl);

	void onSurfaceChanged(GL10 gl, int w, int h);

	void onDrawFrame(GL10 gl);
}
