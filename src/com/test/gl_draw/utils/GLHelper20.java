package com.test.gl_draw.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.opengl.ETC1;
import android.opengl.ETC1Util;
import android.opengl.ETC1Util.ETC1Texture;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLException;
import android.opengl.GLUtils;

public class GLHelper20 {

	public static void checkGLError() {
		if (!GLHelper.EnableGLDebug())
			return;

		int error = GLES20.glGetError();

		if (error != GLES20.GL_NO_ERROR) {
			throw new GLException(error);
		}
	}

	public static boolean isTexture(int texture) {
		return texture != 0 && GLES20.glIsTexture(texture);
	}

	public static boolean isFrameBuffer(int framebuffer) {
		return framebuffer != 0 && GLES20.glIsFramebuffer(framebuffer);
	}

	public static int getTextureMaxSize() {
		int[] max = new int[1];
		GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, max, 0);
		return max[0];
	}

	public static int loadTexture(Bitmap bitmap) {

		if (bitmap == null || bitmap.isRecycled())
			return 0;

		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);

		if (textures[0] == 0 && GLHelper.EnableGLDebug()) {
			throw new RuntimeException("failed to load texture");
		}

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

		// inside antialias
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);

		int pixel_byte = 0;

		switch (bitmap.getConfig()) {
		case ALPHA_8:
			pixel_byte = 1;
			break;
		case RGB_565:
		case ARGB_4444:
			pixel_byte = 2;
			break;
		case ARGB_8888:
			pixel_byte = 4;
			break;
		}

		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, pixel_byte);

		if (!ETC1Util.isETC1Supported() || bitmap.hasAlpha()) {
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		} else {
			bitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
			int bpp = 2;

			ByteBuffer bb = ByteBuffer.allocateDirect(bitmap.getByteCount());
			bb.order(ByteOrder.nativeOrder());
			bitmap.copyPixelsToBuffer(bb);
			bb.position(0);

			int encodedImageSize = ETC1.getEncodedDataSize(bitmap.getWidth(),
					bitmap.getHeight());
			ByteBuffer compressedImage = ByteBuffer.allocateDirect(
					encodedImageSize).order(ByteOrder.nativeOrder());
			ETC1.encodeImage(bb, bitmap.getWidth(), bitmap.getHeight(), bpp,
					bpp * bitmap.getWidth(), compressedImage);
			ETC1Texture etc1tex = new ETC1Texture(bitmap.getWidth(),
					bitmap.getHeight(), compressedImage);

			ETC1Util.loadTexture(GLES10.GL_TEXTURE_2D, 0, 0, GLES10.GL_RGB,
					GLES10.GL_UNSIGNED_SHORT_5_6_5, etc1tex);
		}

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

		checkGLError();
		return textures[0];
	}

	public static int createTargetTexture(int width, int height) {

		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);

		if (textures[0] == 0) {
			throw new RuntimeException("failed to load texture");
		}

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width,
				height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);
		checkGLError();
		return textures[0];
	}

	public static void deleteTargetTexture(int... texture) {
		GLES20.glDeleteTextures(texture.length, texture, 0);
	}

	public static int createFrameBuffer(int[] size, int targetTextureId) {
		int[] fb = new int[1];
		int[] depthRb = new int[1];
		GLHelper20.checkGLError();
		// generate
		GLES20.glGenFramebuffers(1, fb, 0);
		GLES20.glGenRenderbuffers(1, depthRb, 0);

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fb[0]);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRb[0]);

		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
				GLES20.GL_DEPTH_COMPONENT16, size[0], size[1]);

		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
				targetTextureId, 0);
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);

		if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebuffer is not complete: "
					+ Integer.toHexString(status));
		}

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLHelper20.checkGLError();
		return fb[0];
	}

	public static boolean deleteFrameBuffers(int fbo) {
		if (!isFrameBuffer(fbo))
			return false;

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0, GL10.GL_TEXTURE_2D, 0, 0);

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glDeleteFramebuffers(1, new int[] { fbo }, 0);

		GLHelper20.checkGLError();
		return true;
	}

	/**
	 * Helper function to compile a shader.
	 * 
	 * @param shaderType
	 *            The shader type.
	 * @param shaderSource
	 *            The shader source code.
	 * @return An OpenGL handle to the shader.
	 */
	public static int compileShader(final int shaderType,
			final String shaderSource) {
		int shaderHandle = GLES20.glCreateShader(shaderType);

		if (shaderHandle != 0) {
			// Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, shaderSource);

			// Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			// Get the compilation status.
			final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS,
					compileStatus, 0);

			// If the compilation failed, delete the shader.
			if (compileStatus[0] == 0) {
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = 0;
			}
		}

		if (shaderHandle == 0) {
			throw new RuntimeException("Error creating shader."
					+ Integer.toHexString(GLES20.glGetError()));
		}

		return shaderHandle;
	}

	/**
	 * Helper function to compile and link a program.
	 * 
	 * @param vertexShaderHandle
	 *            An OpenGL handle to an already-compiled vertex shader.
	 * @param fragmentShaderHandle
	 *            An OpenGL handle to an already-compiled fragment shader.
	 * @param attributes
	 *            Attributes that need to be bound to the program.
	 * @return An OpenGL handle to the program.
	 */
	public static int createAndLinkProgram(final int vertexShaderHandle,
			final int fragmentShaderHandle, final String[] attributes) {
		int programHandle = GLES20.glCreateProgram();

		if (programHandle != 0) {
			// Bind the vertex shader to the program.
			GLES20.glAttachShader(programHandle, vertexShaderHandle);

			// Bind the fragment shader to the program.
			GLES20.glAttachShader(programHandle, fragmentShaderHandle);

			// Bind attributes
			if (attributes != null) {
				final int size = attributes.length;
				for (int i = 0; i < size; i++) {
					GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
				}
			}

			// Link the two shaders together into a program.
			GLES20.glLinkProgram(programHandle);

			// Get the link status.
			final int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS,
					linkStatus, 0);

			// If the link failed, delete the program.
			if (linkStatus[0] == 0) {
				GLES20.glDeleteProgram(programHandle);
				programHandle = 0;
			}
		}

		if (programHandle == 0) {
			throw new RuntimeException("Error creating program.");
		}

		return programHandle;
	}

	public static String readTextFileFromRawResource(Resources rs,
			int resourceId) {
		InputStream inputStream = rs.openRawResource(resourceId);
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		String nextLine;
		StringBuilder body = new StringBuilder();

		try {
			while ((nextLine = bufferedReader.readLine()) != null) {
				body.append(nextLine);
				body.append('\n');
			}
		} catch (IOException e) {
			return null;
		}

		return body.toString();
	}
}
