package com.tao.realweb.plugins.system.socketio;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.corundumstudio.socketio.SocketIOServer;

public class NamespaceListenerManager {

	private List<NamespaceListener> listeners = new CopyOnWriteArrayList<NamespaceListener>();
	private static NamespaceListenerManager namespaceManager =new NamespaceListenerManager();
	private NamespaceListenerManager(){
	}
	public static NamespaceListenerManager getInstance() {
		return namespaceManager;
	}
	public void fireInitNamespaceListeners(SocketIOServer server){
		for(NamespaceListener listener : listeners){
			listener.initNamespace(server);
		}
	}
	public void addNamespaceListener(NamespaceListener listener){
		this.listeners.add(listener);
	}
	public void clear(){
		listeners.clear();
		namespaceManager = null;
	}
	
	public interface NamespaceListener{
		public String getNamespace();
		public void initNamespace(SocketIOServer server);
	}
}
