package org.tbe.jug.demo;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Stack;

public class MethodChain {

	private final Stack<Method> methods;

	public MethodChain(List<Method> methods) {
		super();
		this.methods = new Stack<Method>();
		this.methods.addAll(methods);
	}

	public MethodChain() {
		super();
		this.methods = new Stack<Method>();
	}

	public void addMethod(Method m) {
		this.methods.add(m);
	}

	public MethodChain(Method method) {
		super();
		this.methods = new Stack<Method>();
		this.methods.add(method);
	}

	public Object invokeChain(Object invoker) {
		Object result = null;
		if (methods.size() == 0) {
			throw new IllegalArgumentException("No methods in chain");
		}
		for (Method m : methods) {
			try {
				result = m.invoke(invoker);
				invoker = result;
			} catch (Exception e) {
				throw new IllegalArgumentException("Cannot Invoke getMethod ! ", e);
			}
		}
		return result;
	}

	public Method getTop() {
		return methods.peek();
	}
}