package com.tao.realweb.modules.system.handlers.basic;

public interface HandlerListener {

	public void handlerAdded(String namespace);
	public void handlerRemoved(String namespace);
	public void handlerReloadToCache();
}
