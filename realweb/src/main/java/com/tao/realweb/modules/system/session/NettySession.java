package com.tao.realweb.modules.system.session;

import java.net.UnknownHostException;

import com.tao.realweb.bean.Packet;

public class NettySession extends AbstractSession{

	public NettySession(String jidStr) {
		super(jidStr);
	}

	public String getServerName() {
		return null;
	}

	public void close() {
		
	}

	public boolean isClosed() {
		return false;
	}

	public String getHostAddress() throws UnknownHostException {
		return null;
	}

	public String getHostName() throws UnknownHostException {
		return null;
	}

	public void sendMessage(Packet packet) {
		
	}

}
