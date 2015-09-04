package com.tao.realweb.conf.basic;

import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

public abstract class AbstractXmlPreference implements Preference{

	protected Element root ;

	public String getString(String key) {
		Element e = root.element(key);
		if(e != null){
			return e.getTextTrim();
		}
		return null;
	}

	@Override
	public void putObject(String key, Object value) {
		if(value != null){
			Element old = root.element("key"); 
			if(old != null){
				old.setText(value.toString());
			}else{
				Element e = new DefaultElement(key);
				e.setText(value.toString());
				root.add(e);
			}
		}
	}

	public void putString(String key,String value){
		putObject(key, value);
		writeFlush();
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

	protected void setRootElement(Element element) {
		this.root = element;
		
	}
	protected abstract void writeFlush();

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
