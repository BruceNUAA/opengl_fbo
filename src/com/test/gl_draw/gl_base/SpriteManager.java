package com.test.gl_draw.gl_base;

import com.test.gl_draw.igl_draw.ISprite;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SpriteManager implements Iterable<ISprite> {
	private List<ISprite> mSprites = new CopyOnWriteArrayList<ISprite>();

	public SpriteManager() {
	}

	public void adddSprite(ISprite s) {
		if (!mSprites.contains(s)) {
			mSprites.add(s);
		}
	}

	public void removeItem(ISprite s) {
		if (mSprites.contains(s)) {
			mSprites.remove(s);
		}
	}
	
	public void clear() {
	    mSprites.clear();
	}

	public int count() {
		return mSprites.size();
	}

	@Override
	public Iterator<ISprite> iterator() {
		return mSprites.iterator();
	}
}
