package com.tao.realweb.modules.system.handlers.basic;

import com.tao.realweb.bean.IQ;
import com.tao.realweb.modules.system.handlers.HandlerManager;

public interface Handler {
	
	public IQ processPacket(IQ iq);
	public void init(HandlerManager handlerManager,HandlerInfo info);
	public HandlerInfo getHandlerInfo();

}
