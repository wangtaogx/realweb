package com.tao.realweb.plugins.system.socketio;

import com.tao.realweb.modules.system.routing.PacketRoutingManager;
import com.tao.realweb.modules.system.session.SessionManager;
import com.tao.realweb.plugins.basic.AbstractPlugin;
import com.tao.realweb.plugins.basic.PluginInfo;
import com.tao.realweb.plugins.basic.PluginManager;
import com.tao.realweb.util.StringUtil;

public class SocketIOPlugin extends AbstractPlugin{

	private SessionManager sessionManager;
	private PacketRoutingManager packetRoutingManager;
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

	@Override
	public void init(PluginManager pluginManager,PluginInfo info) {
		super.init(pluginManager, info);
		this.sessionManager = pluginManager.getRealWebServer().getModuleManager().getSessionManager();
		this.packetRoutingManager = pluginManager.getRealWebServer().getModuleManager().getPacketRoutingManager();
		this.socketIOManager = SocketIOManager.getInstance();
		this.socketIOManager.initServer(this.getPluginManager().getRealWebServer().getServerInfo().getIp(), StringUtil.toInt(getPluginInfo().getParameter("socketio.port")));
		NameSpaceListenerChatRoom chatNameSpaceListener = new NameSpaceListenerChatRoom(sessionManager, packetRoutingManager, this);
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
		super.destroy();
		socketIOManager.stop();
	}


}
