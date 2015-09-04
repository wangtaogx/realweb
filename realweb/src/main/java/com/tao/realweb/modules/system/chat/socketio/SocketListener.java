package com.tao.realweb.modules.system.chat.socketio;

import com.corundumstudio.socketio.SocketIOClient;

public interface SocketListener {

	public void connected(SocketIOClient client);
	
	public void disconnected(SocketIOClient client);
}
