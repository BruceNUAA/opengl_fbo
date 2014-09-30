package com.test.gl_draw.gl_base;

public abstract class NonThreadSafe {

	private Thread mThread = null;

	public NonThreadSafe() {
		ensureThreadIdAssigned();
	}

	abstract public boolean enableThreadCheck();

	public void ThreadCheck() {
		if (!enableThreadCheck())
			return;

		ensureThreadIdAssigned();
		if (mThread != Thread.currentThread()) {
			throw new RuntimeException("Call on invalided thread!");
		}
	}

	public void detachFromThread() {
		mThread = null;
	}

	private void ensureThreadIdAssigned() {
		if (!enableThreadCheck())
			return;

		if (this.mThread == null) {
			this.mThread = Thread.currentThread();
		}
	}
}
