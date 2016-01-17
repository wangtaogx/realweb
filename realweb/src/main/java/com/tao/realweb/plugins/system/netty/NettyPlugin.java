package com.tao.realweb.plugins.system.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.plugins.basic.AbstractPlugin;
import com.tao.realweb.util.StringUtil;

public class NettyPlugin extends AbstractPlugin {

	private Logger logger = LoggerFactory.getLogger(NettyPlugin.class);
	EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
	EventLoopGroup workerGroup = new NioEventLoopGroup();
	private void runNettyServer(int port){
        try {
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
             .channel(NioServerSocketChannel.class) // (3)
             .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                 @Override
                 public void initChannel(SocketChannel ch) throws Exception {
                	 ch.pipeline().addLast(new StringEncoder());
                	 ch.pipeline().addLast(new LineBasedFrameDecoder(1024*64));
                     ch.pipeline().addLast(new StringDecoder());
                     MessageHandler handler = new MessageHandler();
                     handler.setPluginManager(NettyPlugin.this.getPluginManager());
                     ch.pipeline().addLast(handler);
                 }
             })
             .option(ChannelOption.SO_BACKLOG, 128)          // (5)
             .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)
            ChannelFuture f = b.bind(port).sync(); // (7)
            f.channel().closeFuture().sync();
        } catch(Exception e) {
        }
        finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

	}
	@Override
	public void start() {
		new Thread() {
			public void run() {
				try {
					int port = StringUtil.toInt(getPluginInfo().getParameter("netty.port"));
					logger.debug(getPluginInfo().getPluginName()+"   start......................"+"in "+port);
					runNettyServer(port);
				} catch (Exception e) {
					e.printStackTrace();
				}
			};
		}.start();
	}

	@Override
	public void destroy() {
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

}
