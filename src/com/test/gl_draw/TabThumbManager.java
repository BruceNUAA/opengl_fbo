package com.test.gl_draw;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.gl_fbo.R;
import com.test.gl_draw.gl_base.Texture;

public class TabThumbManager {

	private static float[] sShadowSizeInPixel = { 110, 102 };
	private static float[] sShadowBitmapSize = { 0, 0 };
	private static float[] sShadowBorder = { 29, 19, 31, 40 * 1.f };

	private static float[] sShadowStratchPos = { 56, 57, 50, 51 };

	private static TabThumbManager sTabThumbManager;
	private Texture mShadowTexture;

	public static TabThumbManager getInstance() {
		if (sTabThumbManager == null) {
			sTabThumbManager = new TabThumbManager();
		}
		return sTabThumbManager;
	}

	public float[] getShadowBorder() {
		float[] v = sShadowBorder.clone();
		float f = sShadowBitmapSize[0] / (float) sShadowSizeInPixel[0];
		for (int i = 0; i < v.length; i++) {
			v[i] *= f;
		}
		return v;
	}

	public float[] getShadowStratchPos() {
		float[] v = sShadowStratchPos.clone();
		float f = sShadowBitmapSize[0] / (float) sShadowSizeInPixel[0];
		for (int i = 0; i < v.length; i++) {
			v[i] *= f;
		}
		return v;
	}

	public Texture getShadowTexture() {
		if (mShadowTexture == null || !mShadowTexture.isValid()) {
			if (mShadowTexture == null)
				mShadowTexture = new Texture();

			Resources rs = KApplication.sApplication.getResources();
			Bitmap shadow = BitmapFactory.decodeResource(rs,
					R.drawable.muti_tab_shadow);

			sShadowBitmapSize[0] = shadow.getWidth();
			sShadowBitmapSize[1] = shadow.getHeight();

			mShadowTexture.Init(shadow, true);
		}
		return mShadowTexture;
	}
}
