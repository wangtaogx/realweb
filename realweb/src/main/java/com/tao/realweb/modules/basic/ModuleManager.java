package com.tao.realweb.modules.basic;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingWorker;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.container.RealWebServer;
import com.tao.realweb.modules.system.handlers.HandlerManager;
import com.tao.realweb.modules.system.hazelcast.HazelCastManager;
import com.tao.realweb.modules.system.interceptors.InterceptorManager;
import com.tao.realweb.modules.system.routing.PacketRoutingManager;
import com.tao.realweb.modules.system.session.SessionManager;


public class ModuleManager {

	public static final String DEFAULT_MODULE_NAME = "undefinedModule";
	private Logger logger = LoggerFactory.getLogger(ModuleManager.class); 
	private static ModuleManager instance = null;
	private Map<String,Module> modulesMap = new ConcurrentHashMap<String, Module>();
	private static Object lock = new Object();
	private List<Element> modules = null;
	private RealWebServer realWebServer;
	private ModuleManager(RealWebServer server){
		this.realWebServer = server;
		logger.info("加载模块--开始...");
		Document document = this.realWebServer.getRealWebConfig().getDocument();
		if(document != null){
			modules = document.selectNodes("/application/modules/module");
			if(modules != null){
				for(Element ele : modules){
					String moduleName = ele.elementText("modulename");
					String className = ele.elementText("classname");
					try{
					Class clazz = Class.forName(className);
					Object module = clazz.newInstance();
					modulesMap.put(module.getClass().getName(), (Module)module);
					}catch(Exception e){
						e.printStackTrace();
						logger.info("初始化模块失败:"+className);
					}
					
				}
			}
		}
		logger.info("加载模块--结束");
	}
	public static ModuleManager getInstance(RealWebServer server){
		if(instance == null){
			synchronized (lock) {
				if(instance == null){
					instance = new ModuleManager(server);
				}
			}
		}
		return instance;
	}
	public void init(){
		if(modules == null)
			return;
		logger.info("初始化--模块开始...");
		for(Element ele : modules){
			String moduleName = ele.elementText("modulename");
			String className = ele.elementText("classname");
			String description = ele.elementText("hedescription");
			Element parameters = ele.element("parameters");
			try{
				ModuleInfo info = new ModuleInfo();
				info.setModuleName(moduleName);
				info.setDescription(description);
				info.setClassName(className);
				if(parameters != null){
					List<Element> elements = parameters.elements();
					for(Element e : elements){
						info.addParameter(e.getName(), e.getTextTrim());
					}
				}
				Module module = modulesMap.get(className);
				if(module != null)
					module.init(this, info);
			}catch(Exception e){
				e.printStackTrace();
				modulesMap.remove(moduleName);
				logger.info("初始化错误"+className);
			}
		}
		logger.info("初始化--模块结束");
	}
	public Module getModule(String moduleName){
		return modulesMap.get(moduleName);
	}
	
	public void startModules(){

		for(String name :modulesMap.keySet()){
			modulesMap.get(name).start();
		}
	}
	public void stopModules(){
		for(String name :modulesMap.keySet()){
			modulesMap.get(name).stop();
		}
		while(getModuleSize() > 0){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		clear();
	}
	public void clear(){
		this.modulesMap.clear();
		instance = null;
	}
	public int getModuleSize(){
		return this.modulesMap.size();
	}
	public RealWebServer getRealWebServer() {
		return realWebServer;
	}
	public void setRealWebServer(RealWebServer realWebServer) {
		this.realWebServer = realWebServer;
	}
	public PacketRoutingManager getPacketRoutingManager(){
		return (PacketRoutingManager)getModule(PacketRoutingManager.class.getName());
	}
	public HazelCastManager getHazelCastManager(){
		return (HazelCastManager)getModule(HazelCastManager.class.getName());
	}
	public SessionManager getSessionManager(){
		return (SessionManager)getModule(SessionManager.class.getName());
	}
	public HandlerManager getHandlerManager(){
		return (HandlerManager)getModule(HandlerManager.class.getName());
	}
	public InterceptorManager getInterceptorManager(){
		return (InterceptorManager)getModule(InterceptorManager.class.getName());
	}
	public void removeModule(Module module){
		this.modulesMap.remove(module.getClass().getName());
	}
}
