package com.test.gl_draw.gl_base;

import java.util.ArrayList;
import java.util.List;

import com.test.gl_draw.utils.GLHelper20;

public class GLObject extends NonThreadSafe {

	// 确保BeforeThreadCall和AfterThreadCall成对调用
	private List<Integer> mCallStackTrace = new ArrayList<Integer>();
	
	@Override
	public boolean isDebugEnable() {
		return GLConfigure.getInstance().enableDebug();
	}
	
	@Override
	public boolean BeforeThreadCall() {
		if (!super.BeforeThreadCall())
			return false;

		checkCallBefore();
		return true;
	}

	@Override
	public boolean AfterThreadCall() {
		if (!super.AfterThreadCall())
			return false;

		checkCallAfter();

		GLHelper20.checkGLError();

		return true;
	}

	private void checkCallBefore() {
		int call_deep = Thread.currentThread().getStackTrace().length;
		int n = mCallStackTrace.size();

		if (n > 0) {
			int last_call_deep = mCallStackTrace.get(n - 1);
			if (last_call_deep >= call_deep) {
				throwException();
			}
		}

		mCallStackTrace.add(call_deep);
	}

	private void checkCallAfter() {
		int call_deep = Thread.currentThread().getStackTrace().length;

		int n = mCallStackTrace.size();

		do {
			if (n > 0) {
				int last_call_deep = mCallStackTrace.get(n - 1);
				if (last_call_deep == call_deep) {
					break;
				}
			}

			throwException();

		} while (false);

		mCallStackTrace.remove(n - 1);
	}

	private void throwException() {
		throw new RuntimeException("BeforeThreadCall和AfterThreadCall应该成对调用!");
	}
}
