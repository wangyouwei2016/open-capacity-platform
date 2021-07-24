package com.open.capacity.client.token;

import org.springframework.security.oauth2.provider.token.store.redis.StandardStringSerializationStrategy;

import com.open.capacity.redis.serializer.RedisObjectSerializer;

public class HessianSerializationStrategy extends StandardStringSerializationStrategy {

	private static final RedisObjectSerializer OBJECT_SERIALIZER = new RedisObjectSerializer();

	@Override
	@SuppressWarnings("unchecked")
	protected <T> T deserializeInternal(byte[] bytes, Class<T> clazz) {
		return (T) OBJECT_SERIALIZER.deserialize(bytes);
	}

	@Override
	protected byte[] serializeInternal(Object object) {
		return OBJECT_SERIALIZER.serialize(object);
	}
}