package com.example.gl20;

import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import com.example.gl_fbo.R;

/**
 * This class implements our custom renderer. Note that the GL10 parameter
 * passed in is unused for OpenGL ES 2.0 renderers -- the static class GLES20 is
 * used instead.
 */
public class TestRenderer implements GLSurfaceView.Renderer, IShadeManager {
	private final Context mActivityContext;

	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix
	 * transforms world space to eye space; it positions things relative to our
	 * eye.
	 */
	private float[] mViewMatrix = new float[16];

	/**
	 * Store the projection matrix. This is used to project the scene onto a 2D
	 * viewport.
	 */
	private float[] mProjectionMatrix = new float[16];

	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	private float[] mMVPMatrix = new float[16];

	/** This will be used to pass in the transformation matrix. */
	private int mMVPMatrixHandle;

	/** This will be used to pass in the modelview matrix. */
	private int mMVMatrixHandle;

	/** This will be used to pass in the texture. */
	private int mTextureUniformHandle;

	/** This will be used to pass in model position information. */
	private int mPositionHandle;

	/** This will be used to pass in model color information. */
	private int mColorHandle;

	/** This will be used to pass in model texture coordinate information. */
	private int mTextureCoordinateHandle;

	/** This is a handle to our cube shading program. */
	private int mProgramHandle;

	/** This is a handle to our texture data. */
	private int mTextureDataHandle;

	private cube mCube;
	private Texture2D mTexture2D;

	/**
	 * Initialize the model data.
	 */
	public TestRenderer(Context activityContext) {
		mActivityContext = activityContext;
		mCube = new cube(this);
		mTexture2D = new Texture2D(this);
	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		// Set the background clear color to black.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);

		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		// The below glEnable() call is a holdover from OpenGL ES 1, and is not
		// needed in OpenGL ES 2.
		// Enable texture mapping
		//GLES20.glEnable(GLES20.GL_TEXTURE_2D);

		// Position the eye in front of the origin.
		final float eyeX = 0.0f;
		final float eyeY = 0.0f;
		final float eyeZ = -0.5f;

		// We are looking toward the distance
		final float lookX = 0.0f;
		final float lookY = 0.0f;
		final float lookZ = -0f;

		// Set our up vector. This is where our head would be pointing were we
		// holding the camera.
		final float upX = 0.0f;
		final float upY = 1.0f;
		final float upZ = 0.0f;
		ToolsUtil.checkGLError();
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY,
				lookZ, upX, upY, upZ);
		ToolsUtil.checkGLError();
		final String vertexShader = ToolsUtil.readTextFileFromRawResource(
				mActivityContext, R.raw.per_pixel_vertex_shader);
		final String fragmentShader = ToolsUtil.readTextFileFromRawResource(
				mActivityContext, R.raw.per_pixel_fragment_shader);

		final int vertexShaderHandle = ToolsUtil.compileShader(
				GLES20.GL_VERTEX_SHADER, vertexShader);
		final int fragmentShaderHandle = ToolsUtil.compileShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShader);

		mProgramHandle = ToolsUtil.createAndLinkProgram(vertexShaderHandle,
				fragmentShaderHandle, new String[] { "a_Position", "a_Color",
						"a_TexCoordinate" });
		ToolsUtil.checkGLError();
		// Load the texture
		mTextureDataHandle = ToolsUtil.loadTexture(mActivityContext,
				R.drawable.img);

		GLES20.glUseProgram(mProgramHandle);

		// Set program handles for cube drawing.
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle,
				"u_MVPMatrix");
		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle,
				"u_MVMatrix");
		mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle,
				"u_Texture");
		mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle,
				"a_Position");
		mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
		mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle,
				"a_TexCoordinate");
		ToolsUtil.checkGLError();
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

		final float left = -width / 2;
		final float right = width / 2;
		final float bottom = height / 2.0f;
		final float top = -height / 2.0f;
		final float near = -1.0f;
		final float far = 1.0f;

		Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
	}

	float mStep = 0.005f;
	float mV = 0;

	@Override
	public void onDrawFrame(GL10 glUnused) {
		String string  = GLES20.glGetString(GLES20.GL_EXTENSIONS);
		//GLES20.glDisable(cap)(GLES20.GL_TEXTURE_2D);
		ToolsUtil.checkGLError();
		if (true) {
			//if(true)
				//return;
			mV += mStep;
			if (mV > 1 || mV < 0) {
				mStep *= -1;
				mV += mStep;
			}
			GLES20.glClearColor(0, 0, 0, 0);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
					| GLES20.GL_DEPTH_BUFFER_BIT);

			// Do a complete rotation every 10 seconds.
			long time = SystemClock.uptimeMillis() % 10000L;
			float angleInDegrees = (360.0f / 10000.0f) * (2 * (int) time);

			// Set the active texture unit to texture unit 0.
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

			// Bind the texture to this unit.
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

			// Tell the texture uniform sampler to use this texture in the
			// shader by binding to texture unit 0.
			GLES20.glUniform1i(mTextureUniformHandle, 0);

			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.rotateM(mModelMatrix, 0, mV * 360, 0, 0, 0.5f);
			mCube.drawCube();

			return;
		}
		
	//	IntBuffer framebuffer = IntBuffer.allocate(1);
		int [] framebuffer = new int[1];
		IntBuffer depthRenderbuffer = IntBuffer.allocate(1);
		IntBuffer texture = IntBuffer.allocate(1);
		int texWidth = 521, texHeight = 512;
		IntBuffer maxRenderbufferSize = IntBuffer.allocate(1);
		GLES20.glGetIntegerv(GLES20.GL_MAX_RENDERBUFFER_SIZE,
				maxRenderbufferSize);
		// check if GL_MAX_RENDERBUFFER_SIZE is >= texWidth and texHeight
		if ((maxRenderbufferSize.get(0) <= texWidth)
				|| (maxRenderbufferSize.get(0) <= texHeight)) {
			// cannot use framebuffer objects as we need to create
			// a depth buffer as a renderbuffer object
			// return with appropriate error
		}
		// generate the framebuffer, renderbuffer, and texture object names
		GLES20.glGenFramebuffers(1, framebuffer, 0);
		GLES20.glGenRenderbuffers(1, depthRenderbuffer);
		GLES20.glGenTextures(1, texture);
		// bind texture and load the texture mip-level 0
		// texels are RGB565
		// no texels need to be specified as we are going to draw into
		// the texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0));
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB, texWidth,
				texHeight, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_SHORT_5_6_5,
				null);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		// bind renderbuffer and create a 16-bit depth buffer
		// width and height of renderbuffer = width and height of
		// the texture
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER,
				depthRenderbuffer.get(0));
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
				GLES20.GL_DEPTH_COMPONENT16, texWidth, texHeight);
		// bind the framebuffer
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebuffer[0]);
		// specify texture as color attachment
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D,
				texture.get(0), 0);
		// specify depth_renderbufer as depth attachment
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER,
				depthRenderbuffer.get(0));
		// check for framebuffer complete
		int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
		if (status == GLES20.GL_FRAMEBUFFER_COMPLETE) {
			// render to texture using FBO
			GLES20.glClearColor(1, 1, 1, 1);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
					| GLES20.GL_DEPTH_BUFFER_BIT);

			// Do a complete rotation every 10 seconds.
			long time = SystemClock.uptimeMillis() % 10000L;
			float angleInDegrees = (360.0f / 10000.0f) * (2 * (int) time);

			// Set the active texture unit to texture unit 0.
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

			// Bind the texture to this unit.
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

			// Tell the texture uniform sampler to use this texture in the
			// shader by binding to texture unit 0.
			GLES20.glUniform1i(mTextureUniformHandle, 0);

			Matrix.setIdentityM(mModelMatrix, 0);
			// Matrix.translateM(mModelMatrix, 0, 0.0f, -1.0f, -5.0f);
			Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 0, 0, 0.5f);
			mCube.drawCube();

			// render to window system provided framebuffer
			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0);
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT
					| GLES20.GL_DEPTH_BUFFER_BIT);

			// Do a complete rotation every 10 seconds.
			time = SystemClock.uptimeMillis() % 10000L;
			angleInDegrees = (360.0f / 10000.0f) * ((int) time);

			// Set the active texture unit to texture unit 0.
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

			// Bind the texture to this unit.
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.get(0)/* mTextureDataHandle */);

			// Tell the texture uniform sampler to use this texture in the
			// shader by binding to texture unit 0.
			GLES20.glUniform1i(mTextureUniformHandle, 0);

			Matrix.setIdentityM(mModelMatrix, 0);
			Matrix.translateM(mModelMatrix, 0, 0.0f, 0.0f, -.4f);
			// Matrix.rotateM(mModelMatrix, 0, angleInDegrees, 1.0f, 1.0f,
			// 0.0f);
			mTexture2D.drawCube();
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		}

		// cleanup
		GLES20.glDeleteRenderbuffers(1, depthRenderbuffer);
		GLES20.glDeleteFramebuffers(1, framebuffer, 0);
		GLES20.glDeleteTextures(1, texture);
	}

	@Override
	public int getVertexHandle() {
		return mPositionHandle;
	}

	@Override
	public int getTexCoordHandle() {
		return mTextureCoordinateHandle;
	}

	@Override
	public int getColorHandle() {
		return mColorHandle;
	}

	@Override
	public int getMVMatrixHandle() {
		return mMVMatrixHandle;
	}

	@Override
	public int getMVPMatrixHandle() {
		return mMVPMatrixHandle;
	}

	@Override
	public float[] getModelMatrix() {
		return mModelMatrix;
	}

	@Override
	public float[] getViewMatrix() {
		return mViewMatrix;
	}

	@Override
	public float[] getProjectionMatrix() {
		return mProjectionMatrix;
	}

	@Override
	public float[] getMVPMatrix() {
		return mMVPMatrix;
	}

}