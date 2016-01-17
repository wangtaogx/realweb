package com.tao.realweb.plugins.system.socketio;

import com.corundumstudio.socketio.SocketIOClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.modules.system.session.AbstractSession;

public class SocketIOSession extends AbstractSession{

	private SocketIOClient client;
	private ObjectMapper mapper = new ObjectMapper();
	public SocketIOSession(String jidStr,SocketIOClient client){
		super(jidStr);
		this.client = client;
	}


	public String getServerName() {
		return getAddress().getServerName();
	}
	public void close() {
		this.client.disconnect();
	}

	public boolean isClosed() {
		return this.client.isChannelOpen();
	}


	public String getClientAddress() {
		return this.client.getHandshakeData().getAddress().getHostString();
	}

	public String getClientName() {
		return this.client.getHandshakeData().getAddress().getHostName();
	}
	
	public void sendMessage(Packet packet){
		try {
			if(client != null){
				client.sendMessage(mapper.writeValueAsString(packet));
			} 
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

}
