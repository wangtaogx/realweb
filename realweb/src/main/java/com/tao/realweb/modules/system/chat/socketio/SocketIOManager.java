package com.tao.realweb.modules.system.chat.socketio;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.tao.realweb.conf.system.RealWebConfig;
import com.tao.realweb.container.RealWebServer;
import com.tao.realweb.modules.system.chat.ChatServerManager;

public class SocketIOManager{

	private List<SocketListener> socketListeners = new CopyOnWriteArrayList<SocketListener>();
	private Logger logger = LoggerFactory.getLogger(SocketIOManager.class);
	private SocketIOServer socketIOServer;
	private NamespaceManager namespaceManager;
	private static Object lock = new Object();
	private static SocketIOManager instance = null;
	private ChatServerManager chatServerManager;
	private SocketIOManager(ChatServerManager chatServerManager) {
		this.chatServerManager = chatServerManager;
		namespaceManager = NamespaceManager.getInstance();
		Configuration config = new Configuration();
	    config.setHostname(this.chatServerManager.getModuleManager().getRealWebServer().getRealWebConfig().getString("realweb.ip"));
	    config.setPort(this.chatServerManager.getModuleManager().getRealWebServer().getRealWebConfig().getIntDefault("realweb.port", 9092));
	    //config.setStoreFactory(new HazelcastStoreFactory());
	    config.setAuthorizationListener(new AuthorizationListener(){
			public boolean isAuthorized(HandshakeData data) {
				boolean result = false;
				List<String> usernames = data.getUrlParams().get("username");
				List<String> passwords = data.getUrlParams().get("password");
				if(usernames == null || passwords == null){
					usernames = data.getHeaders().get("username");
					passwords = data.getHeaders().get("password");
				}
				if(usernames != null && passwords != null && usernames.size() > 0 && passwords.size() > 0 ){
					String username = usernames.get(0);
					String password = passwords.get(0);
					result = login(username,password);
				}
				return result;
			}
	    });
        socketIOServer = new SocketIOServer(config);
        namespaceManager.start(socketIOServer);
	}

	public static SocketIOManager getInstance(ChatServerManager chatServerManager) {
		if(instance == null){
			synchronized (lock) {
				if(instance == null){
					instance = new SocketIOManager(chatServerManager);
				}
			}
		}
		return instance;
	}
	private boolean login(String username, String password) {
		if(username.equals(password)){
			return true;
		}
		else
			return false;
	}

	public SocketIOServer getSocketIOServer() {
		return socketIOServer;
	}
/*	public SocketIONamespace getNamespaces(){
		return socketIOServer.getNamespace(Namespace.DEFAULT_NAME);
	}*/
	public void start() {
		socketIOServer.start();
	}

	public void stop(){
		socketIOServer.stop();
		socketIOServer = null;
		namespaceManager.clear();
		instance = null;
	}
	public void addSocketListener(SocketListener listener) {
		 socketListeners.add(listener);
	}
	public void removeSocketListener(SocketListener listener){
		socketListeners.remove(listener);
	}
	public void fireClientConnected(){
		
	}

}
