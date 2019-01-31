package com.ssm.chapter19.redis.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

public class RedisMessageListener implements MessageListener {
	
	private RedisTemplate redisTemplate;

	@Override
	public void onMessage(Message message, byte[] bytes) {
		// ��ȡ��Ϣ
		byte[] body = message.getBody();
		// ʹ��ֵ���л���ת��
		String msgBody = (String) getRedisTemplate().getValueSerializer().deserialize(body);
		System.err.println(msgBody);
		// ��ȡchannel
		byte[] channel = message.getChannel();
		// ʹ���ַ������л���ת��
		String channelStr = (String) getRedisTemplate().getStringSerializer().deserialize(channel);
		System.err.println(channelStr);
		// ��������ת��
		String bytesStr = new String(bytes);
		System.err.println(bytesStr);
	}

	public RedisTemplate getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate redisTemplate) {
		this.redisTemplate = redisTemplate;
	}
}
