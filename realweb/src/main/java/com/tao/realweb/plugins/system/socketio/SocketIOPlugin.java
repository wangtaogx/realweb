package com.tao.realweb.plugins.system.socketio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.modules.system.hazelcast.HazelCastManager;
import com.tao.realweb.modules.system.routing.PacketRoutingManager;
import com.tao.realweb.modules.system.session.SessionManager;
import com.tao.realweb.plugins.basic.AbstractPlugin;
import com.tao.realweb.plugins.basic.PluginInfo;
import com.tao.realweb.plugins.basic.PluginManager;
import com.tao.realweb.util.StringUtil;

public class SocketIOPlugin extends AbstractPlugin{

	private Logger logger = LoggerFactory.getLogger(SocketIOPlugin.class);
	private SessionManager sessionManager;
	private PacketRoutingManager packetRoutingManager;
	private HazelCastManager hazelCastManager;
	private SocketIOManager socketIOManager;
	
	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	public PacketRoutingManager getPacketRoutingManager() {
		return packetRoutingManager;
	}

	public void setPacketRoutingManager(PacketRoutingManager packetRoutingManager) {
		this.packetRoutingManager = packetRoutingManager;
	}

	public HazelCastManager getHazelCastManager() {
		return hazelCastManager;
	}

	public void setHazelCastManager(HazelCastManager hazelCastManager) {
		this.hazelCastManager = hazelCastManager;
	}

	@Override
	public void init(PluginManager pluginManager,PluginInfo info) {
		super.init(pluginManager, info);
		this.sessionManager = pluginManager.getRealWebServer().getModuleManager().getSessionManager();
		this.packetRoutingManager = pluginManager.getRealWebServer().getModuleManager().getPacketRoutingManager();
		this.hazelCastManager = pluginManager.getRealWebServer().getModuleManager().getHazelCastManager();
		this.socketIOManager = SocketIOManager.getInstance();
		this.socketIOManager.initServer(this.getPluginManager().getRealWebServer().getServerInfo().getIp(), StringUtil.toInt(getPluginInfo().getParameter("socketio.port")));
		NameSpaceListenerChatRoom chatNameSpaceListener = new NameSpaceListenerChatRoom(sessionManager, packetRoutingManager, hazelCastManager, this);
		this.socketIOManager.addNamespaceListener(chatNameSpaceListener);
		
		socketIOManager.initNamespaces();
	}

	public void start() {
		new Thread(){
			public void run() {
				socketIOManager.start();
				logger.debug(getPluginInfo().getPluginName()+"   start......................");
			};
		}.start();
		
	}
	@Override
	public void destroy() {
		socketIOManager.stop();
	}


}
