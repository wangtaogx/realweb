package com.tao.realweb.plugins.system.jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.plugins.basic.AbstractPlugin;
import com.tao.realweb.plugins.basic.PluginInfo;
import com.tao.realweb.plugins.basic.PluginManager;
import com.tao.realweb.util.StringUtil;

public class JettyPlugin extends AbstractPlugin{

	Server server ;
	private Logger logger = LoggerFactory.getLogger(JettyPlugin.class);
	@Override
	public void init(PluginManager pluginManager, PluginInfo info) {
		super.init(pluginManager, info);
		try{
			int port = StringUtil.toInt(getPluginInfo().getParameter("jetty.port"));
			server = new Server(port);  
	        WebAppContext context = new WebAppContext();  
	        context.setContextPath("/jersey");  
	        context.setDescriptor("F:/products/communication/jersey/WebRoot/WEB-INF/web.xml");  
	        context.setResourceBase("F:/products/communication/jersey/WebRoot");  
	        context.setParentLoaderPriority(true);  
	        server.setHandler(context);
		}catch(Exception e){
			
		}
	}

	@Override
	public void start() {
		new Thread(){
			public void run() {
				try{
				logger.debug(getPluginInfo().getPluginName()+"   start......................");
				server.start();  
			     server.join();
				}catch(Exception e){
					
				}
			};
		}.start();
	}

	@Override
	public void destroy() {
		try{
			server.stop();
			server.destroy();
		}catch(Exception e){
			
		}
	}

}
