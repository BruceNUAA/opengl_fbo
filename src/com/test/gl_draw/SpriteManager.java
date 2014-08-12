package com.test.gl_draw;

import java.util.ArrayList;
import java.util.Iterator;

public class SpriteManager implements Iterable<ISprite> {
	private ArrayList<ISprite> mSprites = new ArrayList<ISprite>();

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

	public int count() {
		return mSprites.size();
	}

	@Override
	public Iterator<ISprite> iterator() {
		return mSprites.iterator();
	}
}
