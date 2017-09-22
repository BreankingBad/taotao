package com.taotao.jedis;

import java.util.HashSet;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sun.tools.classfile.Opcode.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;


public class TestJedisClientPool {

	@Test
	public void testJedisClientPool() {
		
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		
		JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
		jedisClient.set("jedisClient", "test1");
		
		String result = jedisClient.get("jedisClient");
		System.out.println(result);
		
	}
}
