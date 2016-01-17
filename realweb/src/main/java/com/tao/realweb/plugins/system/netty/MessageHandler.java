package com.tao.realweb.plugins.system.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.tao.realweb.bean.JID;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.bean.PacketError;
import com.tao.realweb.modules.system.routing.PacketRoutingManager;
import com.tao.realweb.modules.system.session.Session;
import com.tao.realweb.modules.system.session.SessionManager;
import com.tao.realweb.plugins.basic.PluginManager;
import com.tao.realweb.util.Base64;
import com.tao.realweb.util.StringUtil;

public class MessageHandler extends ChannelInboundHandlerAdapter    {

	private Logger logger = LoggerFactory.getLogger(MessageHandler.class);
	private PluginManager pluginManager ;
	private SessionManager sessionManager;
	private PacketRoutingManager routingManager;
	public MessageHandler() {
	}
	
	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
		this.sessionManager = this.pluginManager.getRealWebServer().getModuleManager().getSessionManager();
		this.routingManager = this.pluginManager.getRealWebServer().getModuleManager().getPacketRoutingManager();
	}

	private String message = "";
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	message += msg;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
    	if(StringUtil.isEmpty(message))
    		return ;
    	String receiveStr = new String(Base64.decode(message));
    	logger.debug("read<---"+receiveStr);
    	Packet packet = JSONObject.parseObject(receiveStr,Packet.class);
    	message = "";
    	boolean isValiate = valiatePacket(packet, ctx.channel());
    	if(isValiate){
    		if(StringUtil.equals(packet.getHeader("cmd"),"login")){
				JSONObject body = packet.getBody();
				String password = body.getString("password");
				NettySession nettySession = new NettySession(packet.getFrom());
				nettySession.setChannel(ctx.channel());
				String token = UUID.randomUUID().toString();
				nettySession.setClientName(token);
				sessionManager.addClient(packet.getFrom(), nettySession);
				Packet response = Packet.createResultPacket(packet);
				JSONObject responseBody = new JSONObject();
				responseBody.put("token", token);
				response.setBody(responseBody);
				NettySession.sendMessage(ctx.channel(),response);
			}else{
				Session session = sessionManager.getClient(packet.getFrom()); 
	    		if(session != null){
	    			String token = packet.getHeader("token");
					if(StringUtil.equals(token, session.getClientName())){
						routingManager.routePacket(packet);
						return ;
					}
	    		}
				NettySession.sendMessage(ctx.channel(),Packet.createErrorResponse(packet, PacketError.TOKEN_NOT_EXIT));
			}
    	}
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
	private boolean valiatePacket(Packet packet,Channel client) {
		try{
			if(!JID.isJID(packet.getFrom()) || !JID.isJID(packet.getTo()) || StringUtil.isEmpty(packet.getPacketID()) ||StringUtil.isEmpty(packet.getNamespace()) || StringUtil.isEmpty(packet.getAction())){
				NettySession.sendMessage(client,Packet.createErrorResponse(packet, PacketError.PACKET_FORMAT_ERROR));
				return false;
			}
			return true;
		}catch(Exception e){
			NettySession.sendMessage(client,Packet.createErrorResponse(packet, PacketError.SERVER_EXCEPTION));
			return false;
		}
	}
}
