package com.github.sumuzhou;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;

@Configuration
@EnableCaching(proxyTargetClass = true)
public class CacheConfig {

	@Autowired
	private RedisCacheManager redisCacheManager;
	@Value("${spring.cache.expire.seconds}")
	private Integer expireSeconds;

	@PostConstruct
	public void setDefaultExpiration() {
		redisCacheManager.setDefaultExpiration(expireSeconds);
	}

}
