package com.tao.realweb.modules.system.handlers.basic;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerInfo {

	private String namespace;
	private String className;
	private String description;
	private Map<String,String> parameters = new ConcurrentHashMap<String, String>();
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getClassName() {
		return className;
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
