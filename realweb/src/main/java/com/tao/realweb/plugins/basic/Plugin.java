package com.tao.realweb.plugins.basic;

import com.tao.realweb.container.RealWebServer;

public interface Plugin {

	public void init(PluginManager pluginManager,PluginInfo info);
	public void start();
	public void destroy();
}
