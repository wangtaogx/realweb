package com.tao.realweb.modules.system.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.hazelcast.core.MessageListener;
import com.tao.realweb.bean.IQ;
import com.tao.realweb.bean.IQ.Type;
import com.tao.realweb.bean.JID;
import com.tao.realweb.bean.Message;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.bean.PacketError;
import com.tao.realweb.bean.Presence;
import com.tao.realweb.conf.system.RealWebConstants;
import com.tao.realweb.modules.basic.AbstractModule;
import com.tao.realweb.modules.basic.ModuleInfo;
import com.tao.realweb.modules.basic.ModuleManager;
import com.tao.realweb.modules.system.handlers.HandlerManager;
import com.tao.realweb.modules.system.handlers.basic.Handler;
import com.tao.realweb.modules.system.hazelcast.HazelCastManager;
import com.tao.realweb.modules.system.interceptors.InterceptorManager;
import com.tao.realweb.modules.system.session.SessionManager;

public class PacketRoutingManager extends AbstractModule {

	public static final String MODULE_NAME = "PacketRoutingManager";
	private Logger logger = LoggerFactory.getLogger(PacketRoutingManager.class);
	private SessionManager sessionManager;
	private HandlerManager handlerManager ;
	private HazelCastManager hazelcastManager = null;
	private InterceptorManager interceptorManager ;
	public PacketRoutingManager(){
	}
	public void routePacket(Packet packet){
		if(isToLocal(packet)){
			routeLocalPacket(packet);
		}else{
			routeRemotePacket(packet);
		}
	}
	/**
	 * 根据JID是否发往哪里
	 * @param jid
	 * @return
	 */
	private boolean isToLocal(Packet packet) {
		boolean result = true;
		if(packet instanceof Message || packet instanceof Presence){
			result = this.getModuleManager().getRealWebServer().getServerInfo().getHostname().equals(JID.formatServerName(packet.getTo()));
		}else if(packet instanceof IQ){
			IQ iq = (IQ)packet;
			if (isRequest(iq)) {
				result = (handlerManager.containsHandler(iq.getNamespace()) );
	        }
			else{
				result = true;
			}
		}
		return result;
	}
	private boolean isRequest(IQ iq){
		if ((iq.getType() == Type.GET || iq.getType() == Type.SET)) {
			return true;
		}else
			return false;
	}
	/**
	 * 发往本地用户
	 * @param packet
	 */
	public void routeLocalPacket(Packet packet){
		logger.debug("send to local :"+JSON.toJSONString(packet));
		if(packet instanceof Message){
			this.interceptorManager.fireGlobalEncode(packet);
			this.interceptorManager.fireUserEncode(packet);
			sessionManager.sendPacket(packet);
		}else if(packet instanceof Presence){
			sessionManager.sendPacket(packet);
		}else if(packet instanceof IQ){
			IQ iq = (IQ)packet;
			if(isRequest(iq)){
				Handler handler = handlerManager.getHandler(iq.getNamespace());
				if(handler != null){
					IQ result = handler.processPacket(iq);
					sessionManager.sendPacket(result);
				}else{
					routeError(iq);
				}
			}else{
				sessionManager.sendPacket(packet);
			}
		}
	}
	/**
	 * 发往远程用户
	 * @param packet
	 */
	public void routeRemotePacket(Packet packet){
		logger.debug("send to remote :"+JSON.toJSONString(packet));
		if(packet instanceof Message){
			hazelcastManager.publish(RealWebConstants.MESSAGE_PRESENCE_TOPIC, packet);
		}else if(packet instanceof Presence){
			hazelcastManager.publish(RealWebConstants.MESSAGE_PRESENCE_TOPIC, packet);
		}else if(packet instanceof IQ){
			routeError((IQ)packet);
		}
	}
	private void routeError(IQ iq) {
		IQ response = IQ.createErrorResponse(iq, new PacketError(PacketError.Condition.bad_request));
		sessionManager.sendPacket(response);
	}
	public void init(ModuleManager moduleManager,ModuleInfo info) {
		super.init(moduleManager, info);
		sessionManager = moduleManager.getSessionManager();
		handlerManager = moduleManager.getHandlerManager();
		interceptorManager = moduleManager.getInterceptorManager();
		hazelcastManager = moduleManager.getHazelCastManager();
		hazelcastManager.subscribeTopic(RealWebConstants.MESSAGE_PRESENCE_TOPIC, new MessageListener<Packet>() {
			
			public void onMessage(com.hazelcast.core.Message<Packet> arg0) {
					Packet p = arg0.getMessageObject();
					if(p instanceof Message || p instanceof Presence){
						if(isToLocal(p)){
							routeLocalPacket(p);
					}
				}
				
			}
		});
	}
	public void start() {
		
	}
	public void stop() {
		this.moduleManager.removeModule(this);
	}
}
