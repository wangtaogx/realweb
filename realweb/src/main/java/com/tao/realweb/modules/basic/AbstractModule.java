package com.tao.realweb.modules.basic;


public abstract  class AbstractModule implements Module {

	protected ModuleManager moduleManager = null;
	protected ModuleInfo moduleInfo = null;
	
	public void init(ModuleManager moduleManager,ModuleInfo info) {
		this.moduleManager = moduleManager;
		this.moduleInfo = info;
	}

	public ModuleInfo getModuleInfo() {
		return moduleInfo;
	}

	public void setModuleInfo(ModuleInfo moduleInfo) {
		this.moduleInfo = moduleInfo;
	}

	public ModuleManager getModuleManager() {
		return moduleManager;
	}

	public void setModuleManager(ModuleManager moduleManager) {
		this.moduleManager = moduleManager;
	}
	

}
