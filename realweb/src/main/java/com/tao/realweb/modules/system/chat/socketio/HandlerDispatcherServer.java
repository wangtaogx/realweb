package com.tao.realweb.modules.system.chat.socketio;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.redisson.Redisson;
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
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tao.realweb.bean.IQ;
import com.tao.realweb.bean.IQ.Type;
import com.tao.realweb.conf.system.RealWebConstants;
import com.tao.realweb.conf.system.RedisConfig;
import com.tao.realweb.modules.system.chat.ChatServerManager;
import com.tao.realweb.modules.system.chat.socketio.NamespaceManager.NamespaceListener;
import com.tao.realweb.modules.system.hazelcast.HazelCastManager;
import com.tao.realweb.modules.system.routing.PacketRoutingManager;
import com.tao.realweb.modules.system.session.SessionManager;
import com.tao.realweb.modules.system.session.SocketIOSession;
import com.tao.realweb.util.StringUtil;

public class HandlerDispatcherServer{

	private Logger logger = LoggerFactory.getLogger(HandlerDispatcherServer.class);
	private NamespaceManager namespaceManager;
	private PacketRoutingManager packetRoutingManager;
	private SessionManager sessionManager;
	private ChatServerManager chatServerManager;
	public static final String HANDLERNAMESPACE = "/handler";
	private ObjectMapper mapper = new ObjectMapper(); 
	public HandlerDispatcherServer(SessionManager sessionManager,PacketRoutingManager packetRoutingManager,HazelCastManager hazelCastManager,ChatServerManager chatServerManager) {
		this.sessionManager = sessionManager;
		this.namespaceManager = NamespaceManager.getInstance();
		this.packetRoutingManager = packetRoutingManager;
		this.chatServerManager = chatServerManager;
		namespaceManager.addNamespaceListener(new NamespaceListener(){
			public String getNamespace() {
				return "/handler";
			}
			public void start(SocketIONamespace namespace,SocketIOServer server) {
				Redisson redisson = RedisConfig.createRedisson();
				Set<String> urls = redisson.getSet(RealWebConstants.REALWEBSERVERHANDLERURL);
				urls.add("http://"+server.getConfiguration().getHostname()+":"+server.getConfiguration().getPort()+HANDLERNAMESPACE);
				
				namespace.addConnectListener(new ConnectListener() {
					public void onConnect(SocketIOClient client) {
						System.out.println("connected");
						if(client instanceof NamespaceClient){
							NamespaceClient nClient = (NamespaceClient)client;
							HandshakeData data = nClient.getBaseClient().getHandshakeData();
							List<String> usernames = data.getHeaders().get("username");
							List<String> resources = data.getHeaders().get("resource");
							if(usernames.size() > 0  ){
								String username = usernames.get(0);
								String resource = "";
								if(resources != null && resources.size() > 0)
								  resource = resources.get(0);
								String jid = username+"@"+HandlerDispatcherServer.this.chatServerManager.getModuleManager().getRealWebServer().getServerInfo().getHostname();
								if(!StringUtil.isEmpty(resource)){
									jid += "/"+resource;
								}
								HandlerDispatcherServer.this.sessionManager.addClient(getNamespace(),jid, new SocketIOSession(jid, nClient));
								logger.info(jid+":登入成功:");
								client.sendEvent("connected", HandlerDispatcherServer.this.chatServerManager.getModuleManager().getRealWebServer().getServerInfo().getHostname());
							}
						}
					}
				});
				/*namespace.addJsonObjectListener(IQ.class, new DataListener<IQ>() {
		            public void onData(SocketIOClient client, IQ data, AckRequest ackRequest) {
		            	System.out.println("服务器收到处理后的IQ消息"+data);
		            	if(data.getProperty("connected") != null){
		            		IQ iq = new IQ();
		            		iq.setNamespace("exampleHandler");
		            		client.sendEvent("message",iq);
		            	}
		            }
			   });*/
				namespace.addMessageListener(new DataListener<String>(){

					public void onData(SocketIOClient client, String data,
							AckRequest ackSender) {
						try {
							IQ iq = mapper.readValue(data, IQ.class);
							iq.setType(Type.RESULT);
							System.out.println("服务器收到结果:"+iq.toString());
							HandlerDispatcherServer.this.packetRoutingManager.routePacket(iq);
						} catch (JsonParseException e) {
							e.printStackTrace();
						} catch (JsonMappingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				});
				namespace.addDisconnectListener(new DisconnectListener() {

					public void onDisconnect(SocketIOClient client) {
						System.out.println("断开连接");
					}
				});
			}
		});
	}
	
}
