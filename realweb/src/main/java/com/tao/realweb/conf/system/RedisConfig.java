package com.tao.realweb.conf.system;

import java.net.URL;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.codec.RedissonCodec;
import org.redisson.codec.SerializationCodec;

import com.corundumstudio.socketio.store.RedissonStoreFactory;
import com.corundumstudio.socketio.store.pubsub.PubSubStore;
import com.tao.realweb.conf.basic.Preference;
import com.tao.realweb.container.RealWebServer;
import com.tao.realweb.util.StringUtil;

public class RedisConfig implements Preference{

	private  static Config config = null;
	private static Object lock = new Object();
	private static Redisson redisson = null;
	private static RedissonStoreFactory factory = null ;
	private static PubSubStore pubSubStore = null;
	private static Config getConfig(){
		if(config == null){
			synchronized (lock) {
				if(config == null)
					config = new Config();
			}
		}
		return config;
	}
	public static Config createConfig(){
		getConfig().setCodec(new SerializationCodec());
		setThreadSize();
		addServer();
		return config;
	}
	public static Config createConfig(RedissonCodec codec){
		getConfig().setCodec(codec);
		setThreadSize();
		addServer();
		return config;
	}
	private static void addServer(){
		/*String urls = RealWebConfig.getInstance().getString("realweb.redis.url");
		if(!StringUtil.isEmpty(urls)){
			for(String url : urls.split(",")){
				config.addAddress(url);
			}
		}*/
	}
	private static void setThreadSize(){
		/*int size = RealWebConfig.getInstance().getIntDefault("RealWebConfig.properties",2);
		config.setConnectionPoolSize(size);*/
	}
	public static Redisson createRedisson (){
		if(redisson == null){
			synchronized (lock) {
				if(redisson == null)
					redisson = Redisson.create(createConfig());
			}
		}
		return redisson;
	}
	public static RedissonStoreFactory  createRedissonStoreFactory(){
		if(factory == null){
			factory = new RedissonStoreFactory(RedisConfig.createRedisson()){

				@Override
				protected Long getNodeId() {
					//return Long.valueOf(RealWebServer.getInstance().getServerInfo().getHostname().hashCode());
					return 1l;
				}
			
			};
		}
		return factory;
	}
	public static PubSubStore  createPubSubStore(){
		if(factory == null){
			factory =createRedissonStoreFactory();
		}
		if(pubSubStore == null){
			pubSubStore = factory.pubSubStore();
		}
		return pubSubStore;
	}
	public String getNamespace() {
		return null;
	}
	public String getString(String key) {
		return null;
	}
	public String getStringDefault(String key, String defaultValue) {
		return null;
	}
	public int getIntDefault(String key, int defaultValue) {
		return 0;
	}
	public double getDoubleDefault(String key, double defaultValue) {
		return 0;
	}
	public boolean getBooleanDefault(String key, boolean defaultValue) {
		return false;
	}
	public String getTitle() {
		return null;
	}
	public URL getIcon() {
		return null;
	}
	public String getTooltip() {
		return null;
	}
	public String getListLinkName() {
		return null;
	}
	public void putString(String key, Object value) {
		
	}
	@Override
	public void putString(String key, String value) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void putObject(String key, Object value) {
		// TODO Auto-generated method stub
		
	}
}
