package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.R.bool;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.Log;

import com.test.gl_draw.GLUIView;
import com.test.gl_draw.KApplication;
import com.test.gl_draw.igl_draw.IGLView;
import com.test.gl_draw.utils.CustomToast;

public class GLRotateView extends GLTextureView {

	private float mRotateDegree = 0;

	private float[] mRotateOrigin = new float[2];

	private float[] mOrigin = new float[2];

	private int mDrawInRotateModeC = 0;

	private Matrix mDisplayMatrix = new Matrix();

	public void setRotateDegree(float d) {
		mRotateDegree = d;
	}

	public void setRotateOrigin(float... rotate_orgin) {
		if (rotate_orgin.length < 2)
			return;

		mRotateOrigin[0] = rotate_orgin[0];
		mRotateOrigin[1] = rotate_orgin[1];
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

	public float calcDegree() {
		return (float) (Math
				.asin((Bounds().centerX() - GLView.sRenderWidth / 2)
						/ mRotateOrigin[1])
				/ Math.PI * 180*2);
	}

	@Override
	public void SetBounds(RectF rc) {
		super.SetBounds(rc);

		setRotateDegree(calcDegree());

		getDraw().SetRenderRect(rc);
		getBackgoundDraw().SetRenderRect(rc);
	}

	@Override
	public void AddView(IGLView view) {
		throw new RuntimeException();
	}

	@Override
	public void RemoveView(IGLView view) {
		throw new RuntimeException();
	}

	public void SetRotateEven(GL10 gl) {
		if (mDrawInRotateModeC == 1) {
			RestoreRotateEven(gl);
		} else if (mDrawInRotateModeC != 0) {
			throw new RuntimeException(
					"SetRotateEven and RestoreRotateEven should be used Pairly!");
		}
		gl.glPushMatrix();
		gl.glTranslatef(mRotateOrigin[0], mRotateOrigin[1], 0);
		gl.glRotatef(mRotateDegree, 0, 0, 1);
		gl.glTranslatef(-mRotateOrigin[0], -mRotateOrigin[1], 0);

		gl.glTranslatef(mOrigin[0], mOrigin[1], 0);

		mDrawInRotateModeC++;
	}

	public void RestoreRotateEven(GL10 gl) {
		if (mDrawInRotateModeC == 1) {
			gl.glPopMatrix();
			mDrawInRotateModeC--;
		} else {
			throw new RuntimeException(
					"SetRotateEven and RestoreRotateEven should be used Pairly!");
		}
	}

	public void setDrawClipBound(GL10 gl) {
		gl.glPushMatrix();

		gl.glEnable(GL10.GL_SCISSOR_TEST);
		if (Parent() == null)
			return;

		RectF r = Parent().ClipBoundForChildren();

		gl.glScissor((int) r.left, sRenderHeight - (int) r.bottom,
				(int) r.width(), (int) r.height());
	}

	public void restoreDrawClipBound(GL10 gl) {
		gl.glDisable(GL10.GL_SCISSOR_TEST);
		gl.glPopMatrix();
	}

	// 绘制
	@Override
	public void Draw(GL10 gl) {

		setDrawClipBound(gl);

		SetRotateEven(gl);

		OnDrawBackgound(gl);

		OnDraw(gl);

		RestoreRotateEven(gl);

		restoreDrawClipBound(gl);
	}

	public boolean isPtInRegin(float x, float y) {
		// 统一坐标系
		float[] pt_v = { x - mOrigin[0], y - mOrigin[1] };

		float offset_y = -Bounds().bottom;

		float[] map_rect_pts = { 0, offset_y, Bounds().width(), offset_y,
				Bounds().width(), Bounds().height() + offset_y, 0,
				Bounds().height() + offset_y };

		mDisplayMatrix.mapPoints(map_rect_pts);

		return Hittest(pt_v, map_rect_pts);
	}

	private static boolean Hittest(float[] point, float[] rect_pts) {
		if (point.length < 2 || rect_pts.length < 8)
			return false;

		double tan_angle = -(rect_pts[5] - rect_pts[7])
				/ (rect_pts[4] - rect_pts[6]);
		double cos = 1 / Math.sqrt(1 + tan_angle * tan_angle);
		double sin = tan_angle / Math.sqrt(1 + tan_angle * tan_angle);

		double w = Math.sqrt((rect_pts[2] - rect_pts[0])
				* (rect_pts[2] - rect_pts[0]) + (rect_pts[3] - rect_pts[1])
				* (rect_pts[3] - rect_pts[1]));
		double h = Math.sqrt((rect_pts[6] - rect_pts[0])
				* (rect_pts[6] - rect_pts[0]) + (rect_pts[7] - rect_pts[1])
				* (rect_pts[7] - rect_pts[1]));

		point[0] -= rect_pts[6];
		point[1] -= rect_pts[7];

		double x = point[0] * cos - point[1] * sin;
		double y = point[0] * sin + point[1] * cos;

		return x >= 0 && x <= w && y >= -h && y <= 0;
	}
	
	@Override
    public boolean onDown(final float x, final float y) {
		final boolean sx = true;
	    GLUIView.sMultiWindowView.doUITask(new Runnable() {
			
			@Override
			public void run() {
				String string = Boolean.toString(sx) + "|" + Float.toString(x) + "-" + Float.toString(y);
				Log.e("---", string);
				CustomToast.showLong(KApplication.sApplication, string);
				
			}
		});
		return true;
	}
}
