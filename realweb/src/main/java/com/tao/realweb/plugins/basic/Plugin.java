package com.tao.realweb.plugins.basic;

import org.dom4j.Document;

public interface Plugin {

	public void setDocument(Document document);
	public Document getDocument();
	public void init(PluginManager pluginManager,PluginInfo info);
	public void start();
	public void destroy();
}
