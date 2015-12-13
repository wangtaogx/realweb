package com.tao.realweb.plugins.system.socketio;

import java.util.List;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOServer;
import com.tao.realweb.plugins.system.socketio.NamespaceListenerManager.NamespaceListener;

public class SocketIOManager{

	private SocketIOServer socketIOServer;
	private NamespaceListenerManager namespaceManager = NamespaceListenerManager.getInstance();
	private static Object lock = new Object();
	private static SocketIOManager instance = null;
	private SocketIOManager() {
	}

	public static SocketIOManager getInstance() {
		if(instance == null){
			synchronized (lock) {
				if(instance == null){
					instance = new SocketIOManager();
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
	public void initServer(String host,int port){
		if(socketIOServer == null){
			Configuration config = new Configuration();
		    config.setHostname(host);
		    config.setPort(port);
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
		}
	}
	public void initNamespaces(){
		namespaceManager.fireInitNamespaceListeners(socketIOServer);
	}
	public void start() {
		socketIOServer.start();
	}

	public void stop(){
		socketIOServer.stop();
		socketIOServer = null;
		namespaceManager.clear();
		instance = null;
	}
	public void addNamespaceListener(NamespaceListener listener){
		namespaceManager.addNamespaceListener(listener);
	}
}
