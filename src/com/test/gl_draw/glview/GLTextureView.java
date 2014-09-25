package com.test.gl_draw.glview;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.test.gl_draw.gl_base.Texture;

public class GLTextureView extends GLView {
	private TextureDraw mDraw = new TextureDraw();

	public void SetTexture(Texture texture, boolean destory_texture_when_detach) {
		mDraw.SetTexture(texture, destory_texture_when_detach);
	}

	public void SetFillMode(TextureDraw.FillMode mode) {
		mDraw.SetFillMode(mode);
	}
	
	public void SetColor(int... color) {
	    mDraw.SetColor(color);
	}
	
	public TextureDraw getDraw() {
		return mDraw;
	}

	  @Override
	    public float[] getPreferSize() {
	      if (mDraw == null || mDraw.getTexture() == null) {
	          return super.getPreferSize();
	      } else {
	          int[] size = mDraw.getTexture().getTextSize();
	          return new float[] {size[0], size[1]};
	      }
	  }
	  
	@Override
	public void SetBounds(RectF rc) {
		super.SetBounds(rc);
		mDraw.SetRenderRect(rc);
	}
	
	@Override
    public void SetAlpha(float alpha) {
	    mDraw.SetAlpha(alpha);
    }

	@Override
	public void OnDraw(GL10 gl) {
		mDraw.Draw(gl);
	}
	
	@Override
	public void Detach() {
		super.Detach();
		mDraw.DetachFromView();
	}
	
	@Override
	public void detachFromThread() {
        super.detachFromThread();
        mDraw.detachFromThread();
    }
}
