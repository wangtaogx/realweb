package com.tao.realweb.modules.system.hazelcast;

import java.io.Serializable;

import com.hazelcast.core.IQueue;
import com.tao.realweb.bean.IQ;

public class ServerQueue implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IQueue<IQ> queue;
	private boolean state;
	private String uuid;
	
	
	public ServerQueue(IQueue<IQ> queue, boolean state, String uuid) {
		this.queue = queue;
		this.state = state;
		this.uuid = uuid;
	}
	public IQueue<IQ> getQueue() {
		return queue;
	}
	public void setQueue(IQueue<IQ> queue) {
		this.queue = queue;
	}
	public boolean isState() {
		return state;
	}
	public void setState(boolean state) {
		this.state = state;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
}
