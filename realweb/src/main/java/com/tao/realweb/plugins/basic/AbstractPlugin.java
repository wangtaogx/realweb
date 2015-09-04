package com.tao.realweb.plugins.basic;


public abstract  class AbstractPlugin implements Plugin {

	protected PluginManager pluginManager = null;
	protected PluginInfo pluginInfo = null;
	
	public void init(PluginManager pluginManager,PluginInfo info) {
		this.pluginManager = pluginManager;
		this.pluginInfo = info;
	}

	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}

	public void setPluginInfo(PluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public PluginManager getPluginManager() {
		return pluginManager;
	}

	public void setPluginManager(PluginManager pluginManager) {
		this.pluginManager = pluginManager;
	}
	

}
