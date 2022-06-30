package com.javatao.rest.client.proxy;

import java.lang.reflect.Method;
import java.util.Map;

public class DoAdapter implements DoInterceptors {
	@Override
	public Object before(Object proxy, Method method, Map<String, Object> datas) {
		return null;
	}

	@Override
	public Object after(Object proxy, Method method, String resutl, Map<String, String> headers) {
		return null;
	}

	@Override
	public Object finalReturn(Object result, Object proxy, Method method, Object[] args) {
		return result;
	}
}
