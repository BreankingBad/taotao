package com.taotao.jedis;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestJedisClientPool {

	@Test
	public void testJedisClientPool() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");
		
		JedisClient jedisClient = applicationContext.getBean(JedisClient.class);
		jedisClient.set("jedisClient", "test");
		
		String result = jedisClient.get("jedisClient");
		System.out.println(result);
	}
}
