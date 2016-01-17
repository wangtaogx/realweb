package com.tao.realweb.modules.system.hazelcast;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.core.MessageListener;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.bean.JID;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.conf.system.RealWebConstants;
import com.tao.realweb.launch.SwingWorker;
import com.tao.realweb.modules.basic.AbstractModule;
import com.tao.realweb.modules.basic.ModuleInfo;
import com.tao.realweb.modules.basic.ModuleManager;

public class HazelCastManager extends AbstractModule {

	private Config cfg = new Config();
	private HazelcastInstance instance ;
	private Member local = null;
	private Map<String,ServerQueue> otherQueueMap = new ConcurrentHashMap<String,ServerQueue>();
	private Map<String,ITopic<Packet>> topicsMap = new ConcurrentHashMap<String,ITopic<Packet>>();
	private IMap<String,Set<String>> accountsMap = null;
	public HazelCastManager(){
		instance = Hazelcast.newHazelcastInstance(cfg);
	}
	@Override
	public void init(ModuleManager moduleManager, ModuleInfo info) {
		super.init(moduleManager, info);
		this.local = instance.getCluster().getLocalMember();
		moduleManager.getRealWebServer().getServerInfo().setHostname(this.local.getUuid());
		this.accountsMap = instance.getMap(RealWebConstants.USERACCOUNTSMAP);
		addClusterListener();
		Set<Member> members = instance.getCluster().getMembers();
		for(Member m : members){
			if(!m.getUuid().equals(moduleManager.getRealWebServer().getServerInfo().getHostname())){
				IQueue<Packet> queue = instance.getQueue(m.getUuid());
				otherQueueMap.put(m.getUuid(),new ServerQueue(queue,true,m.getUuid()));
			}
		}
		//taskManager = new PacketTaskManager(getModuleManager().getRealWebServer().getRealWebConfig().getIntDefault("realweb.handler.threadsize", 2),myQueue);
		//scheduledExecutorService = Executors.newScheduledThreadPool(getModuleManager().getRealWebServer().getRealWebConfig().getIntDefault("realweb.handler.threadsize",2));
		/*myQueue.addItemListener(new ItemListener<IQ>(){

			public void itemAdded(ItemEvent<IQ> arg0) {
				taskManager.unlock();
			}
			public void itemRemoved(ItemEvent<IQ> arg0) {
				
			}
			
		}, false);*/
		
	}

	private void addClusterListener() {
		instance.getCluster().addMembershipListener(new MembershipListener() {
			
			public void memberRemoved(MembershipEvent arg0) {
				Member m = arg0.getMember();
				otherQueueMap.remove(m.getUuid());
				
			}
			
			
			public void memberAdded(MembershipEvent arg0) {
				Member m = arg0.getMember();
				IQueue<Packet> other = instance.getQueue(m.getUuid());
				ServerQueue q = new ServerQueue(other, true, m.getUuid());
				otherQueueMap.put(m.getUuid(), q);
				
			}
		});
	}

	public void subscribeTopic(String topicString,MessageListener<Packet> listener){
		ITopic<Packet> t = instance.getTopic(topicString);
		t.addMessageListener(listener);
		topicsMap.put(topicString, t);
	}
	
	public void publish(String topicString,Packet packet){
		ITopic<Packet> topic = this.topicsMap.get(topicString);
		if(topic != null){
			topic.publish(packet);
		}
	}
	public void start() {
	}
	public void stop() {
		SwingWorker worker = new SwingWorker() {
			@Override
			public Object construct(){
				while(moduleManager.getModuleSize() > 1){
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				/*if(scheduledExecutorService != null){
					scheduledExecutorService.shutdown();
				}*/
				if(instance != null){
					instance.shutdown();
					instance = null;
				}
				moduleManager.removeModule(HazelCastManager.this);
				return null;
			}
		};
		worker.start();
	}

	public boolean containsServer(String uuid){
		if(local.getUuid().equals(uuid))
			return true;
		if(otherQueueMap.containsKey(uuid))
			return true;
		return false;
	}
	public HazelcastInstance getHazelcastInstance(){
		return instance;
	}
	public void addAccount(String strJID){
		JID jid = JID.formatJID(strJID);
		Set<String> set = this.accountsMap.get(jid.getUsername());
		if(set == null){
			set = new HashSet<String>();
		}
		set.add(strJID);
		this.accountsMap.put(jid.getUsername(), set);
		jid = null;
	}
	public void removeAccount(String strJID){
		JID jid = JID.formatJID(strJID);
		Set<String> set = this.accountsMap.get(jid.getUsername());
		if(set == null){
			return;
		}
		set.remove(strJID);
		this.accountsMap.put(jid.getUsername(), set);
		jid = null;
	}	
}
