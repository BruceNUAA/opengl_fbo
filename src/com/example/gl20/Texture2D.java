package com.example.gl20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Texture2D {
	/** Store our model data in a float buffer. */
	private final FloatBuffer mCubePositions;
	private final FloatBuffer mCubeColors;
	private final FloatBuffer mCubeTextureCoordinates;

	private IShadeManager mIShadeMgr;
	private int mWidth = 500;
	private int mHeight = 500;

	public Texture2D(IShadeManager shade_mgr) {
		mIShadeMgr = shade_mgr;

		float x = -mWidth / 2.0f;
		float y = -mHeight / 2.0f;

		float[] cubePositionData = {
				//
				x, y, 0,//
				x + mWidth, y + 0, 0,//
				x + 0, y + mHeight, 0,//
				x + mWidth, y + mHeight, 0, };
		// R, G, B, A
		final float[] cubeColorData = {
				//
				1.0f, 0.0f, 0.0f, 1.0f,//
				1.0f, 1.0f, 0.0f, 1.0f,//
				1.0f, 0.0f, 1.0f, 1.0f,//
				1.0f, 0.0f, 0.0f, 1.0f,//

		};

		final float[] cubeTextureCoordinateData = {
				//
				0.0f, 0.0f, //
				1.0f, .0f, //
				0.0f, 1.0f, //
				1.0f, 1.0f, };

		// Initialize the buffers.
		mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubePositions.put(cubePositionData).position(0);

		mCubeColors = ByteBuffer.allocateDirect(cubeColorData.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubeColors.put(cubeColorData).position(0);

		mCubeTextureCoordinates = ByteBuffer
				.allocateDirect(cubeTextureCoordinateData.length * 4)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

	}

	/**
	 * Draws a cube.
	 */
	public void drawCube() {
		ToolsUtil.checkGLError();
		// Pass in the position information
		mCubePositions.position(0);
		GLES20.glVertexAttribPointer(mIShadeMgr.getVertexHandle(), 3,
				GLES20.GL_FLOAT, false, 0, mCubePositions);

		GLES20.glEnableVertexAttribArray(mIShadeMgr.getVertexHandle());

		// Pass in the color information
		mCubeColors.position(0);
		GLES20.glVertexAttribPointer(mIShadeMgr.getColorHandle(), 4,
				GLES20.GL_FLOAT, false, 0, mCubeColors);
		GLES20.glEnableVertexAttribArray(mIShadeMgr.getColorHandle());

		// Pass in the texture coordinate information
		mCubeTextureCoordinates.position(0);
		GLES20.glVertexAttribPointer(mIShadeMgr.getTexCoordHandle(), 2,
				GLES20.GL_FLOAT, false, 0, mCubeTextureCoordinates);

		GLES20.glEnableVertexAttribArray(mIShadeMgr.getTexCoordHandle());

		// This multiplies the view matrix by the model matrix, and stores the
		// result in the MVP matrix
		// (which currently contains model * view).
		Matrix.multiplyMM(mIShadeMgr.getMVPMatrix(), 0,
				mIShadeMgr.getViewMatrix(), 0, mIShadeMgr.getModelMatrix(), 0);

		// Pass in the modelview matrix.
		GLES20.glUniformMatrix4fv(mIShadeMgr.getMVMatrixHandle(), 1, false,
				mIShadeMgr.getMVPMatrix(), 0);

		// This multiplies the modelview matrix by the projection matrix, and
		// stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(mIShadeMgr.getMVPMatrix(), 0,
				mIShadeMgr.getProjectionMatrix(), 0, mIShadeMgr.getMVPMatrix(),
				0);

		// Pass in the combined matrix.
		GLES20.glUniformMatrix4fv(mIShadeMgr.getMVPMatrixHandle(), 1, false,
				mIShadeMgr.getMVPMatrix(), 0);

		// Draw the cube.
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	}
}
