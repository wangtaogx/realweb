package com.tao.realweb.modules.system.interceptors.basic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InterceptorInfo {

	private String name;
	private String className;
	private String description;
	private Map<String,String> parameters = new ConcurrentHashMap<String, String>();

	public String getClassName() {
		return className;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void addParameter(String key,String value){
		this.parameters.put(key, value);
	}
	public String getParameter(String key){
		return this.parameters.get(key);
	}
	
}
