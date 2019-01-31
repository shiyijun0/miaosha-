package com.ssm.chapter19.main;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;

import com.ssm.chapter19.redis.pojo.Role;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;

public class Chapter19Main {

	public static void main(String[] args) {
		testTransaction();
		testPipeline();
		testJedisPipeline();
		testPubSub();
		testExpire();
		testLuaScript();
		testRedisScript();
		testLuaFile();
	}

	public static void testTransaction() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		SessionCallback callBack = (SessionCallback) (RedisOperations ops) -> {
			ops.multi();
			ops.boundValueOps("key1").set("value1");
			// ע����������ֻ�ǽ�����У���û�б�ִ�У����Դ˴�����get�����valueȴ����Ϊnull
			String value = (String) ops.boundValueOps("key1").get();
			System.out.println("����ִ�й����У���������У���û�б�ִ�У�����valueΪ�գ�value=" + value);
			// ��ʱlist�ᱣ��֮ǰ������е���������Ľ��
			List list = ops.exec();// ִ������
			// ��������󣬻�ȡvalue1
			value = (String) redisTemplate.opsForValue().get("key1");
			return value;
		};
		// ִ��Redis������
		String value = (String) redisTemplate.execute(callBack);
		System.out.println(value);
	}

	public static void testPipeline() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		// ʹ��Java8��Lambda���ʽ
		SessionCallback callBack = (SessionCallback) (RedisOperations ops) -> {
			for (int i = 0; i < 100000; i++) {
				int j = i + 1;
				ops.boundValueOps("pipeline_key_" + j).set("pipeline_value_" + j);
				ops.boundValueOps("pipeline_key_" + j).get();
			}
			return null;
		};
		long start = System.currentTimeMillis();
		// ִ��Redis����ˮ������
		List resultList = redisTemplate.executePipelined(callBack);
		long end = System.currentTimeMillis();
		System.err.println(end - start);
	}

	private static JedisPool getPool() {
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

	public static void testJedisPipeline() {
		JedisPool pool = getPool();
		Jedis jedis = pool.getResource();
		long start = System.currentTimeMillis();
		// ������ˮ��
		Pipeline pipeline = jedis.pipelined();
		// �������10�����Ķ�д2������
		for (int i = 0; i < 100000; i++) {
			int j = i + 1;
			pipeline.set("pipeline_key_" + j, "pipeline_value_" + j);
			pipeline.get("pipeline_key_" + j);
		}
		// pipeline.sync();//����ִֻ��ͬ�������ǲ����ؽ��
		// pipeline.syncAndReturnAll();������ִ�й�������ص�List�б���
		List result = pipeline.syncAndReturnAll();
		long end = System.currentTimeMillis();
		// �����ʱ
		System.err.println("��ʱ��" + (end - start) + "����");
	}

	public static void testPubSub() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		String channel = "chat";
		redisTemplate.convertAndSend(channel, "I am lazy!!");
	}

	public static void testExpire() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		redisTemplate.execute((RedisOperations ops) -> {
			ops.boundValueOps("key1").set("value1");
			String keyValue = (String) ops.boundValueOps("key1").get();
			Long expSecond = ops.getExpire("key1");
			System.err.println(expSecond);
			boolean b = false;
			b = ops.expire("key1", 120L, TimeUnit.SECONDS);
			b = ops.persist("key1");
			Long l = 0L;
			l = ops.getExpire("key1");
			Long now = System.currentTimeMillis();
			Date date = new Date();
			date.setTime(now + 120000);
			ops.expireAt("key", date);
			return null;
		});
	}

	public static void testLuaScript() {
		// ����Ǽ򵥵Ķ���ʹ��ԭ���ķ�װ�����Щ
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		// ����Ǽ򵥵Ĳ�����ʹ��ԭ����Jedis�����Щ
		Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
		// ִ�м򵥵Ľű�
		String helloJava = (String) jedis.eval("return 'hello java'");
		System.out.println(helloJava);
		// ִ�д������Ľű�
		jedis.eval("redis.call('set',KEYS[1], ARGV[1])", 1, "lua-key", "lua-value");
		String luaKey = (String) jedis.get("lua-key");
		System.out.println(luaKey);
		// ����ű�������sha1ǩ����ʶ
		String sha1 = jedis.scriptLoad("redis.call('set',KEYS[1], ARGV[1])");
		// ͨ����ʶִ�нű�
		jedis.evalsha(sha1, 1, new String[] { "sha-key", "sha-val" });
		// ��ȡִ�нű��������
		String shaVal = jedis.get("sha-key");
		System.out.println(shaVal);
		// �ر�����
		jedis.close();
	}

	public static void testRedisScript() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		// ����Ĭ�Ͻű���װ��
		DefaultRedisScript<Role> redisScript = new DefaultRedisScript<Role>();
		// ���ýű�
		redisScript.setScriptText("redis.call('set', KEYS[1], ARGV[1])  return redis.call('get', KEYS[1])");
		// ���������key�б�
		List<String> keyList = new ArrayList<String>();
		keyList.add("role1");
		// ��Ҫ���л�����Ͷ�ȡ�Ķ���
		Role role = new Role();
		role.setId(1L);
		role.setRoleName("role_name_1");
		role.setNote("note_1");
		// ��ñ�ʶ�ַ���
		String sha1 = redisScript.getSha1();
		System.out.println(sha1);
		// ���÷��ؽ�����ͣ����û����仰���������Ϊ��
		redisScript.setResultType(Role.class);
		// �������л���
		JdkSerializationRedisSerializer serializer = new JdkSerializationRedisSerializer();
		// ִ�нű�
		// ��һ����RedisScript�ӿڶ��󣬵ڶ����ǲ������л���
		// �������ǽ�����л��������ĸ���Reids��key�б�����ǲ����б�
		Role obj = (Role) redisTemplate.execute(redisScript, serializer, serializer, keyList, role);
		// ��ӡ���
		System.out.println(obj);
	}

	public static void testLuaFile() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		// �����ļ���
		File file = new File("G:\\dev\\redis\\test.lua");
		byte[] bytes = getFileToByte(file);
		Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();
		// �����ļ������Ƹ�Redis������REdis�ͻ᷵��sha1��ʶ
		byte[] sha1 = jedis.scriptLoad(bytes);
		// ʹ�÷��صı�ʶִ�У����еڶ�������2����ʾʹ��2����
		// ��������ַ�����ת��Ϊ�˶������ֽڽ��д���
		Object obj = jedis.evalsha(sha1, 2, "key1".getBytes(), "key2".getBytes(), "2".getBytes(), "4".getBytes());
		System.out.println(obj);
	}

	/**
	 * ���ļ�ת��Ϊ����������
	 * 
	 * @param file
	 *            �ļ�
	 * @return ����������
	 */
	public static byte[] getFileToByte(File file) {
		byte[] by = new byte[(int) file.length()];
		try {
			InputStream is = new FileInputStream(file);
			ByteArrayOutputStream bytestream = new ByteArrayOutputStream();
			byte[] bb = new byte[2048];
			int ch;
			ch = is.read(bb);
			while (ch != -1) {
				bytestream.write(bb, 0, ch);
				ch = is.read(bb);
			}
			by = bytestream.toByteArray();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return by;
	}
}
