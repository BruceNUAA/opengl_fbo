package com.test.gl_draw.utils;

import android.content.Context;

import com.example.gl_fbo.BuildConfig;

public class DebugToast {

	public static void showShort(Context context, int id) {
		if (!BuildConfig.DEBUG)
			return;

		CustomToast.showShort(context, id);
	}

	public static void showShort(Context context, String text) {
		if (!BuildConfig.DEBUG)
			return;

		CustomToast.showShort(context, text);
	}

	public static void showLong(Context context, int id) {
		if (!BuildConfig.DEBUG)
			return;

		CustomToast.showLong(context, id);
	}

	public static void showLong(Context context, String text) {
		if (!BuildConfig.DEBUG)
			return;

		CustomToast.showLong(context, text);
	}

}
