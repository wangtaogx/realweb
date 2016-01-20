package com.tao.realweb.plugins.system.netty.masterconnection;

import java.util.Collection;

import com.alibaba.fastjson.JSONObject;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.bean.PacketError;
import com.tao.realweb.modules.system.handlers.HandlerManager;
import com.tao.realweb.modules.system.handlers.basic.AbstractHandler;
import com.tao.realweb.modules.system.handlers.basic.HandlerInfo;
import com.tao.realweb.modules.system.handlers.basic.HandlerListener;
import com.tao.realweb.modules.system.session.SessionManager;

public class MasterConnectionHandler extends AbstractHandler{

	private SessionManager sessionManager;
	private CommandManager commandManager = CommandManager.getInstance();
	public static final String NAMESPACE_SERVER_MASTER_CONNECTION = "http://tj.com/namespace/masterconnection";
	@Override
	public Packet processPacket(Packet iq) {
		String cmd = iq.getHeader("cmd");
		if(commandManager.containsHandler(cmd)){
			CommandHandler cmdHandler = commandManager.getHandler(cmd);
			Packet result = cmdHandler.processPacket(iq);
		}
		return Packet.createErrorResponse(iq, PacketError.PACKET_FORMAT_ERROR);
	}
	@Override
	public void init(HandlerManager handlerManager, HandlerInfo info) {
		super.init(handlerManager, info);
		sessionManager = handlerManager.getModuleManager().getSessionManager();
		commandManager.putHandler("heatheart", new CommandHandler() {
			
			@Override
			public Packet processPacket(Packet iq) {
				return Packet.createResultPacket(iq);
			}
		});
		commandManager.putHandler("namespace", new CommandHandler() {
			
			@Override
			public Packet processPacket(Packet iq) {
				Collection<String> namespaces = MasterConnectionHandler.this.handlerManager.getHandlerNamespaces();
				Packet result = Packet.createResultPacket(iq);
				JSONObject body = new JSONObject();
				body.put("namespaces", namespaces);
				result.setBody(body);
				return result;
			}
		});
		this.handlerManager.addHandlerListener(new HandlerListener() {
			
			@Override
			public void handlerRemoved(String namespace) {
				Packet packet = new Packet();
				sessionManager.sendPacketToAll(packet);
			}
			
			@Override
			public void handlerAdded(String namespace) {
				
			}
		});
	}
}
