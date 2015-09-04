package com.tao.realweb.modules.system.handlers.basic;

public interface RemoteHandlerProvider {

	public String getServerUsername(String namespace);
	public void addServerUsername(String namespace,String serverUsername);
	public void removeServerUsername(String namespace);
}
