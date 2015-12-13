package com.tao.realweb.plugins.system.example;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.modules.system.handlers.HandlerManager;
import com.tao.realweb.plugins.basic.AbstractPlugin;
import com.tao.realweb.plugins.basic.PluginInfo;
import com.tao.realweb.plugins.basic.PluginManager;

public class ExamplePlugin extends AbstractPlugin{

	Server server ;
	private Logger logger = LoggerFactory.getLogger(ExamplePlugin.class);
	private HandlerManager handlerManager ;
	@Override
	public void init(PluginManager pluginManager, PluginInfo info) {
		super.init(pluginManager, info);
		handlerManager = pluginManager.getRealWebServer().getModuleManager().getHandlerManager();
		ExampleHandler handler = new ExampleHandler();
		handlerManager.putHandler(handler.getNamespace(), handler);
	}

	@Override
	public void start() {
		logger.debug(getPluginInfo().getPluginName()+"   start......................");
	}

	@Override
	public void destroy() {
	}

}
