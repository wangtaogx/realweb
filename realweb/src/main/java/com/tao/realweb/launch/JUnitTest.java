package com.tao.realweb.launch;

import java.util.Set;

import org.junit.Test;
import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.codec.SerializationCodec;

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
}
