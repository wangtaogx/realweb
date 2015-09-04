package com.tao.realweb.modules.system.interceptors.basic;

import com.tao.realweb.bean.Packet;
import com.tao.realweb.modules.system.interceptors.InterceptorManager;

public interface Interceptor {

	public void decode(Packet packet);
	public void encode(Packet packet);
	public void init(InterceptorManager interceptorManager,InterceptorInfo info);
}
