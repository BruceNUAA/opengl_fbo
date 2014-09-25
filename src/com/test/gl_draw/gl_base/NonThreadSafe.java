package com.test.gl_draw.gl_base;

public abstract class NonThreadSafe {

	private Thread mThread = null;

	public NonThreadSafe() {
		ensureThreadIdAssigned();
	}

	abstract boolean isCheckEnable();

	public boolean BeforeThreadCall() {
		if (!isCheckEnable())
			return false;

		ensureThreadIdAssigned();
		if (mThread != Thread.currentThread()) {
			throw new RuntimeException("Call on invalided thread!");
		}

		return true;
	}

	public boolean AfterThreadCall() {
		return isCheckEnable();
	}

	public void detachFromThread() {
		mThread = null;
	}

	private void ensureThreadIdAssigned() {
		if (!isCheckEnable())
			return;

		if (this.mThread == null) {
			this.mThread = Thread.currentThread();
		}
	}
}
