package com.tao.realweb.plugins.system.netty;

import io.netty.channel.Channel;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.modules.system.session.AbstractSession;
import com.tao.realweb.util.Base64;

public class NettySession extends AbstractSession{
	private static Logger logger = LoggerFactory.getLogger(NettySession.class);
	private Channel channel;
	public NettySession(String jidStr) {
		super(jidStr);
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getServerName() {
		return getAddress().getServerName();
	}

	public void close() {
		channel.close();
	}

	public boolean isClosed() {
		return channel.isActive();
	}

	public void sendMessage(Packet packet) {
		sendMessage(channel,packet);
	}
	public static void sendMessage(Channel channel,Packet packet) {
		String sendStr = JSONObject.toJSONString(packet);
		logger.debug("write--->"+sendStr);
		channel.writeAndFlush(Base64.encodeBytes(sendStr.getBytes()) +"\r\n");
	}

	@Override
	public String getClientAddress() throws UnknownHostException {
		return "";
	}
}
