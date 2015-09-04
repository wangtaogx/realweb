package com.tao.realweb.util;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.tao.realweb.conf.system.RealWebConfig;


public class TaskExecutor {

	private static TaskExecutor instance = null;
	private ScheduledExecutorService service ;
	private Map<Runnable,ScheduledFuture<?>> futures = new ConcurrentHashMap<Runnable,ScheduledFuture<?>>();
	private TaskExecutor(){
		 service = Executors.newScheduledThreadPool(RealWebConfig.getInstance().getIntDefault("realweb.handler.threadsize",2));
	}
	public void addScheduled(Runnable command, long initialDelay, long period, TimeUnit unit){
		ScheduledFuture<?> future = service.scheduleAtFixedRate(command, initialDelay, period, unit);
		futures.put(command, future);
	}
	public void removeScheduled(Runnable r){
		ScheduledFuture<?> f = this.futures.get(r);
		f.cancel(true);
		this.futures.remove(r);
	}
	public void execute(Runnable r){
		this.service.submit(r);
	}

	public static TaskExecutor getInstance(){
		if(instance == null)
			instance = new TaskExecutor();
		return instance;
	}
	public void shutdown(){
		service.shutdown();
		futures.clear();
		instance = null;
	}
	
}
