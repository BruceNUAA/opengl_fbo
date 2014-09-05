package com.example.gl20;


public interface IShadeManager {
	int getVertexHandle();

	int getTexCoordHandle();

	int getColorHandle();
	
	int getMVMatrixHandle();
	
	int getMVPMatrixHandle();

	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	float[] getModelMatrix();

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix
	 * transforms world space to eye space; it positions things relative to our
	 * eye.
	 */
	float[] getViewMatrix();

	/**
	 * Store the projection matrix. This is used to project the scene onto a 2D
	 * viewport.
	 */
	float[] getProjectionMatrix();

	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	float[] getMVPMatrix();

}
