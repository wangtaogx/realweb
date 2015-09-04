package com.tao.realweb.modules.system.chat.socketio;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIONamespace;
import com.corundumstudio.socketio.SocketIOServer;

public class NamespaceManager {

	private Logger logger = LoggerFactory.getLogger(NamespaceManager.class);
	private List<NamespaceListener> listeners = new CopyOnWriteArrayList<NamespaceListener>();
	private static NamespaceManager namespaceManager =null;
	private static Object o = new Object();
	private NamespaceManager(){
	}
	public static NamespaceManager getInstance() {
		if(namespaceManager == null){
			synchronized (o) {
				if(namespaceManager == null){
					namespaceManager = new NamespaceManager();
				}
			}
		}
		return namespaceManager;
	}
	public void start(SocketIOServer server){
		for(NamespaceListener listener : listeners){
			final SocketIONamespace namespace = server.addNamespace(listener.getNamespace());
			listener.start(namespace,server);
		}
	}
	public void addNamespaceListener(NamespaceListener listener){
		this.listeners.add(listener);
	}
	public interface NamespaceListener{
		public String getNamespace();
		public void start(SocketIONamespace namespace,SocketIOServer server);
	}
	public void clear(){
		listeners.clear();
		namespaceManager = null;
	}
}
