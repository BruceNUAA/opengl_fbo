package com.example.gl20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class cube {
	/** Store our model data in a float buffer. */
	private final FloatBuffer mCubePositions;
	private final FloatBuffer mCubeColors;
	private final FloatBuffer mCubeTextureCoordinates;

	private IShadeManager mIShadeMgr;
	private int mWidth = 550;
	private int mHeight = 550;

	public cube(IShadeManager shade_mgr) {
		mIShadeMgr = shade_mgr;

		float x = 0;
		float y = 0;

		float[] cubePositionData = {
				//
				x, y, //
				x + mWidth, y + 0, //
				x + 0, y + mHeight, //
				x + mWidth, y + mHeight,  };
		// R, G, B, A
		final float[] cubeColorData = {
				//
			
				1.0f, 1.0f, 1.0f, .0f,//
				1.0f, 1.0f, 1.0f, 1.0f,//
				1.0f, 1.0f, 1.0f, 1.0f,//
				1.0f, 1.0f, 1.0f, 1.0f,//
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
		GLES20.glClearColor(10.0f, 0.0f,0.0f, 10.0f);
		GLES20.glEnable(GL10.GL_BLEND);
		GLES20.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		ToolsUtil.checkGLError();
		// Pass in the position information
		GLES20.glVertexAttribPointer(mIShadeMgr.getVertexHandle(), 2,
				GLES20.GL_FLOAT, false, 0, mCubePositions);

		GLES20.glEnableVertexAttribArray(mIShadeMgr.getVertexHandle());

		// Pass in the color information
		GLES20.glVertexAttribPointer(mIShadeMgr.getColorHandle(), 4,
				GLES20.GL_FLOAT, false, 0, mCubeColors);
		GLES20.glEnableVertexAttribArray(mIShadeMgr.getColorHandle());

		// Pass in the texture coordinate information
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
