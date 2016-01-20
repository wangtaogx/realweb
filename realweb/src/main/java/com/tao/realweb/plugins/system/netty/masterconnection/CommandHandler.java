package com.tao.realweb.plugins.system.netty.masterconnection;

import com.tao.realweb.bean.Packet;

public interface CommandHandler {
	
	public Packet processPacket(Packet iq);

}
