package com.test.gl_draw.gl_base;

public abstract class NonThreadSafe {

	private Thread mThread = null;

	public NonThreadSafe() {
		ensureThreadIdAssigned();
	}

	abstract boolean isDebugEnable();

	public boolean BeforeThreadCall() {
		if (!isDebugEnable())
			return false;

		ensureThreadIdAssigned();
		if (mThread != Thread.currentThread()) {
			throw new RuntimeException("Call on invalided thread!");
		}

		return true;
	}

	public boolean AfterThreadCall() {
		return isDebugEnable();
	}

	public void detachFromThread() {
		mThread = null;
	}

	private void ensureThreadIdAssigned() {
		if (!isDebugEnable())
			return;

		if (this.mThread == null) {
			this.mThread = Thread.currentThread();
		}
	}
}
