package com.tao.realweb.modules.system.handlers.basic;

import com.tao.realweb.bean.IQ;
import com.tao.realweb.container.RealWebServer;
import com.tao.realweb.modules.system.handlers.HandlerManager;

public abstract  class AbstractHandler implements Handler {

	protected HandlerManager handlerManager = null;
	protected HandlerInfo handlerInfo = null;
	
	public void init(HandlerManager handlerManager,HandlerInfo info) {
		this.handlerManager = handlerManager;
		this.handlerInfo = info;
	}

	
	public IQ processPacket(IQ iq) {
		return IQ.createResultIQ(iq);
	}

	public HandlerInfo getHandlerInfo() {
		return handlerInfo;
	}

	public void setHandlerInfo(HandlerInfo handlerInfo) {
		this.handlerInfo = handlerInfo;
	}


	public HandlerManager getHandlerManager() {
		return handlerManager;
	}


	public void setHandlerManager(HandlerManager handlerManager) {
		this.handlerManager = handlerManager;
	}
	

}
