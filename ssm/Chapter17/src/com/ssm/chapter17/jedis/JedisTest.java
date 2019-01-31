package com.ssm.chapter17.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisTest {
	
	public void testJedis() {
		Jedis jedis = testPool().getResource();
		// Jedis jedis = new Jedis("localhost", 6379); //����Redis
		// jedis.auth("password");//���������
		int i = 0;// ��¼��������
		try {
			long start = System.currentTimeMillis();// ��ʼ������
			while (true) {
				long end = System.currentTimeMillis();
				if (end - start >= 1000) {// �����ڵ���1000���루�൱��1�룩ʱ����������
					break;
				}
				i++;
				jedis.set("test" + i, i + "");
			}
		} finally {// �ر�����
			jedis.close();
		}
		System.out.println("redisÿ�������" + i + "��");// ��ӡ1���ڶ�Redis�Ĳ�������
	}

	private JedisPool testPool() {
		JedisPoolConfig poolCfg = new JedisPoolConfig();
		// ��������
		poolCfg.setMaxIdle(50);
		// ���������
		poolCfg.setMaxTotal(100);
		// ���ȴ�������
		poolCfg.setMaxWaitMillis(20000);
		// ʹ�����ô������ӳ�
		JedisPool pool = new JedisPool(poolCfg, "localhost");
		// �����ӳ��л�ȡ��������
		Jedis jedis = pool.getResource();
		// ���������
		// jedis.auth("password");
		return pool;
	}
}
