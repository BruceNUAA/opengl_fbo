package com.test.gl_draw.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;

import com.example.gl_fbo.R;
import com.test.gl_draw.KApplication;
import com.test.gl_draw.gl_base.Texture;
import com.test.gl_draw.glview.GLDragView;
import com.test.gl_draw.glview.GLRotateView;
import com.test.gl_draw.glview.GLTextureView;
import com.test.gl_draw.glview.GLView;
import com.test.gl_draw.igl_draw.IGLView;
import com.test.gl_draw.utils.helper.DebugToast;
import com.test.gl_draw.utils.helper.ThreadUtils;

public class GLViewTest {
	public static void test(GLView root_view) {
		GLView view = new GLNinePatchDrawTest();
		view.SetBackgound(0x5fffffff);
		view.SetBounds(new RectF(100, 100, GLView.sRenderWidth - 100,
				GLView.sRenderHeight - 100));

		root_view.AddView(view);
	}

	public static void test1(GLView root_view) {
		IGLView.OnTouchListener touch = new IGLView.OnTouchListener() {
			@Override
			public boolean OnClick(final IGLView v) {
				ThreadUtils.postOnUiThread(new Runnable() {

					@Override
					public void run() {
						long c = System.currentTimeMillis() % 1000;

						DebugToast.showLong(KApplication.sApplication, v
								.getClass().getSimpleName()
								+ ":"
								+ v.id()
								+ ":\tTime:" + c);

					}
				});
				return true;
			}
		};

		float w = GLView.sRenderWidth;
		float h = GLView.sRenderHeight;

		GLView view = new GLDragViewTest();
		view.setOnTouchLisener(touch);
		view.SetBounds(new RectF(w * 0.1f, h * 0.1f, w * 0.9f, h * 0.9f));
		view.SetBackgound(0x3fff0000);
		root_view.AddView(view);

		Context cx = KApplication.sApplication;
		Bitmap b = null;
		Texture texture = null;
		{

			b = BitmapFactory.decodeResource(cx.getResources(),
					R.drawable.port_img);
			texture = new Texture();
			texture.Init(b);
			b.recycle();

			GLRotateView view6 = new GLRotateViewTest();

			view6.SetTexture(texture, true);
			view6.setOnTouchLisener(touch);
			RectF bF = new RectF(0, 0, view.Bounds().width(), view.Bounds()
					.height());
			bF.inset(w * 0.2f, h * 0.2f);

			view6.setRotateOrigin(bF.centerX(), GLView.sRenderHeight);

			view6.SetBounds(bF);

			view6.SetBackgound(0xa500ffff);
			view.AddView(view6);

			GLZoomView view2 = new GLZoomView();

			float rw = Math.min(w, h) * 0.2f;
			RectF rc1 = new RectF((w - rw) / 2, h - 2 * rw, (w + rw) / 2, h
					- rw);
			RectF rc2 = new RectF(0, 0, w, h);
			view2.SetZoomRect(rc1, rc2);
			view2.SetBounds(rc1);
			// view2.SetBackgound(0xff00ffff);

			root_view.AddView(view2);
		}
	}

	public static void test2(GLView root_view) {
		IGLView.OnTouchListener touch = new IGLView.OnTouchListener() {
			@Override
			public boolean OnClick(IGLView v) {
				// DLog.e("Test", "Touch ID = " + v.id());
				return true;
			}
		};

		GLView view = new GLDragView();
		view.setOnTouchLisener(touch);
		view.SetBounds(new RectF(GLView.sRenderWidth * 0.1f,
				GLView.sRenderHeight * 0.1f, GLView.sRenderWidth * 0.9f,
				GLView.sRenderHeight * 0.9f));
		view.SetBackgound(0x3fff0000);
		root_view.AddView(view);

		Context cx = KApplication.sApplication;
		Bitmap b = null;
		Texture texture = null;
		{

			b = BitmapFactory.decodeResource(cx.getResources(), R.drawable.img);
			GLTextureView view5 = new GLTextureView();
			texture = new Texture();
			texture.Init(b);
			b.recycle();
			view5.SetTexture(texture, true);
			view5.setOnTouchLisener(touch);
			view5.SetBounds(new RectF(0.0f, 00, 300.0f, 450.0f));
			view5.SetBackgound(0x2fffffff);
			view.AddView(view5);
		}

		{
			b = BitmapFactory.decodeResource(cx.getResources(),
					R.drawable.port_img);
			texture = new Texture();
			// texture2.Init("HELLO", false, 50);
			texture.Init(b);
			b.recycle();
			GLRotateView view6 = new GLRotateView();
			view6.setRotateOrigin(450, 300);
			view6.setRotateDegree(10);
			view6.SetTexture(texture, true);
			view6.setOnTouchLisener(touch);
			view6.SetBounds(new RectF(340.0f, 00, 670.0f, 450.0f));
			view6.SetBackgound(0x2fffffff);
			view.AddView(view6);
		}

		GLTextureView view2 = new GLTextureView();
		texture = new Texture();
		texture.Init("ID = " + view2.id(), Color.RED, 50);
		view2.SetTexture(texture, true);
		view2.setOnTouchLisener(touch);
		view2.SetBounds(new RectF(30.0f, 15, 300.0f, 500.0f));
		view2.SetBackgound(0xff0000ff);
		view.AddView(view2);

		GLTextureView view3 = new GLTextureView();
		texture = new Texture();
		texture.Init("ID = " + view3.id(), Color.RED, 50);
		view3.SetTexture(texture, true);
		view3.setOnTouchLisener(touch);
		view3.SetBounds(new RectF(00.0f, 0, 300.0f, 200.0f));
		view3.SetBackgound(0x5fffff00);
		view.AddView(view3);

		GLTextureView view4 = new GLTextureView();
		texture = new Texture();
		texture.Init("ID = " + view4.id(), Color.RED, 50);
		view4.SetTexture(texture, true);
		view4.setOnTouchLisener(touch);
		view4.SetBounds(new RectF(30.0f, 150, 300.0f, 500.0f));
		view4.SetBackgound(0xffff0000);
		view3.AddView(view4);
	}
}
