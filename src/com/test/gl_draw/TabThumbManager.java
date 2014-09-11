package com.test.gl_draw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.gl_fbo.R;
import com.test.gl_draw.gl_base.Texture;

public class TabThumbManager {

	public static float[] sShadowBorder = { 29, 19, 31, 40 };

	public static float[] sShadowStratchPos = { 56, 57, 50, 51 };

	private static TabThumbManager sTabThumbManager;
	private Texture mShadowTexture;

	public static TabThumbManager getInstance() {
		if (sTabThumbManager == null) {
			sTabThumbManager = new TabThumbManager();
		}
		return sTabThumbManager;
	}

	public Texture getShadowTexture() {
		if (mShadowTexture == null || !mShadowTexture.isValid()) {
			if (mShadowTexture == null)
				mShadowTexture = new Texture();

			Bitmap shadow = BitmapFactory.decodeResource(
					KApplication.sApplication.getResources(),
					R.drawable.muti_tab_shadow);
			mShadowTexture.Init(shadow, true);
		}
		return mShadowTexture;
	}
}
