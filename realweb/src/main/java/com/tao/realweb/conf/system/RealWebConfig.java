package com.tao.realweb.conf.system;

import java.net.URL;

import org.dom4j.Document;
import org.dom4j.Element;

import com.tao.realweb.conf.basic.AbstractXmlPreference;
import com.tao.realweb.util.Dom4JSupport;
public class RealWebConfig extends AbstractXmlPreference{

	public static final String NAMESPACE = "RealWebSystem";
	public static RealWebConfig instance = null ;
	private static Object lock = new Object();
	private Document document = null;
	private String xmpPath;
	private RealWebConfig(String xmlPath){
		this.xmpPath = xmlPath;
		document = Dom4JSupport.parse(xmlPath);
		this.setRootElement((Element)(document.selectSingleNode("/application/properties")));
	}
	public synchronized static RealWebConfig getInstance(String xmlPath){
		if(instance == null){
			synchronized (lock) {
				if(instance == null){
					instance = new RealWebConfig(xmlPath);
				}
			}
		}
		return instance;
	}
	public synchronized static RealWebConfig getInstance(){
		if(instance != null)
			return instance;
		return null;
	}
	public String getNamespace() {
		return NAMESPACE;
	}

	public String getTitle() {
		return "系统配置";
	}

	public URL getIcon() {
		return null;
	}

	public String getTooltip() {
		return "系统配置";
	}

	public String getListLinkName() {
		return "系统参数配置";
	}
	
	@Override
	protected void writeFlush() {
		Dom4JSupport.writeXML(document, this.xmpPath);
	}
	public Document getDocument() {
		return document;
	}
	public void setDocument(Document document) {
		this.document = document;
	}
	public String getXmpPath() {
		return xmpPath;
	}
	public void setXmpPath(String xmpPath) {
		this.xmpPath = xmpPath;
	}
	
}
