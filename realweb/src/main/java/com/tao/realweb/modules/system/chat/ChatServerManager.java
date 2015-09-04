package com.tao.realweb.modules.system.chat;

import com.tao.realweb.modules.basic.AbstractModule;
import com.tao.realweb.modules.basic.ModuleInfo;
import com.tao.realweb.modules.basic.ModuleManager;
import com.tao.realweb.modules.system.chat.socketio.ChatDispatcherServer;
import com.tao.realweb.modules.system.chat.socketio.SocketIOManager;
import com.tao.realweb.modules.system.hazelcast.HazelCastManager;
import com.tao.realweb.modules.system.routing.PacketRoutingManager;
import com.tao.realweb.modules.system.session.SessionManager;

public class ChatServerManager extends AbstractModule{

	private SessionManager sessionManager;
	private PacketRoutingManager packetRoutingManager;
	private HazelCastManager hazelCastManager;
	
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
	public void init(ModuleManager moduleManager, ModuleInfo info) {
		super.init(moduleManager, info);
		this.sessionManager = moduleManager.getSessionManager();
		this.packetRoutingManager = moduleManager.getPacketRoutingManager();
		this.hazelCastManager = moduleManager.getHazelCastManager();
	}

	public void start() {
		ChatDispatcherServer chatServer = new ChatDispatcherServer(sessionManager, packetRoutingManager, hazelCastManager, this);
		SocketIOManager.getInstance(this).start();
		
	}

	public void stop() {
		SocketIOManager.getInstance(this).stop();
		this.moduleManager.removeModule(this);
	}


}
