package com.tao.realweb.container;

import java.io.File;
import java.util.Date;

import com.tao.realweb.bean.Packet;
import com.tao.realweb.conf.basic.Preference;
import com.tao.realweb.conf.basic.PreferenceManager;
import com.tao.realweb.conf.system.RealWebConfig;
import com.tao.realweb.conf.system.RealWebConstants;
import com.tao.realweb.modules.basic.Module;
import com.tao.realweb.modules.basic.ModuleManager;
import com.tao.realweb.modules.system.interceptors.InterceptorManager;
import com.tao.realweb.plugins.basic.PluginManager;
import com.tao.realweb.util.TaskExecutor;
public class RealWebServer {

	private static ModuleManager modulerManager = null;
	private static PreferenceManager preferenceManager = null;
	private static PluginManager pluginManager = null;
	private RealWebServerInfo serverInfo = null;
	private RealWebConfig realWebConfig;
	
	private static Object lock = new Object(); 
	
	public RealWebServer(){
		}
	
	public void init(RealWebConfig realWebConfig){
		this.realWebConfig = realWebConfig;
		preferenceManager = PreferenceManager.getInstance();
		preferenceManager.addPreference(realWebConfig);
		serverInfo = new RealWebServerInfo(realWebConfig.getString("realweb.dommain"),this.realWebConfig.getString("realweb.hostname"),this.realWebConfig.getString("realweb.version"),new Date());
	}
	public void start(){
		TaskExecutor.getInstance();
		modulerManager = ModuleManager.getInstance(this);
		modulerManager.init();
		modulerManager.startModules();
		pluginManager = PluginManager.getInstance(this);
		File pluginDir = new File(realWebConfig.getString("realweb.plugin.dir"));
		pluginManager.setPluginDirectory(pluginDir);
		pluginManager.init(this);
		pluginManager.startPlugins();
	}
	public void stop(){
		pluginManager.stopPlugins();
		modulerManager.stopModules();
		System.gc();
		
	}
	public ModuleManager getModuleManager(){
		return modulerManager;
	}
	public Module getModule(String moduleName){
		return modulerManager.getModule(moduleName);
	}
	public PreferenceManager getPreferenceManager(){
		return preferenceManager;
	}

	public PluginManager getPluginManager(){
		return pluginManager;
	}

	public RealWebServerInfo getServerInfo() {
		return serverInfo;
	}

	public void setServerInfo(RealWebServerInfo serverInfo) {
		this.serverInfo = serverInfo;
	}
	public RealWebConfig getRealWebConfig(){
		return this.realWebConfig;
	}
}
