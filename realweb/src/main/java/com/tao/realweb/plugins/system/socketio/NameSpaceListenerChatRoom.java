package com.tao.realweb.plugins.system.socketio;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.corundumstudio.socketio.transport.NamespaceClient;
import com.tao.realweb.bean.JID;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.bean.PacketError;
import com.tao.realweb.modules.system.routing.PacketRoutingManager;
import com.tao.realweb.modules.system.session.SessionManager;
import com.tao.realweb.plugins.system.socketio.NamespaceListenerManager.NamespaceListener;
import com.tao.realweb.util.StringUtil;

public class NameSpaceListenerChatRoom implements NamespaceListener{

	private Logger logger = LoggerFactory.getLogger(NameSpaceListenerChatRoom.class);
	private SessionManager sessionManager;
	private PacketRoutingManager packetRoutingManager;
	public static final String CHATNAMESPACE = "/chat";
	private SocketIOPlugin chatServerPlugin;
	public NameSpaceListenerChatRoom(SessionManager sessionManager,PacketRoutingManager packetRoutingManager,SocketIOPlugin chatServerPlugin) {
		this.packetRoutingManager = packetRoutingManager;
		this.sessionManager = sessionManager;
		this.chatServerPlugin = chatServerPlugin;
	}
	public String getNamespace() {
		return CHATNAMESPACE;
	}

	public void initNamespace(SocketIOServer server) {
		final SocketIONamespace namespace = server.addNamespace(getNamespace());
		namespace.addJsonObjectListener(Packet.class, new DataListener<Packet>() {
            public void onData(SocketIOClient client, Packet iq, AckRequest ackRequest) {
            	logger.info("iq消息");
            	if(valiatePacket(iq,client))
            		NameSpaceListenerChatRoom.this.packetRoutingManager.routePacket(iq);
            }
        });
		namespace.addEventListener("iqEvent",Packet.class, new DataListener<Packet>() {
            public void onData(SocketIOClient client, Packet iq, AckRequest ackRequest) {
            	logger.info("iq事件");
            	if(valiatePacket(iq,client))
            		NameSpaceListenerChatRoom.this.packetRoutingManager.routePacket(iq);
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
						NameSpaceListenerChatRoom.this.sessionManager.deleteClient(jid);
						logger.info(jid+":退出系统:");
					}
					
				}
			}
        	
        });
	}
	private boolean valiatePacket(Packet packet,SocketIOClient client) {
		try{
			if(!JID.isJID(packet.getFrom()) || !JID.isJID(packet.getTo()) || StringUtil.isEmpty(packet.getPacketID()) ||StringUtil.isEmpty(packet.getNamespace()) || StringUtil.isEmpty(packet.getAction())){
				client.sendEvent("error",Packet.createErrorResponse(packet, PacketError.PACKET_FORMAT_ERROR));
				return false;
			}
			return true;
		}catch(Exception e){
			client.sendEvent("error",Packet.createErrorResponse(packet, PacketError.SERVER_EXCEPTION));
			return false;
		}
		
	}
	
}
