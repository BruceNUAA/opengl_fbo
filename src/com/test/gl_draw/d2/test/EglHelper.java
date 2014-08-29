package com.test.gl_draw.d2.test;

import java.lang.ref.WeakReference;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;

import android.util.Log;
import android.view.SurfaceView;

public class EglHelper {
	static private EglHelper sEglHelper = null;

	static synchronized public EglHelper getInstance() {

		if (sEglHelper == null) {
			sEglHelper = new EglHelper();
		}

		return sEglHelper;
	}

	public void init() {
		start();
	}

	/**
	 * Initialize EGL for a given configuration spec.
	 * 
	 * @param configSpec
	 */
	public void start() {
		/*
		 * Get an EGL instance
		 */
		mEgl = (EGL10) EGLContext.getEGL();

		/*
		 * Get to the default display.
		 */
		mEglDisplay = mEgl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

		if (mEglDisplay == EGL10.EGL_NO_DISPLAY) {
			throw new RuntimeException("eglGetDisplay failed");
		}

		/*
		 * We can now initialize EGL for that display
		 */
		int[] version = new int[2];
		if (!mEgl.eglInitialize(mEglDisplay, version)) {
			throw new RuntimeException("eglInitialize failed");
		}
		mEglConfig = mEGLConfigChooser.chooseConfig(mEgl, mEglDisplay);

		/*
		 * Create an EGL context. We want to do this as rarely as we can,
		 * because an EGL context is a somewhat heavy object.
		 */
		mEglContext = mEGLContextFactory.createContext(mEgl, mEglDisplay,
				mEglConfig);
		if (mEglContext == null || mEglContext == EGL10.EGL_NO_CONTEXT) {
			mEglContext = null;
			throwEglException("createContext");
		}

		mEglSurface = null;
	}

	/**
	 * Create an egl surface for the current SurfaceHolder surface. If a surface
	 * already exists, destroy it before creating the new surface.
	 * 
	 * @return true if the surface was created successfully.
	 */
	public boolean AttatchSurfaceView(SurfaceView view) {
		if (view == null)
			return false;
		/*
		 * Check preconditions.
		 */
		if (mEgl == null) {
			throw new RuntimeException("egl not initialized");
		}
		if (mEglDisplay == null) {
			throw new RuntimeException("eglDisplay not initialized");
		}
		if (mEglConfig == null) {
			throw new RuntimeException("mEglConfig not initialized");
		}

		/*
		 * The window size has changed, so we need to create a new surface.
		 */
		destroySurfaceImp();

		/*
		 * Create an EGL surface we can render into.
		 */
		mEglSurface = mEGLWindowSurfaceFactory.createWindowSurface(mEgl,
				mEglDisplay, mEglConfig, view.getHolder());

		if (mEglSurface == null || mEglSurface == EGL10.EGL_NO_SURFACE) {
			int error = mEgl.eglGetError();
			if (error == EGL10.EGL_BAD_NATIVE_WINDOW) {
				Log.e("EglHelper",
						"createWindowSurface returned EGL_BAD_NATIVE_WINDOW.");
			}
			return false;
		}

		/*
		 * Before we can issue GL commands, we need to make sure the context is
		 * current and bound to a surface.
		 */
		if (!mEgl.eglMakeCurrent(mEglDisplay, mEglSurface, mEglSurface,
				mEglContext)) {
			/*
			 * Could not make the context current, probably because the
			 * underlying SurfaceView surface has been destroyed.
			 */
			logEglErrorAsWarning("EGLHelper", "eglMakeCurrent",
					mEgl.eglGetError());
			return false;
		}

		return true;
	}

	/**
	 * Create a GL object for the current EGL context.
	 * 
	 * @return
	 */
	GL createGL() {

		GL gl = mEglContext.getGL();
		return gl;
	}

	/**
	 * Display the current render surface.
	 * 
	 * @return the EGL error code from eglSwapBuffers.
	 */
	public int swap() {
		if (!mEgl.eglSwapBuffers(mEglDisplay, mEglSurface)) {
			return mEgl.eglGetError();
		}
		return EGL10.EGL_SUCCESS;
	}

	public void destroySurface() {

		destroySurfaceImp();
	}

	private void destroySurfaceImp() {
		if (mEglSurface != null && mEglSurface != EGL10.EGL_NO_SURFACE) {
			mEgl.eglMakeCurrent(mEglDisplay, EGL10.EGL_NO_SURFACE,
					EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
			mEGLWindowSurfaceFactory.destroySurface(mEgl, mEglDisplay,
					mEglSurface);
			mEglSurface = null;
		}
	}

	public void finish() {

		if (mEglContext != null) {
			mEGLContextFactory.destroyContext(mEgl, mEglDisplay, mEglContext);
			mEglContext = null;
		}
		if (mEglDisplay != null) {
			mEgl.eglTerminate(mEglDisplay);
			mEglDisplay = null;
		}
	}

	private void throwEglException(String function) {
		throwEglException(function, mEgl.eglGetError());
	}

	public static void throwEglException(String function, int error) {
		String message = formatEglError(function, error);

		throw new RuntimeException(message);
	}

	public static void logEglErrorAsWarning(String tag, String function,
			int error) {
		Log.w(tag, formatEglError(function, error));
	}

	public static String formatEglError(String function, int error) {
		return function + " failed: ";
	}

	private WeakReference<CustomGLSurfaceView> mGLSurfaceViewWeakRssef;
	EGL10 mEgl;
	EGLDisplay mEglDisplay;
	EGLSurface mEglSurface;
	EGLConfig mEglConfig;
	EGLContext mEglContext;

	EGLWindowSurfaceFactory mEGLWindowSurfaceFactory = new DefaultWindowSurfaceFactory();

	EGLContextFactory mEGLContextFactory = new DefaultContextFactory();
	EGLConfigChooser mEGLConfigChooser = new EGLConfigChooser(8, 8, 8, 8, 0, 0);

	public interface EGLWindowSurfaceFactory {
		/**
		 * @return null if the surface cannot be constructed.
		 */
		EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display,
				EGLConfig config, Object nativeWindow);

		void destroySurface(EGL10 egl, EGLDisplay display, EGLSurface surface);
	}

	private static class DefaultWindowSurfaceFactory implements
			EGLWindowSurfaceFactory {

		public EGLSurface createWindowSurface(EGL10 egl, EGLDisplay display,
				EGLConfig config, Object nativeWindow) {
			EGLSurface result = null;
			try {
				result = egl.eglCreateWindowSurface(display, config,
						nativeWindow, null);
			} catch (IllegalArgumentException e) {
			}
			return result;
		}

		public void destroySurface(EGL10 egl, EGLDisplay display,
				EGLSurface surface) {
			egl.eglDestroySurface(display, surface);
		}
	}

	/**
	 * An interface for customizing the eglCreateContext and eglDestroyContext
	 * calls.
	 * <p>
	 * This interface must be implemented by clients wishing to call
	 * {@link CustomGLSurfaceView#setEGLContextFactory(EGLContextFactory)}
	 */
	public interface EGLContextFactory {
		EGLContext createContext(EGL10 egl, EGLDisplay display,
				EGLConfig eglConfig);

		void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context);
	}

	private class DefaultContextFactory implements EGLContextFactory {

		public EGLContext createContext(EGL10 egl, EGLDisplay display,
				EGLConfig config) {
			return egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT,
					null);
		}

		public void destroyContext(EGL10 egl, EGLDisplay display,
				EGLContext context) {
			if (!egl.eglDestroyContext(display, context)) {
				Log.e("DefaultContextFactory", "display:" + display
						+ " context: " + context);

				EglHelper.throwEglException("eglDestroyContex",
						egl.eglGetError());
			}
		}
	}
}
