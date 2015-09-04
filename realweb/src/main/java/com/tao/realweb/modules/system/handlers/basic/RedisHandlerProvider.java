package com.tao.realweb.modules.system.handlers.basic;

import java.util.Set;

import org.redisson.Redisson;

import com.tao.realweb.conf.system.RedisConfig;
import com.tao.realweb.container.RealWebServer;
import com.tao.realweb.container.RealWebServerInfo;

public class RedisHandlerProvider implements RemoteHandlerProvider {

	private Redisson redisson;
	public RedisHandlerProvider(){
		this.redisson = RedisConfig.createRedisson();
	}
	public String getServerUsername(String namespace) {
		Set<String> set = redisson.getSet(namespace);
		if(set == null || set.size() <= 0){
			return null;
		}else{
			return set.iterator().next();
		}
	}
	public void addServerUsername(String namespace, String serverUsername) {
		Set<String> set = redisson.getSet(namespace);
		//set.add(RealWebServer.getInstance().getServerInfo().getServerUsername());
	}
	public void removeServerUsername(String namespace,String serverusername) {
		Set<String> set = redisson.getSet(namespace);
		set.remove(serverusername);
	
	}
	public void removeServerUsername(String namespace) {
		//removeServerUsername(namespace,RealWebServer.getInstance().getServerInfo().getServerUsername());
	
	}

}
