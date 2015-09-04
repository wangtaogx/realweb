package com.tao.realweb.modules.system.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.bean.Packet;
import com.tao.realweb.modules.basic.AbstractModule;

public class SessionManager extends AbstractModule{

	public static String MODULE_NAME = "SessionManager";
	public static String DEFAULT_CHAT_NAMESPACE = "/chat";
	private Logger logger = LoggerFactory.getLogger(SessionManager.class);
	private Map<String,Map<String,Session>> sessionMap = new ConcurrentHashMap<String,Map<String, Session>>();
	public SessionManager(){
	}
	public void addClient(String jid,Session client){
		addClient(DEFAULT_CHAT_NAMESPACE,jid,client);
	}
	public void addClient(String namespace,String jid,Session client){
		if(!sessionMap.containsKey(namespace))
			sessionMap.put(namespace, new ConcurrentHashMap<String,Session>() );
		Map<String,Session> userMap = sessionMap.get(namespace);
		if(userMap.containsKey(jid))
			userMap.remove(jid);
		userMap.put(jid, client);
	}
	public void deleteClient(String namespace,String jid){
		if(sessionMap.containsKey(namespace)){
			Map<String,Session> userMap = sessionMap.get(namespace);
			if(userMap != null && userMap.containsKey(jid))
				userMap.remove(jid);
		}
	}
	public void deleteClient(String jid){
		deleteClient(DEFAULT_CHAT_NAMESPACE,jid);
	}
	public Session getClient(String namespace,String jid){
		if(sessionMap.containsKey(namespace)){
			Map<String,Session> userMap = sessionMap.get(namespace);
			if(userMap != null && userMap.containsKey(jid))
				return userMap.get(jid);
		}
		return null;
	}
	public Session getClient(String jid){
		return getClient(DEFAULT_CHAT_NAMESPACE,jid);
	}
	public Map<String,Session> getClientMap(String namespace){
		return sessionMap.get(namespace);
	}
	public void sendPacket(Packet packet){
		String to = packet.getTo();
		Session session = getClient(to);
		if(session != null){
			session.sendMessage(packet);
			packet = null;
		}
	}
	public void start() {
		
	}
	public void stop() {
		this.moduleManager.removeModule(this);
	}
}
