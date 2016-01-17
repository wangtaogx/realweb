package com.tao.realweb.modules.system.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.bean.PacketError;
import com.tao.realweb.modules.basic.AbstractModule;
import com.tao.realweb.modules.basic.ModuleInfo;
import com.tao.realweb.modules.basic.ModuleManager;
import com.tao.realweb.modules.system.handlers.HandlerManager;
import com.tao.realweb.modules.system.handlers.basic.Handler;
import com.tao.realweb.modules.system.session.SessionManager;

public class PacketRoutingManager extends AbstractModule {

	public static final String MODULE_NAME = "PacketRoutingManager";
	private Logger logger = LoggerFactory.getLogger(PacketRoutingManager.class);
	private SessionManager sessionManager;
	private HandlerManager handlerManager ;
	public PacketRoutingManager(){
	}
	public void routePacket(Packet packet){
		if(isRequest(packet)){
			Handler handler = handlerManager.getHandler(packet.getNamespace());
			if(handler != null){
				Packet result = handler.processPacket(packet);
				sessionManager.sendPacket(result);
			}else{
				routeError(packet,PacketError.HANDLER_NOT_FIND);
			}
		}else{
			logger.debug("send to local :"+JSON.toJSONString(packet));
			sessionManager.sendPacket(packet);
		}
	}
	private boolean isRequest(Packet iq){
		if (Packet.ACTION_READ.equals(iq.getAction())){
			return true;
		}else
			return false;
	}
	private void routeError(Packet packet,PacketError error) {
		Packet response = Packet.createErrorResponse(packet,error );
		sessionManager.sendPacket(response);
	}
	public void init(ModuleManager moduleManager,ModuleInfo info) {
		super.init(moduleManager, info);
		sessionManager = moduleManager.getSessionManager();
		handlerManager = moduleManager.getHandlerManager();
	}
	public void start() {
		
	}
	public void stop() {
		this.moduleManager.removeModule(this);
	}
}
