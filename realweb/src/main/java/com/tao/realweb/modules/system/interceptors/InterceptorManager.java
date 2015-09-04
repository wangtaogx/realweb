package com.tao.realweb.modules.system.interceptors;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tao.realweb.bean.Packet;
import com.tao.realweb.modules.basic.AbstractModule;
import com.tao.realweb.modules.basic.ModuleInfo;
import com.tao.realweb.modules.basic.ModuleManager;
import com.tao.realweb.modules.system.interceptors.basic.Interceptor;
import com.tao.realweb.modules.system.interceptors.basic.InterceptorInfo;

public class InterceptorManager extends AbstractModule {

	private static Logger logger = LoggerFactory.getLogger(InterceptorManager.class);
	private List<Interceptor> globalInterceptors = new  CopyOnWriteArrayList<Interceptor>();
	private Map<String,List<Interceptor>> userInterceptors = new ConcurrentHashMap<String, List<Interceptor>>();
	private List<Element> interceptors;
	public InterceptorManager(){
	}
	public void init(){
		logger.info("初始化--Interceptors开始...");
		Document document = getModuleManager().getRealWebServer().getRealWebConfig().getDocument();
		if(document != null){
			interceptors = document.selectNodes("/application/interceptors/interceptor");
			if(interceptors != null){
				for(Element ele : interceptors){
					String className = ele.elementText("classname");
					try{
						Class clazz = Class.forName(className);
						Object interceptor = clazz.newInstance();
						globalInterceptors.add((Interceptor)interceptor);
					}catch(Exception e){
						logger.error("初始化Handler:"+className+"异常，类错误");
					}
				}
			}
		}
		for(Element ele : interceptors){
			String name = ele.elementText("name");
			String className = ele.elementText("classname");
			String description = ele.elementText("description");
			Element parameters = ele.element("parameters");
			try{
				InterceptorInfo info = new InterceptorInfo();
				info.setName(name);
				info.setDescription(description);
				info.setClassName(className);
				if(parameters != null){
					List<Element> elements = parameters.elements();
					for(Element e : elements){
						info.addParameter(e.getName(), e.getTextTrim());
					}
				}
				for(Interceptor interceptor:globalInterceptors){
					interceptor.init(this,info);
				}
			}catch(Exception e){
				e.printStackTrace();
				logger.info("初始化错误"+className);
			}
		}
		logger.info("初始化--Interceptors结束");
	}
	public void fireGlobalDecode(Packet packet){
		for(Interceptor i :globalInterceptors){
			i.decode(packet);
		}
	}
	public void fireGlobalEncode(Packet packet){
		for(Interceptor i :globalInterceptors){
			i.encode(packet);
		}
	}
	public void fireUserDecode(Packet packet){
		List<Interceptor> interceptors = userInterceptors.get(packet.getFrom());
		if(interceptors != null){
			for(Interceptor i :interceptors){
				i.decode(packet);
			}
		}
	}
	public void fireUserEncode(Packet packet){
		List<Interceptor> interceptors = userInterceptors.get(packet.getTo());
		if(interceptors != null && interceptors.size() > 0){
			for(Interceptor i :interceptors){
				i.encode(packet);
			}
		}
	}
	public void addInterceptor(String username,Interceptor interceptor){
		List<Interceptor> interceptors = userInterceptors.get(username);
		if(interceptors == null){
			interceptors = new CopyOnWriteArrayList<Interceptor>();
		}
		interceptors.add(interceptor);
	}
	
	@Override
	public void init(ModuleManager moduleManager, ModuleInfo info) {
		super.init(moduleManager, info);
		init();
	}
	public void start() {
		
	}
	public void stop() {
		this.moduleManager.removeModule(this);
	}

}
