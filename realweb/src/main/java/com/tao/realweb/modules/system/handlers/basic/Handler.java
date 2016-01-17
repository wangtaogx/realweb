package com.tao.realweb.modules.system.handlers.basic;

import com.tao.realweb.bean.Packet;
import com.tao.realweb.modules.system.handlers.HandlerManager;

public interface Handler {
	
	public Packet processPacket(Packet iq);
	public void init(HandlerManager handlerManager,HandlerInfo info);
	public HandlerInfo getHandlerInfo();

}
