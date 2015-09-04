package com.tao.realweb.launch;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.SerializationCodec;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tao.realweb.bean.Message;
import com.tao.realweb.bean.Packet;
import com.tao.realweb.bean.PacketError.Condition;
import com.tao.realweb.bean.User;


public class JUnitTest {
	private Config config = new Config();
	public void init(){
	        config.setConnectionPoolSize(2);
	        config.addAddress("192.168.232.2:6379");
	}
	public JUnitTest(){
		init();
	}
	@Test
	public void  testJsonInsert(){
		config.setCodec(new JsonJacksonCodec());
		  Redisson redisson = Redisson.create(config);
        Set<User> set = redisson.getSet("handler1");
        set.add(new User("a",1));
        set.add(new User("b",2));
        redisson.shutdown();
	}
	@Test
	public void  testJsonGet(){
		config.setCodec(new JsonJacksonCodec());
		Redisson redisson = Redisson.create(config);
        Set<User> set = redisson.getSet("handler1");
        for(User u : set){
        	System.out.println(u.getName());
        }
        redisson.shutdown();
	}
	@Test
	public void  testSeriableInsert(){
		config.setCodec(new SerializationCodec());
		  Redisson redisson = Redisson.create(config);
        Set<User> set = redisson.getSet("handler3");
        set.add(new User("a",1));
        set.add(new User("b",2));
        redisson.shutdown();
	}
	@Test
	public void  testSeriableGet(){
		config.setCodec(new SerializationCodec());
		Redisson redisson = Redisson.create(config);
        Set<User> set = redisson.getSet("handler3");
        for(User u : set){
        	System.out.println(u.getName());
        }
        redisson.shutdown();
	}
	
	public void testJackson() throws IOException{
		Message message = new Message();
		message.setFrom("wangtao");
		message.setTo("wangtao2");
		message.setPacketID(Packet.nextID());
		message.setBody("bodyTEst");
		message.setType("nonmail"); 
		message.setError(new com.tao.realweb.bean.PacketError(Condition.bad_request));
		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		String jsonStr = mapper.writeValueAsString(message);
		System.out.println(jsonStr);
		
		Message map = mapper.readValue(jsonStr, Message.class);
		
		System.out.println(map.getPacketID());
		System.out.println(map.getFrom());
		System.out.println(map.getTo());
		System.out.println(map.getType());
		System.out.println(map.getError().toString());
		System.out.println(map.getBody());
	}
}
