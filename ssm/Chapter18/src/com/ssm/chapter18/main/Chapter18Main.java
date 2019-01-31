package com.ssm.chapter18.main;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.connection.RedisListCommands;
import org.springframework.data.redis.connection.RedisZSetCommands.Limit;
import org.springframework.data.redis.connection.RedisZSetCommands.Range;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public class Chapter18Main {

	public static void main(String[] args) {
		testString();
		testCal();
		testRedisHash();
		testList();
		testBList();
		testSet();
		testZset();
		testHyperLogLog();
	}

	public static void testString() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		// ��ֵ
		redisTemplate.opsForValue().set("key1", "value1");
		redisTemplate.opsForValue().set("key2", "value2");
		// ͨ��key��ȡֵ
		String value1 = (String) redisTemplate.opsForValue().get("key1");
		System.out.println(value1);
		// ͨ��keyɾ��ֵ
		redisTemplate.delete("key1");
		// �󳤶�
		Long length = redisTemplate.opsForValue().size("key2");
		System.out.println(length);
		// ��ֵ��ֵ�����ؾ�ֵ
		String oldValue2 = (String) redisTemplate.opsForValue().getAndSet("key2", "new_value2");
		System.out.println(oldValue2);
		// ͨ��key��ȡֵ.
		String value2 = (String) redisTemplate.opsForValue().get("key2");
		System.out.println(value2);
		// ���Ӵ�
		String rangeValue2 = redisTemplate.opsForValue().get("key2", 0, 3);
		System.out.println(rangeValue2);
		// ׷���ַ�����ĩβ�������´�����
		int newLen = redisTemplate.opsForValue().append("key2", "_app");
		System.out.println(newLen);
		String appendValue2 = (String) redisTemplate.opsForValue().get("key2");
		System.out.println(appendValue2);
	}

	/**
	 * ����Redis����.
	 */
	public static void testCal() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		redisTemplate.opsForValue().set("i", "9");
		printCurrValue(redisTemplate, "i");
		redisTemplate.opsForValue().increment("i", 1);
		printCurrValue(redisTemplate, "i");
		redisTemplate.getConnectionFactory().getConnection().decr(redisTemplate.getKeySerializer().serialize("i"));
		printCurrValue(redisTemplate, "i");
		redisTemplate.getConnectionFactory().getConnection().decrBy(redisTemplate.getKeySerializer().serialize("i"), 6);
		printCurrValue(redisTemplate, "i");
		redisTemplate.opsForValue().increment("i", 2.3);
		printCurrValue(redisTemplate, "i");
	}

	/**
	 * ��ӡ��ǰkey��ֵ
	 * 
	 * @param redisTemplate
	 *            spring RedisTemplate
	 * @param key��
	 */
	public static void printCurrValue(RedisTemplate redisTemplate, String key) {
		String i = (String) redisTemplate.opsForValue().get(key);
		System.err.println(i);
	}

	public static void testRedisHash() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		String key = "hash";
		Map<String, String> map = new HashMap<String, String>();
		map.put("f1", "val1");
		map.put("f2", "val2");
		// �൱��hmset����
		redisTemplate.opsForHash().putAll(key, map);
		// �൱��hset����
		redisTemplate.opsForHash().put(key, "f3", "6");
		printValueForhash(redisTemplate, key, "f3");
		// �൱�� hexists key filed����
		boolean exists = redisTemplate.opsForHash().hasKey(key, "f3");
		System.out.println(exists);
		// �൱��hgetall����
		Map keyValMap = redisTemplate.opsForHash().entries(key);
		// �൱��hincrby����
		redisTemplate.opsForHash().increment(key, "f3", 2);
		printValueForhash(redisTemplate, key, "f3");
		// �൱��hincrbyfloat����
		redisTemplate.opsForHash().increment(key, "f3", 0.88);
		printValueForhash(redisTemplate, key, "f3");
		// �൱��hvals����
		List valueList = redisTemplate.opsForHash().values(key);
		// �൱��hkeys����
		Set keyList = redisTemplate.opsForHash().keys(key);
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("f1");
		fieldList.add("f2");
		// �൱��hmget����
		List valueList2 = redisTemplate.opsForHash().multiGet(key, keyList);
		// �൱��hsetnx����
		boolean success = redisTemplate.opsForHash().putIfAbsent(key, "f4", "val4");
		System.out.println(success);
		// �൱��hdel����
		Long result = redisTemplate.opsForHash().delete(key, "f1", "f2");
		System.out.println(result);
	}

	private static void printValueForhash(RedisTemplate redisTemplate, String key, String field) {
		// �൱��hget����
		Object value = redisTemplate.opsForHash().get(key, field);
		System.out.println(value);
	}

	public static void testList() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		try {
			// ɾ�������Ա����ǿ��Է�������
			redisTemplate.delete("list");
			// ��node3��������list
			redisTemplate.opsForList().leftPush("list", "node3");
			List<String> nodeList = new ArrayList<String>();
			for (int i = 2; i >= 1; i--) {
				nodeList.add("node" + i);
			}
			// �൱��lpush�Ѷ����ֵ�����������
			redisTemplate.opsForList().leftPushAll("list", nodeList);
			// ���ұ߲���һ���ڵ�
			redisTemplate.opsForList().rightPush("list", "node4");
			// ��ȡ�±�Ϊ0�Ľڵ�
			String node1 = (String) redisTemplate.opsForList().index("list", 0);
			// ��ȡ������
			long size = redisTemplate.opsForList().size("list");
			// ����ߵ���һ���ڵ�
			String lpop = (String) redisTemplate.opsForList().leftPop("list");
			// ���ұߵ���һ���ڵ�
			String rpop = (String) redisTemplate.opsForList().rightPop("list");
			// ע�⣬��Ҫʹ�ø�Ϊ�ײ��������ܲ���linsert����
			// ʹ��linsert������node2ǰ����һ���ڵ�
			redisTemplate.getConnectionFactory().getConnection().lInsert("list".getBytes("utf-8"),
					RedisListCommands.Position.BEFORE, "node2".getBytes("utf-8"), "before_node".getBytes("utf-8"));
			// ʹ��linsert������node2�����һ���ڵ�
			redisTemplate.getConnectionFactory().getConnection().lInsert("list".getBytes("utf-8"),
					RedisListCommands.Position.AFTER, "node2".getBytes("utf-8"), "after_node".getBytes("utf-8"));
			// �ж�list�Ƿ���ڣ�������������߲���head�ڵ�
			redisTemplate.opsForList().leftPushIfPresent("list", "head");
			// �ж�list�Ƿ���ڣ������������ұ߲���end�ڵ�
			redisTemplate.opsForList().rightPushIfPresent("list", "end");
			// �����ң������±��0��10�Ľڵ�Ԫ��
			List valueList = redisTemplate.opsForList().range("list", 0, 10);
			nodeList.clear();
			for (int i = 1; i <= 3; i++) {
				nodeList.add("node");
			}
			// ��������߲�������ֵΪnode�Ľڵ�
			redisTemplate.opsForList().leftPushAll("list", nodeList);
			// ������ɾ����������node�ڵ�
			redisTemplate.opsForList().remove("list", 3, "node");
			// �������±�Ϊ0�Ľڵ�������ֵ
			redisTemplate.opsForList().set("list", 0, "new_head_value");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		// ��ӡ��������
		printList(redisTemplate, "list");
	}

	public static void printList(RedisTemplate redisTemplate, String key) {
		// ������
		Long size = redisTemplate.opsForList().size(key);
		// ��ȡ���������ֵ
		List valueList = redisTemplate.opsForList().range(key, 0, size);
		// ��ӡ
		System.out.println(valueList);
	}

	public static void testBList() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		// ������ݣ������ظ�����
		redisTemplate.delete("list1");
		redisTemplate.delete("list2");
		// ��ʼ������list1
		List<String> nodeList = new ArrayList<String>();
		for (int i = 1; i <= 5; i++) {
			nodeList.add("node" + i);
		}
		redisTemplate.opsForList().leftPushAll("list1", nodeList);
		// Springʹ�ò�����ʱʱ����Ϊ�����������֣��ȼ���blpop������ҿ�������ʱ�����
		redisTemplate.opsForList().leftPop("list1", 1, TimeUnit.SECONDS);
		// Springʹ�ò�����ʱʱ����Ϊ�����������֣��ȼ���brpop������ҿ�������ʱ�����
		redisTemplate.opsForList().rightPop("list1", 1, TimeUnit.SECONDS);
		nodeList.clear();
		// ��ʼ������list2
		for (int i = 1; i <= 3; i++) {
			nodeList.add("data" + i);
		}
		redisTemplate.opsForList().leftPushAll("list2", nodeList);
		// �൱��rpoplpush�������list1���ұߵĽڵ㣬���뵽list2�����
		redisTemplate.opsForList().rightPopAndLeftPush("list1", "list2");
		// �൱��brpoplpush���ע����Spring��ʹ�ó�ʱ��������
		redisTemplate.opsForList().rightPopAndLeftPush("list1", "list2", 1, TimeUnit.SECONDS);
		// ��ӡ��������
		printList(redisTemplate, "list1");
		printList(redisTemplate, "list2");
	}

	public static void testSet() {
		// ���RedisTemplateֵ���л�������ΪStringRedisSerializer���Ըô���Ƭ��
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		Set set = null;
		// ��Ԫ�ؼ����б�
		redisTemplate.boundSetOps("set1").add("v1", "v2", "v3", "v4", "v5", "v6");
		redisTemplate.boundSetOps("set2").add("v0", "v2", "v4", "v6", "v8");
		// �󼯺ϳ���
		redisTemplate.opsForSet().size("set1");
		// ��
		set = redisTemplate.opsForSet().difference("set1", "set2");
		// �󲢼�
		set = redisTemplate.opsForSet().intersect("set1", "set2");
		// �ж��Ƿ񼯺��е�Ԫ��
		boolean exists = redisTemplate.opsForSet().isMember("set1", "v1");
		// ��ȡ��������Ԫ��
		set = redisTemplate.opsForSet().members("set1");
		// �Ӽ������������һ��Ԫ��
		String val = (String) redisTemplate.opsForSet().pop("set1");
		// �����ȡһ�����ϵ�Ԫ��
		val = (String) redisTemplate.opsForSet().randomMember("set1");
		// �����ȡ2�����ϵ�Ԫ��
		List list = redisTemplate.opsForSet().randomMembers("set1", 2L);
		// ɾ��һ�����ϵ�Ԫ�أ����������Ƕ��
		redisTemplate.opsForSet().remove("set1", "v1");
		// ���������ϵĲ���
		redisTemplate.opsForSet().union("set1", "set2");
		// ���������ϵĲ�������浽����diff_set��
		redisTemplate.opsForSet().differenceAndStore("set1", "set2", "diff_set");
		// ���������ϵĽ����������浽����inter_set��
		redisTemplate.opsForSet().intersectAndStore("set1", "set2", "inter_set");
		// ���������ϵĲ����������浽����union_set��
		redisTemplate.opsForSet().unionAndStore("set1", "set2", "union_set");
	}

	public static void testZset() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		// Spring�ṩ�ӿ�TypedTuple�������򼯺�
		Set<TypedTuple> set1 = new HashSet<TypedTuple>();
		Set<TypedTuple> set2 = new HashSet<TypedTuple>();
		int j = 9;
		for (int i = 1; i <= 9; i++) {
			j--;
			// ���������ֵ
			Double score1 = Double.valueOf(i);
			String value1 = "x" + i;
			Double score2 = Double.valueOf(j);
			String value2 = j % 2 == 1 ? "y" + j : "x" + j;
			// ʹ��Spring�ṩ��Ĭ��TypedTuple����DefaultTypedTuple
			TypedTuple typedTuple1 = new DefaultTypedTuple(value1, score1);
			set1.add(typedTuple1);
			TypedTuple typedTuple2 = new DefaultTypedTuple(value2, score2);
			set2.add(typedTuple2);
		}
		// ��Ԫ�ز������򼯺�zset1
		redisTemplate.opsForZSet().add("zset1", set1);
		redisTemplate.opsForZSet().add("zset2", set2);
		// ͳ������
		Long size = null;
		size = redisTemplate.opsForZSet().zCard("zset1");
		// �Ʒ���Ϊscore����ô����ķ���������3<=score<=6��Ԫ��
		size = redisTemplate.opsForZSet().count("zset1", 3, 6);
		Set set = null;
		// ���±�һ��ʼ��ȡ5��Ԫ�أ����ǲ����ط���,ÿһ��Ԫ����String
		set = redisTemplate.opsForZSet().range("zset1", 1, 5);
		printSet(set);
		// ��ȡ��������Ԫ�أ����ҶԼ��ϰ��������򣬲����ط���,ÿһ��Ԫ����TypedTuple
		set = redisTemplate.opsForZSet().rangeWithScores("zset1", 0, -1);
		printTypedTuple(set);
		// ��zset1��zset2�������ϵĽ������뼯��inter_zset
		size = redisTemplate.opsForZSet().intersectAndStore("zset1", "zset2", "inter_zset");
		// ����
		Range range = Range.range();
		range.lt("x8");// С��
		range.gt("x1");// ����
		set = redisTemplate.opsForZSet().rangeByLex("zset1", range);
		printSet(set);
		range.lte("x8");// С�ڵ���
		range.gte("x1");// ���ڵ���
		set = redisTemplate.opsForZSet().rangeByLex("zset1", range);
		printSet(set);
		// ���Ʒ��ظ���
		Limit limit = Limit.limit();
		// ���Ʒ��ظ���
		limit.count(4);
		// ���ƴӵ������ʼ��ȡ
		limit.offset(5);
		// �������ڵ�Ԫ�أ������Ʒ���4��
		set = redisTemplate.opsForZSet().rangeByLex("zset1", range, limit);
		printSet(set);
		// �����У�������1����0����2����1
		Long rank = redisTemplate.opsForZSet().rank("zset1", "x4");
		System.err.println("rank = " + rank);
		// ɾ��Ԫ�أ�����ɾ������
		size = redisTemplate.opsForZSet().remove("zset1", "x5", "x6");
		System.err.println("delete = " + size);
		// ��������ɾ����0��ʼ�������ｫɾ����������2�͵�3��Ԫ��
		size = redisTemplate.opsForZSet().removeRange("zset2", 1, 2);
		// ��ȡ���м��ϵ�Ԫ�غͷ�������-1����ȫ��Ԫ��
		set = redisTemplate.opsForZSet().rangeWithScores("zset2", 0, -1);
		printTypedTuple(set);
		// ɾ��ָ����Ԫ��
		size = redisTemplate.opsForZSet().remove("zset2", "y5", "y3");
		System.err.println(size);
		// �������е�һ��Ԫ�صķ�������11
		Double dbl = redisTemplate.opsForZSet().incrementScore("zset1", "x1", 11);
		redisTemplate.opsForZSet().removeRangeByScore("zset1", 1, 2);
		set = redisTemplate.opsForZSet().reverseRangeWithScores("zset2", 1, 10);
		printTypedTuple(set);
	}

	/**
	 * ��ӡTypedTuple����
	 * 
	 * @param set
	 *            -- Set<TypedTuple>
	 */
	public static void printTypedTuple(Set<TypedTuple> set) {
		if (set != null && set.isEmpty()) {
			return;
		}
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			TypedTuple val = (TypedTuple) iterator.next();
			System.err.print("{value = " + val.getValue() + ", score = " + val.getScore() + "}\n");
		}
	}

	/**
	 * ��ӡ��ͨ����
	 * 
	 * @param set��ͨ����
	 */
	public static void printSet(Set set) {
		if (set != null && set.isEmpty()) {
			return;
		}
		Iterator iterator = set.iterator();
		while (iterator.hasNext()) {
			Object val = iterator.next();
			System.out.print(val + "\t");
		}
		System.out.println();
	}

	public static void testHyperLogLog() {
		ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
		RedisTemplate redisTemplate = applicationContext.getBean(RedisTemplate.class);
		redisTemplate.opsForHyperLogLog().add("HyperLogLog", "a", "b", "c", "d", "a");
		redisTemplate.opsForHyperLogLog().add("HyperLogLog2", "a");
		redisTemplate.opsForHyperLogLog().add("HyperLogLog2", "z");
		Long size = redisTemplate.opsForHyperLogLog().size("HyperLogLog");
		System.err.println(size);
		size = redisTemplate.opsForHyperLogLog().size("HyperLogLog2");
		System.err.println(size);
		redisTemplate.opsForHyperLogLog().union("des_key", "HyperLogLog", "HyperLogLog2");
		size = redisTemplate.opsForHyperLogLog().size("des_key");
		System.err.println(size);
	}
}
