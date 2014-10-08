package com.test.gl_draw.gl_base;

import java.util.Stack;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.example.gl20.ToolsUtil;
import com.test.gl_draw.data.GLBitmapLoader;
import com.test.gl_draw.utils.GLHelper20;

public class GLShadeManager extends GLObject {

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
	
	private int mUseTextureHandle;
	
	private Stack<float[]> mMatrixStack = new Stack<float[]>();

	private static GLShadeManager sGlShadeManager = null;

	public static GLShadeManager getInstance() {
		if (sGlShadeManager == null) {
			sGlShadeManager = new GLShadeManager();
		}
		return sGlShadeManager;
	}

	private GLShadeManager() {
	}

	public void SceneCreate(GL10 gl) {
		BeforeThreadCall();

		Matrix.setIdentityM(mViewMatrix, 0);

		String vertexShader = GLBitmapLoader.getInstance().getVertexShader();
		String fragmentShader = GLBitmapLoader.getInstance()
				.getFragmentShader();

		int vertexShaderHandle = GLHelper20.compileShader(
				GLES20.GL_VERTEX_SHADER, vertexShader);
		int fragmentShaderHandle = GLHelper20.compileShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentShader);

		mProgramHandle = GLHelper20.createAndLinkProgram(vertexShaderHandle,
				fragmentShaderHandle, new String[] { "a_Position", "a_Color",
						"a_TexCoordinate" });

		GLES20.glUseProgram(mProgramHandle);

		// Set program handles for cube drawing.
		mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle,
				"u_MVPMatrix");

		mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle,
				"u_MVMatrix");
		mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle,
				"u_Texture");
		
		mUseTextureHandle = GLES20.glGetUniformLocation(mProgramHandle,
				"u_use_texture");
		
		mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle,
				"a_Position");
		mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
		mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle,
				"a_TexCoordinate");

		AfterThreadCall();
	}

	public void SceneChange(GL10 gl, int w, int h) {
		BeforeThreadCall();

		GLES20.glViewport(0, 0, w, h);

		float left = 0;
		float right = w;
		float bottom = h;
		float top = 0;
		float near = -1.0f;
		float far = 1.0f;
		Matrix.setIdentityM(mProjectionMatrix, 0);
		
		Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

		GLES20.glEnable(GL10.GL_BLEND);
		// src alpha在glsl里重新计算
		GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		AfterThreadCall();
	}

	public void SceneFrameCome(GL10 gl) {

		Matrix.setIdentityM(mModelMatrix, 0);
		GLES20.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}

	public int getVertexHandle() {
		return mPositionHandle;
	}

	public int getTextureUniformHandle() {
		return mTextureUniformHandle;
	}

	public int getTexCoordHandle() {
		return mTextureCoordinateHandle;
	}

	public int getColorHandle() {
		return mColorHandle;
	}

	public int getMVMatrixHandle() {
		return mMVMatrixHandle;
	}

	public int getMVPMatrixHandle() {
		return mMVPMatrixHandle;
	}

	public float[] getModelMatrix() {
		return mModelMatrix;
	}

	public float[] getViewMatrix() {
		return mViewMatrix;
	}

	public float[] getProjectionMatrix() {
		return mProjectionMatrix;
	}

	public float[] getMVPMatrix() {
		return mMVPMatrix;
	}

	public void PushMatrix() {
		mMatrixStack.push(mModelMatrix.clone());
	}

	public void PopMatrix() {
		mModelMatrix = mMatrixStack.pop().clone();
	}
	
	public void SetHasTexture(boolean has_texture) {
		GLES20.glUniform1f(mUseTextureHandle, has_texture ? 1 : 0);
	}
}
