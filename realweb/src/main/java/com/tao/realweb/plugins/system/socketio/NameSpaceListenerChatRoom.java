package com.tao.realweb.plugins.system.socketio;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.transport.NamespaceClient;
import com.tao.realweb.bean.IQ;
import com.tao.realweb.bean.JID;
import com.tao.realweb.bean.Message;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.bean.PacketError;
import com.tao.realweb.bean.PacketError.Condition;
import com.tao.realweb.bean.Presence;
import com.tao.realweb.modules.system.hazelcast.HazelCastManager;
import com.tao.realweb.modules.system.routing.PacketRoutingManager;
import com.tao.realweb.modules.system.session.SessionManager;
import com.tao.realweb.modules.system.session.SocketIOSession;
import com.tao.realweb.plugins.system.socketio.NamespaceListenerManager.NamespaceListener;
import com.tao.realweb.util.StringUtil;

public class NameSpaceListenerChatRoom implements NamespaceListener{

	private Logger logger = LoggerFactory.getLogger(NameSpaceListenerChatRoom.class);
	private SessionManager sessionManager;
	private PacketRoutingManager packetRoutingManager;
	private HazelCastManager hazelCastManager;
	public static final String CHATNAMESPACE = "/chat";
	private SocketIOPlugin chatServerPlugin;
	public NameSpaceListenerChatRoom(SessionManager sessionManager,PacketRoutingManager packetRoutingManager,HazelCastManager hazelCastManager,SocketIOPlugin chatServerPlugin) {
		this.packetRoutingManager = packetRoutingManager;
		this.sessionManager = sessionManager;
		this.hazelCastManager = hazelCastManager;
		this.chatServerPlugin = chatServerPlugin;
	}
	public String getNamespace() {
		return CHATNAMESPACE;
	}

	public void initNamespace(SocketIOServer server) {
		final SocketIONamespace namespace = server.addNamespace(getNamespace());
		namespace.addJsonObjectListener(Message.class, new DataListener<Message>() {
            public void onData(SocketIOClient client, Message message, AckRequest ackRequest) {
            	logger.debug("接收到Message消息:"+JSON.toJSONString(message));
            	if(valiatePacket(message,client))
            		NameSpaceListenerChatRoom.this.packetRoutingManager.routePacket(message);
            	//socketIOServer.getBroadcastOperations().sendJsonObject(message);
            }

        });
		namespace.addJsonObjectListener(Presence.class, new DataListener<Presence>() {
            public void onData(SocketIOClient client, Presence presence, AckRequest ackRequest) {
                // broadcast messages to all clients
            	logger.info("Presence消息");
            	if(valiatePacket(presence,client))
            		NameSpaceListenerChatRoom.this.packetRoutingManager.routePacket(presence);
            	//socketIOServer.getBroadcastOperations().sendJsonObject(presence);
            }
        });
		namespace.addJsonObjectListener(IQ.class, new DataListener<IQ>() {
            public void onData(SocketIOClient client, IQ iq, AckRequest ackRequest) {
            	logger.info("iq消息");
            	if(valiatePacket(iq,client))
            		NameSpaceListenerChatRoom.this.packetRoutingManager.routePacket(iq);
            }
        });
		namespace.addEventListener("iqEvent",IQ.class, new DataListener<IQ>() {
            public void onData(SocketIOClient client, IQ iq, AckRequest ackRequest) {
            	logger.info("iq事件");
            	if(valiatePacket(iq,client))
            		NameSpaceListenerChatRoom.this.packetRoutingManager.routePacket(iq);
            }
        });
		namespace.addEventListener("messageEvent",Message.class, new DataListener<Message>() {
            public void onData(SocketIOClient client, Message message, AckRequest ackRequest) {
            	logger.info("message事件");
            	if(valiatePacket(message,client))
            		NameSpaceListenerChatRoom.this.packetRoutingManager.routePacket(message);
            }
        });
		namespace.addEventListener("presenceEvent",Presence.class, new DataListener<Presence>() {
            public void onData(SocketIOClient client, Presence presence, AckRequest ackRequest) {
            	logger.info("presence事件");
            	if(valiatePacket(presence,client))
            		NameSpaceListenerChatRoom.this.packetRoutingManager.routePacket(presence);
            }
        });
		namespace.addConnectListener(new ConnectListener(){

			public void onConnect(SocketIOClient client) {
				if(client instanceof NamespaceClient){
					NamespaceClient nClient = (NamespaceClient)client;
					HandshakeData data = nClient.getBaseClient().getHandshakeData();
					List<String> usernames = data.getUrlParams().get("username");
					List<String> resources = data.getUrlParams().get("resource");
					if(usernames.size() > 0  ){
						String username = usernames.get(0);
						String resource = "";
						if(resources != null && resources.size() > 0)
						  resource = resources.get(0);
						String jid = username+"@"+NameSpaceListenerChatRoom.this.chatServerPlugin.getPluginManager().getRealWebServer().getServerInfo().getHostname();
						if(!StringUtil.isEmpty(resource)){
							jid += "/"+resource;
						}
						NameSpaceListenerChatRoom.this.sessionManager.addClient(jid, new SocketIOSession(jid, nClient));
						logger.info(jid+":登入成功:");
						NameSpaceListenerChatRoom.this.hazelCastManager.addAccount(jid);
						client.sendEvent("connected", NameSpaceListenerChatRoom.this.chatServerPlugin.getPluginManager().getRealWebServer().getServerInfo().getHostname());
					}
					
				}
				
			}
        	
        });
		namespace.addDisconnectListener(new DisconnectListener(){

			public void onDisconnect(SocketIOClient client) {
				if(client instanceof NamespaceClient){
					NamespaceClient nClient = (NamespaceClient)client;
					HandshakeData data = nClient.getBaseClient().getHandshakeData();
					List<String> usernames = data.getUrlParams().get("username");
					List<String> resources = data.getUrlParams().get("resource");
					if(usernames.size() > 0  ){
						String username = usernames.get(0);
						String resource = "";
						if(resources != null && resources.size() > 0)
						  resource = resources.get(0);
						String jid = username+"@"+NameSpaceListenerChatRoom.this.chatServerPlugin.getPluginManager().getRealWebServer().getServerInfo().getHostname();
						if(!StringUtil.isEmpty(resource)){
							jid += "/"+resource;
						}
						NameSpaceListenerChatRoom.this.hazelCastManager.removeAccount(jid);
						NameSpaceListenerChatRoom.this.sessionManager.deleteClient(jid);
						logger.info(jid+":退出系统:");
					}
					
				}
			}
        	
        });
	}
	private boolean valiatePacket(Packet packet,SocketIOClient client) {
		try{
			if(!JID.isJID(packet.getFrom()) || !JID.isJID(packet.getTo())){
				packet.setError(new PacketError(Condition.forbidden,"消息内容格式错误"));
				client.sendEvent("error",packet);
				return false;
			}
			if(packet instanceof Message){
				Message m =  (Message)packet;
				if(m.getType() == null){
					packet.setError(new PacketError(Condition.forbidden,"消息内容格式错误"));
					client.sendEvent("error",packet);
					return false;
				}
			}
			if(packet instanceof Presence){
				Presence p =  (Presence)packet;
				if(p.getType() == null){
					packet.setError(new PacketError(Condition.forbidden,"消息内容格式错误"));
					client.sendEvent("error",packet);
					return false;
				}
			}
			if(packet instanceof IQ){
				IQ iq = (IQ)packet;
				if(StringUtil.isEmpty(iq.getNamespace())){
					client.sendEvent("error",new PacketError(Condition.forbidden,"消息内容格式错误"));
					return false;
				}
				if(iq.getType() == null){
					client.sendEvent("error",new PacketError(Condition.forbidden,"消息内容格式错误"));
					return false;
				}
			}
			if(!hazelCastManager.containsServer(JID.formatServerName(packet.getFrom())) || !hazelCastManager.containsServer(JID.formatServerName(packet.getTo()))){
				client.sendEvent("error",new PacketError(Condition.forbidden,"消息内容格式错误"));
				return false;
			}
			return true;
		}catch(Exception e){
			client.sendEvent("error",new PacketError(Condition.forbidden,"服务器异常"));
			return false;
		}
		
	}
	
}
