package com.ssm.chapter20.main;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

public class Chapter20Main {

	public static void main(String[] args) {
//		testSentinel();
//		testSpringSentinel();

	}

	public static void testSentinel() {
		// ���ӳ�����
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		jedisPoolConfig.setMaxTotal(10);
		jedisPoolConfig.setMaxIdle(5);
		jedisPoolConfig.setMinIdle(5);
		// �ڱ���Ϣ
		Set<String> sentinels = new HashSet<String>(
				Arrays.asList("192.168.11.128:26379", "192.168.11.129:26379", "192.168.11.130:26379"));
		// �������ӳ�
		// mymaster���������ø��ڱ��ķ�������
		// sentinels���ڱ���Ϣ
		// jedisPoolConfig�����ӳ�����
		// abcdefg������Redis������������
		JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels, jedisPoolConfig, "abcdefg");
		// ��ȡ�ͻ���
		Jedis jedis = pool.getResource();
		// ִ����������
		jedis.set("mykey", "myvalue");
		String myvalue = jedis.get("mykey");
		// ��ӡ��Ϣ
		System.out.println(myvalue);
	}

	public static void testSpringSentinel() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("com/ssm/chapter20/config/spring-cfg.xml");
		RedisTemplate redisTemplate = ctx.getBean(RedisTemplate.class);
		String retVal = (String) redisTemplate.execute((RedisOperations ops) -> {
			ops.boundValueOps("mykey").set("myvalue");
			String value = (String) ops.boundValueOps("mykey").get();
			return value;
		});
		System.out.println(retVal);
	}

}
