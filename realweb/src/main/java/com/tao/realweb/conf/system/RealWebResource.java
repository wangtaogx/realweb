package com.tao.realweb.conf.system;

import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import com.tao.realweb.conf.basic.AbstractPropertiesPreference;
import com.tao.realweb.util.StringUtil;
public class RealWebResource extends AbstractPropertiesPreference{

	public static final String NAMESPACE = "RealWebResource";
	public static RealWebResource instance = null ;
	private static Object lock = new Object();
	public synchronized static RealWebResource getInstance(){
		if(instance == null){
			synchronized (lock) {
				if(instance == null){
					instance = new RealWebResource();
				}
			}
		}
		return instance;
	}
	public String getNamespace() {
		return NAMESPACE;
	}

	public String getTitle() {
		return "系统资源";
	}

	public URL getIcon() {
		return null;
	}

	public String getTooltip() {
		return "系统资源";
	}

	public String getListLinkName() {
		return "系统资源";
	}
	public ImageIcon getImageResource(String key){
		String resourcePath = getString(key);
		if(!StringUtil.isEmpty(key)){
			URL url = getClass().getClassLoader().getResource(resourcePath);
			return new ImageIcon(url);
		}
		return null;
	}
	@Override
	protected ResourceBundle getResourceBundle() {
		return ResourceBundle.getBundle("com/tao/realweb/conf/system/RealWebResource");
	}
	
}
