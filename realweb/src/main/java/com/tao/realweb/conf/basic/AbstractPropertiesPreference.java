package com.tao.realweb.conf.basic;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractPropertiesPreference implements Preference{

	protected ResourceBundle prb = getResourceBundle();
	protected Map<String,Object> customMap = new ConcurrentHashMap<String, Object>();

	public String getString(String key) {
		Object result = getObject(key);
		if(result instanceof String)
			return (String)result;
		return null;
	}

	private  Object getObject(String key){
		Object result = prb.getString(key);
		if(result == null){
			result = customMap.get(key);
		}
		return result;
	}
	public void putString(String key,String value){
		putObject(key, value);
	}
	public void putObject(String key,Object value){
		if(customMap.containsKey(key)){
			customMap.put(key, value);
		}
		else if(prb.containsKey(key)){
			throw new UnsupportedOperationException("该参数是正在运行不可以修改");
		}
		else{
			customMap.put(key, value);
		}
	}
	public String getStringDefault(String key,String defaultValue) {
		try{
			String result = getString(key);
			if(result != null){
				return result;
			}
			return defaultValue;
		}catch(Exception e){
			return defaultValue;
		}
	}

	protected abstract ResourceBundle getResourceBundle() ;

	public int getIntDefault(String key, int defaultValue) {
		try{
			String result = getString(key);
			if(result != null){
				int value = Integer.valueOf(result);
				return value;
			}
			return defaultValue;
		}catch(Exception e){
			return defaultValue;
		}
	}

	public double getDoubleDefault(String key, double defaultValue) {
		try{
			String result = getString(key);
			if(result != null){
				double value = Double.valueOf(result);
				return value;
			}
			return defaultValue;
		}catch(Exception e){
			return defaultValue;
		}
	}

	public boolean getBooleanDefault(String key, boolean defaultValue) {
		try{
			String result = getString(key);
			if(result != null){
				boolean value = Boolean.valueOf(result);
				return value;
			}
			return defaultValue;
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	
	
}
