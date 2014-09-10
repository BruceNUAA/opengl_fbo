package com.test.gl_draw;

import android.app.Application;

public class KApplication extends Application {
	public static KApplication sApplication;

	public void onCreate() {
		super.onCreate();
		sApplication = this;
	}
}
