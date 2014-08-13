package com.test.gl_draw;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

public class utils {

	public static long cellPowerOf2(long n) {
		n--;
		n |= n >> 1;
		n |= n >> 2;
		n |= n >> 4;
		n |= n >> 8;
		n |= n >> 16;
		n++;
		return n;
	}

	public static boolean isEGLContextOK() {
		return !((EGL10) EGLContext.getEGL()).eglGetCurrentContext().equals(
				EGL10.EGL_NO_CONTEXT);
	}

	public static void checkEGLContextOK() {
		if (!isEGLContextOK()) {
			throw new RuntimeException("Opengl context is not created !");
		}
	}

	public static void checkGLError(GL10 gl) {
		int error = gl.glGetError();
		if (error != GL10.GL_NO_ERROR) {
			throw new RuntimeException("GLError 0x"
					+ Integer.toHexString(error));
		}
	}

	public static int loadTexture(GL10 gl, Bitmap bitmap) {
		final int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);

		if (textures[0] == 0) {
			throw new RuntimeException("failed to load texture");
		}

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

		// inside antialias
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

		return textures[0];
	}

	public static int createTargetTexture(GL10 gl, int width, int height) {
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);

		if (textures[0] == 0) {
			throw new RuntimeException("failed to load texture");
		}

		gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);
		gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height, 0,
				GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_REPEAT);
		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);
		return textures[0];
	}

	public static void deleteTargetTexture(GL10 gl, int[] texture) {
		gl.glDeleteTextures(texture.length, texture, 0);
	}

	public static int createFrameBuffer(GL10 gl, int width, int height,
			int targetTextureId) {
		GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;
		int framebuffer;
		int[] framebuffers = new int[1];
		gl11ep.glGenFramebuffersOES(1, framebuffers, 0);
		framebuffer = framebuffers[0];
		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				framebuffer);

		int depthbuffer;
		int[] renderbuffers = new int[1];
		gl11ep.glGenRenderbuffersOES(1, renderbuffers, 0);
		depthbuffer = renderbuffers[0];

		gl11ep.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES,
				depthbuffer);
		gl11ep.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES,
				GL11ExtensionPack.GL_DEPTH_COMPONENT16, width, height);
		gl11ep.glFramebufferRenderbufferOES(
				GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				GL11ExtensionPack.GL_DEPTH_ATTACHMENT_OES,
				GL11ExtensionPack.GL_RENDERBUFFER_OES, depthbuffer);

		gl11ep.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
				GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D,
				targetTextureId, 0);
		int status = gl11ep
				.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
		if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
			throw new RuntimeException("Framebuffer is not complete: "
					+ Integer.toHexString(status));
		}

		gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, 0);
		return framebuffer;
	}

	public static void deleteFrameBuffers(GL10 gl, int[] fbo) {
		GL11ExtensionPack gl11ep = (GL11ExtensionPack) gl;
		gl11ep.glDeleteFramebuffersOES(fbo.length, fbo, 0);
	}

	public static boolean checkIfContextSupportsFrameBufferObject(GL10 gl) {
		return checkIfContextSupportsExtension(gl, "GL_OES_framebuffer_object");
	}

	/**
	 * This is not the fastest way to check for an extension, but fine if we are
	 * only checking for a few extensions each time a context is created.
	 * 
	 * @param gl
	 * @param extension
	 * @return true if the extension is present in the current context.
	 */
	public static boolean checkIfContextSupportsExtension(GL10 gl,
			String extension) {
		String extensions = " " + gl.glGetString(GL10.GL_EXTENSIONS) + " ";
		// The extensions string is padded with spaces between extensions,
		// but not
		// necessarily at the beginning or end. For simplicity, add spaces
		// at the
		// beginning and end of the extensions string and the extension
		// string.
		// This means we can avoid special-case checks for the first or last
		// extension, as well as avoid special-case checks when an extension
		// name
		// is the same as the first part of another extension name.
		return extensions.indexOf(" " + extension + " ") >= 0;
	}
}
