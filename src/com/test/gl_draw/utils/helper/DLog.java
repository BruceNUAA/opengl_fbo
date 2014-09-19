package com.test.gl_draw.utils.helper;

import android.util.Log;

import com.example.gl_fbo.BuildConfig;

public class DLog {
	public final static int RESULT_SUCCESS = 0;

	public static int d(String tag, String message) {
		if (BuildConfig.DEBUG) {
			return Log.d(tag, message);
		} else {
			return RESULT_SUCCESS;
		}
	}

	public static int d(String tag, String message, Throwable tr) {
		if (BuildConfig.DEBUG) {
			return Log.d(tag, message, tr);
		} else {
			return RESULT_SUCCESS;
		}
	}

	public static int d(String tag, String format, Object... args) {
		if (BuildConfig.DEBUG) {
			String msg = String.format(format, args);
			return Log.d(tag, msg);
		} else {
			return RESULT_SUCCESS;
		}
	}

	public static int e(String tag, String message) {
		if (BuildConfig.DEBUG) {
			return Log.e(tag, message);
		} else {
			return RESULT_SUCCESS;
		}
	}

	public static int e(String tag, String format, Object... args) {
		if (BuildConfig.DEBUG) {
			String msg = String.format(format, args);
			return Log.e(tag, msg);
		} else {
			return RESULT_SUCCESS;
		}
	}

	public static int e(String tag, String message, Throwable tr) {
		if (BuildConfig.DEBUG) {
			return Log.e(tag, message, tr);
		} else {
			return RESULT_SUCCESS;
		}
	}

	public static int i(String tag, String message) {
		if (BuildConfig.DEBUG) {
			return Log.i(tag, message);
		} else {
			return RESULT_SUCCESS;
		}
	}

	public static int i(String tag, String format, Object... args) {
		if (BuildConfig.DEBUG) {
			String msg = String.format(format, args);
			return Log.i(tag, msg);
		} else {
			return RESULT_SUCCESS;
		}

	}

	public static int w(String tag, String message) {
		if (BuildConfig.DEBUG) {
			return Log.w(tag, message);
		} else {
			return RESULT_SUCCESS;
		}
	}

	public static int w(String tag, String format, Object... args) {
		if (BuildConfig.DEBUG) {
			String msg = String.format(format, args);
			return Log.w(tag, msg);
		} else {
			return RESULT_SUCCESS;
		}

	}

	public static int w(String tag, String message, Throwable tr) {
		if (BuildConfig.DEBUG) {
			return Log.w(tag, message, tr);
		} else {
			return RESULT_SUCCESS;
		}
	}

	public static int v(String tag, String message) {
		if (BuildConfig.DEBUG) {
			return Log.v(tag, message);
		} else {
			return RESULT_SUCCESS;
		}
	}
}
