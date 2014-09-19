package com.test.gl_draw.utils.helper;

import android.content.Context;
import android.widget.Toast;

public class CustomToast {

	public static void showShort(Context context, int id) {
		if (context == null) {
			return;
		}

		String text = context.getResources().getString(id);
		showSingleton(context, text, true);
	}

	public static void showShort(Context context, String text) {
		if (context == null) {
			return;
		}

		showSingleton(context, text, true);
	}

	public static void showLong(Context context, int id) {
		if (context == null) {
			return;
		}

		String text = context.getResources().getString(id);
		showSingleton(context, text, false);
	}

	public static void showLong(Context context, String text) {
		if (context == null) {
			return;
		}

		showSingleton(context, text, false);
	}

	private static Toast sToast = null;

	private static void showSingleton(Context context, String text,
			boolean isShort) {
		int type = Toast.LENGTH_LONG;
		if (isShort) {
			type = Toast.LENGTH_SHORT;
		}

		if (sToast == null) {
			sToast = Toast.makeText(context, text, type);
		} else {
			sToast.setText(text);
			sToast.setDuration(type);
		}
		sToast.show();
	}
}
