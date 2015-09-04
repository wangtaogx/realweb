package com.tao.realweb.modules.system.session;

import java.net.UnknownHostException;

import com.corundumstudio.socketio.SocketIOClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tao.realweb.bean.Packet;

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


	public String getHostAddress() throws UnknownHostException {
		return this.client.getHandshakeData().getAddress().getHostString();
	}

	public String getHostName() throws UnknownHostException {
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
