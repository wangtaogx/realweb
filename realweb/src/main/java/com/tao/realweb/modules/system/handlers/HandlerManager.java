package com.tao.realweb.modules.system.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.modules.basic.AbstractModule;
import com.tao.realweb.modules.basic.ModuleInfo;
import com.tao.realweb.modules.basic.ModuleManager;
import com.tao.realweb.modules.system.handlers.basic.Handler;
import com.tao.realweb.modules.system.handlers.basic.HandlerInfo;
import com.tao.realweb.modules.system.handlers.basic.HandlerListener;
import com.tao.realweb.modules.system.handlers.basic.HazelcastHandlerProvider;
import com.tao.realweb.modules.system.handlers.basic.RemoteHandlerProvider;

public class HandlerManager extends AbstractModule {

	private Logger logger = LoggerFactory.getLogger(HandlerManager.class); 
	private Map<String,Handler> handlersMap = new ConcurrentHashMap<String, Handler>();
	private List<Element> handlers;
	private List<HandlerListener> listeners = new ArrayList<HandlerListener>();
	private RemoteHandlerProvider provider;
	public HandlerManager(){
	}
	/*private void initHandlerListener() {
		this.addHandlerListener(new HandlerListener(){

			public void handlerAdded(String namespace) {
				Redisson redisson = RedisConfig.createRedisson();
				Set<RealWebServerInfo> set = redisson.getSet(namespace);
				set.add(RealWebServer.getInstance().getServerInfo());
				//redisson.shutdown();
			}

			public void handlerRemoved(String namespace) {
				Redisson redisson = RedisConfig.createRedisson();
				Set<RealWebServerInfo> set = redisson.getSet(namespace);
				set.remove(RealWebServer.getInstance().getServerInfo());
				//redisson.shutdown();
			}

			public void handlerReloadToCache() {
				Redisson redisson = RedisConfig.createRedisson();
				for(Handler h :handlersMap.values() ){
					Set<RealWebServerInfo> set = redisson.getSet(h.getHandlerInfo().getNamespace());
					set.add(RealWebServer.getInstance().getServerInfo());
				}
				
			}
			
		});
	}*/
	public void init(){
		logger.info("初始化--Handler开始...");
		Document document = getModuleManager().getRealWebServer().getRealWebConfig().getDocument();
		if(document != null){
			handlers = document.selectNodes("/application/handlers/handler");
			if(handlers != null){
				for(Element ele : handlers){
					String namespace = ele.elementText("namespace");
					String className = ele.elementText("classname");
					try{
						Class clazz = Class.forName(className);
						Object handler = clazz.newInstance();
						putHandler(namespace, (Handler)handler);
					}catch(Exception e){
						logger.error("初始化Handler:"+className+"异常，类错误");
					}
				}
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
				Handler handler = handlersMap.get(namespace);
				if(handler != null)
					handler.init(this, info);
			}catch(Exception e){
				e.printStackTrace();
				handlersMap.remove(namespace);
				logger.error("初始化错误"+className);
			}
		}
		logger.info("初始化--Handler结束");
	}
	public void putHandler(String namespace,Handler handler){
		handlersMap.put(namespace, handler);
		//fireHandlerListenerAdded(namespace);
		this.addRemoteServerUsername(namespace);
	}
	public void removeHandler(String namespace){
		handlersMap.remove(namespace);
		//fireHandlerListenerRemoved(namespace);
		this.removeRemoteServerUsername(namespace);
	}
	public Handler getHandler(String namespace){
		return handlersMap.get(namespace);
	}
	public Set<String> getHandlerNamespaces(){
		return handlersMap.keySet();
	}
	public void addHandlerListener(HandlerListener listener){
		this.listeners.add(listener);
	}
	public void removeHandlerListener(HandlerListener listener){
		this.listeners.remove(listener);
	}
	private void fireHandlerListenerAdded(String namespace){
		for(HandlerListener listener : listeners){
			listener.handlerAdded(namespace);
		}
	}
	private void fireHandlerListenerRemoved(String namespace){
		for(HandlerListener listener : listeners){
			listener.handlerRemoved(namespace);
		}
	}
	public void fireHandlerListenerReloadToCache(){
		for(HandlerListener listener : listeners){
			listener.handlerReloadToCache();
		}
	}
	
	public String getRemoteServerUsername(String namespace){
		if(provider != null){
			return provider.getServerUsername(namespace);
		}
		return null;
	}
	public void addRemoteServerUsername(String namespace){
		if(provider == null){
			return;
		}
		provider.addServerUsername(namespace,getModuleManager().getRealWebServer().getServerInfo().getServerUsername());
	}
	public void removeRemoteServerUsername(String namespace){
		if(provider == null){
			return;
		}
		provider.removeServerUsername(namespace);
	}
	public boolean containsHandler(String namespace){
		return handlersMap.containsKey(namespace);
	}
	
	@Override
	public void init(ModuleManager moduleManager, ModuleInfo info) {
		super.init(moduleManager, info);
		try{
			provider = new HazelcastHandlerProvider(getModuleManager().getHazelCastManager().getHazelcastInstance());
			init();
		}catch(Exception e){
			e.printStackTrace();
			provider = null;
		}
	}
	public void start() {
		
	}
	public void stop() {
		this.moduleManager.removeModule(this);
	}
}
