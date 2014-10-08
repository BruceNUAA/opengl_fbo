package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;
import android.opengl.Matrix;

import com.test.gl_draw.gl_base.GLClipManager;
import com.test.gl_draw.gl_base.GLShadeManager;

public class GLRotateView extends GLTextureView {

	private float mRotateDegree = 0;

	private float[] mRotateOrigin = new float[2];

	private float[] mOrigin = new float[2];

	private int mDrawInRotateModeC = 0;

	private float[] mGLMatrix = new float[16];

	public void setRotateDegree(float d) {
		mRotateDegree = d;
		InValidate();
	}

	public void setRotateOrigin(float... rotate_orgin) {
		if (rotate_orgin.length < 2)
			return;

		mRotateOrigin[0] = rotate_orgin[0];
		mRotateOrigin[1] = rotate_orgin[1];
		InValidate();
	}
	
	public float getRotateDegree() {
		return mRotateDegree;
	}

	public float[] getOrigin() {
		return mOrigin;
	}

	public void setOrigin(float... orgin) {
		if (orgin.length < 2)
			return;

		mOrigin[0] = orgin[0];
		mOrigin[1] = orgin[1];
	}
	
	@Override
	public void SetBounds(RectF rc) {
		super.SetBounds(rc);

		getDraw().SetRenderRect(rc);
		getBackgoundDraw().SetRenderRect(rc);
	}

	@Override
	public void AddView(GLView view) {
		throw new RuntimeException();
	}

	@Override
	public void RemoveView(GLView view) {
		throw new RuntimeException();
	}

	@Override
	public boolean HitTest(final float x, final float y) {
		return HitTestPoint(x, y, Bounds());
	}

	public void SetRotateEven(GL10 gl) {
		BeforeThreadCall();
		
		if (mDrawInRotateModeC == 1) {
			RestoreRotateEven(gl);
		} else if (mDrawInRotateModeC != 0) {
			throw new RuntimeException(
					"SetRotateEven and RestoreRotateEven should be used Pairly!");
		}
	
		float[] model_matrix = GLShadeManager.getInstance().getModelMatrix();
		
		GLShadeManager.getInstance().PushMatrix();
		Matrix.translateM(model_matrix, 0, mRotateOrigin[0], mRotateOrigin[1], 0);
		Matrix.rotateM(model_matrix, 0, mRotateDegree, 0, 0, 1);
		Matrix.translateM(model_matrix, 0, mOrigin[0] - mRotateOrigin[0], mOrigin[1]
				- mRotateOrigin[1], 0);
		
		mGLMatrix = model_matrix.clone();
		
		mDrawInRotateModeC++;
		
		AfterThreadCall();
	}

	public void RestoreRotateEven(GL10 gl) {
		BeforeThreadCall();
		
		if (mDrawInRotateModeC == 1) {
			GLShadeManager.getInstance().PopMatrix();
			mDrawInRotateModeC--;
		} else {
			throw new RuntimeException(
					"SetRotateEven and RestoreRotateEven should be used Pairly!");
		}
		
		AfterThreadCall();
	}

	public void setDrawClipBound(GL10 gl) {
		GLShadeManager.getInstance().PushMatrix();

		if (Parent() == null)
			return;

		RectF r = Parent().ClipBoundForChildren();

		GLClipManager.getInstance().ClipRect(gl, r);
	}

	public void restoreDrawClipBound(GL10 gl) {
		GLClipManager.getInstance().DisableClip(gl);
		GLShadeManager.getInstance().PopMatrix();
	}

	// 绘制
	@Override
	public void Draw(GL10 gl) {
		BeforeThreadCall();
		
		setDrawClipBound(gl);

		SetRotateEven(gl);

		OnDrawBackgound(gl);

		OnDraw(gl);

		RestoreRotateEven(gl);

		restoreDrawClipBound(gl);
		
		AfterThreadCall();
	}

	public boolean isPtInRegin(float x, float y, RectF rc) {
		return HitTestPoint(x, y, rc);
	}

	private boolean HitTestPoint(float x, float y, RectF rc) {
		float[] pt_v = { x, y };
		PtInRender(pt_v);

		float[][] points = {
				//
				{ rc.left, rc.top, 0, 1 },//
				{ rc.right, rc.top, 0, 1 },//
				{ rc.right, rc.bottom, 0, 1 },//
				{ rc.left, rc.bottom, 0, 1 },//
		};

		for (int i = 0; i < points.length; i++) {
			Matrix.multiplyMV(points[i], 0, mGLMatrix, 0,
					points[i], 0);
		}

		return Hittest(pt_v, points);
	}

	//
	// 0------------------1
	// --------------------
	// --------------------
	// --------------------
	// 3------------------2
	//
	private boolean Hittest(float[] point, float[][] rect_pts) {
		if (point.length < 2 || rect_pts.length < 4)
			return false;

		double tan_angle = -(rect_pts[2][1] - rect_pts[3][1])
				/ (rect_pts[2][0] - rect_pts[3][0]);
		double cos = 1 / Math.sqrt(1 + tan_angle * tan_angle);
		double sin = tan_angle / Math.sqrt(1 + tan_angle * tan_angle);

		double w = Math.sqrt((rect_pts[1][0] - rect_pts[0][0])
				* (rect_pts[1][0] - rect_pts[0][0])
				+ (rect_pts[1][1] - rect_pts[0][1])
				* (rect_pts[1][1] - rect_pts[0][1]));
		double h = Math.sqrt((rect_pts[3][0] - rect_pts[0][0])
				* (rect_pts[3][0] - rect_pts[0][0])
				+ (rect_pts[3][1] - rect_pts[0][1])
				* (rect_pts[3][1] - rect_pts[0][1]));

		point[0] -= rect_pts[3][0];
		point[1] -= rect_pts[3][1];

		double x = point[0] * cos - point[1] * sin;
		double y = point[0] * sin + point[1] * cos;

		return x >= 0 && x <= w && y >= -h && y <= 0;
	}
}
