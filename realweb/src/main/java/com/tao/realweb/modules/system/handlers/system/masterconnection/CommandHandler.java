package com.tao.realweb.modules.system.handlers.system.masterconnection;

import com.tao.realweb.bean.Packet;

public interface CommandHandler {
	
	public Packet processPacket(Packet iq);

}
