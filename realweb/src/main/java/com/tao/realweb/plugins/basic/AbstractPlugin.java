package com.tao.realweb.plugins.basic;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.modules.system.handlers.HandlerManager;
import com.tao.realweb.modules.system.handlers.basic.Handler;
import com.tao.realweb.modules.system.handlers.basic.HandlerInfo;


public abstract  class AbstractPlugin implements Plugin {
	protected Logger logger = LoggerFactory.getLogger(Plugin.class);;
	protected PluginManager pluginManager = null;
	protected PluginInfo pluginInfo = null;
	protected Document document;
	protected HandlerManager handlerManager;
	public void init(PluginManager pluginManager,PluginInfo info) {
		logger.info("初始化--"+this.getClass()+"开始...");
		this.pluginManager = pluginManager;
		this.pluginInfo = info;
		this.handlerManager = this.pluginManager.getRealWebServer().getModuleManager().getHandlerManager();
		initPluginHandlers();
	}
	private void initPluginHandlers(){
		if(document == null)
			return; 
	 	List<Element> handlers = document.selectNodes("/plugin/handlers/handler");
		if(handlers == null)
			return ;
		for(Element ele : handlers){
			String namespace = ele.elementText("namespace");
			String className = ele.elementText("classname");
			try{
				Class clazz = Class.forName(className);
				Object handler = clazz.newInstance();
				handlerManager.putHandler(namespace, (Handler)handler);
			}catch(Exception e){
				logger.error("初始化Handler:"+className+"异常，类错误");
			}
		}
		
		for(Element ele : handlers){
			String namespace = ele.elementText("namespace");
			String className = ele.elementText("classname");
			String description = ele.elementText("description");
			Element parameters = ele.element("parameters");
			try{
				HandlerInfo info = new HandlerInfo();
				info.setNamespace(namespace);
				info.setDescription(description);
				info.setClassName(className);
				if(parameters != null){
					List<Element> elements = parameters.elements();
					for(Element e : elements){
						info.addParameter(e.getName(), e.getTextTrim());
					}
				}
				Handler handler = handlerManager.getHandler(namespace);
				if(handler != null){
					logger.info("完成初始化Handler--"+info.getClassName());
					handler.init(handlerManager, info);
				}
			}catch(Exception e){
				e.printStackTrace();
				handlerManager.removeHandler(namespace);
				logger.error("初始化错误"+className);
			}
		}
		logger.info("完成初始化Handlers--"+this.pluginInfo.getClassName());
	}
	
	@Override
	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public Document getDocument() {
		return this.document;
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
	@Override
	public void start() {
		logger.info("启动--"+this.getClass()+"开始...");
		
	}
	@Override
	public void destroy() {
		logger.info("卸载--"+this.getClass()+"开始...");		
	}
}
