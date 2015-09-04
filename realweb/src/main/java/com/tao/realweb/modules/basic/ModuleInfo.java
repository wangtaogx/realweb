package com.tao.realweb.modules.basic;

import java.util.HashMap;
import java.util.Map;

public class ModuleInfo {

	private String moduleName;
	private String className;
	private String description;
	private Map<String,String> parameters = new HashMap<String,String>();
	
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
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
