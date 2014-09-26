package com.test.gl_draw.gl_base;

import java.util.ArrayList;
import java.util.List;

import android.util.Pair;

import com.test.gl_draw.utils.GLHelper20;

public class GLObject extends NonThreadSafe {

	// 确保BeforeThreadCall和AfterThreadCall成对调用
	private List<Pair<Integer, String>> mCallStackTrace = new ArrayList<Pair<Integer, String>>();

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
		StackTraceElement[] call_stack = Thread.currentThread().getStackTrace();

		final int inspact_place_deep = 5;

		if (call_stack == null || call_stack.length < inspact_place_deep)
			return;

		int call_deep = call_stack.length;

		int n = mCallStackTrace.size();

		if (n > 0) {
			Pair<Integer, String> last_call = mCallStackTrace.get(n - 1);
			if (last_call.first >= call_deep) {
				throwException(last_call.second);
			}
		}

		StackTraceElement inspact_place = call_stack[inspact_place_deep - 1];

		StringBuilder info = new StringBuilder();
		info.append(inspact_place.getClassName());
		info.append(":");
		info.append(inspact_place.getMethodName());
		info.append("(");
		info.append(inspact_place.getLineNumber());
		info.append("）");

		mCallStackTrace.add(new Pair<Integer, String>(call_deep, info
				.toString()));
	}

	private void checkCallAfter() {
		StackTraceElement[] call_stack = Thread.currentThread().getStackTrace();

		final int inspact_place_deep = 5;

		if (call_stack == null || call_stack.length < inspact_place_deep)
			return;

		int call_deep = call_stack.length;

		int n = mCallStackTrace.size();

		do {
			String error_info = null;
			
			if (n > 0) {
				Pair<Integer, String> last_call = mCallStackTrace.get(n - 1);
				if (last_call.first != call_deep) {
					error_info = last_call.second;
				} else {
					break;
				}
			} else {
				StackTraceElement inspact_place = call_stack[inspact_place_deep - 1];

				StringBuilder info = new StringBuilder();
				info.append(inspact_place.getClassName());
				info.append(":");
				info.append(inspact_place.getMethodName());
				info.append("(");
				info.append(inspact_place.getLineNumber());
				info.append("）");
				
				error_info = info.toString();
			}
			
			throwException(error_info);
			
		} while(false);
		
		mCallStackTrace.remove(n - 1);
	}

	private void throwException(String last_call_place) {
		StringBuffer b = new StringBuffer();
		b.append("\nFunc:[BeforeThreadCall] and Func:[AfterThreadCall] should be call pairly!");
		b.append("\nLast call place: ");
		b.append(last_call_place);
		throw new RuntimeException(b.toString());
	}
}
