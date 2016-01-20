package com.tao.realweb.plugins.system.example;

import com.tao.realweb.plugins.basic.AbstractPlugin;

public class ExamplePlugin extends AbstractPlugin{

	@Override
	public void start() {
		logger.debug(getPluginInfo().getClassName()+" start......................");
	}
}
