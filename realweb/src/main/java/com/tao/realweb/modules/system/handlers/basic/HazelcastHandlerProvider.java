package com.tao.realweb.modules.system.handlers.basic;

import java.util.Set;

import org.redisson.Redisson;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.tao.realweb.conf.system.RealWebConstants;
import com.tao.realweb.conf.system.RedisConfig;
import com.tao.realweb.container.RealWebServer;
import com.tao.realweb.container.RealWebServerInfo;

public class HazelcastHandlerProvider implements RemoteHandlerProvider {

	private HazelcastInstance instance;
	public HazelcastHandlerProvider(HazelcastInstance instance){
		this.instance = instance;
	}
	public String getServerUsername(String namespace) {
		IMap<String,String> handlers = instance.getMap(RealWebConstants.HANDLERMAP);
		return handlers.get(namespace);
	}
	public void addServerUsername(String namespace, String serverUsername) {
		IMap<String,String> handlers = instance.getMap(RealWebConstants.HANDLERMAP);
		handlers.put(namespace, instance.getCluster().getLocalMember().getUuid());
	}
	public void removeServerUsername(String namespace) {
		IMap<String,String> handlers = instance.getMap(RealWebConstants.HANDLERMAP);
		handlers.remove(namespace);
	}

}
