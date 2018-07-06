package com.github.sumuzhou;

import java.nio.charset.Charset;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

@Configuration
public class ApplicationConfig {

	@Value("${spring.redis.host}")
	private String redisHost;
	@Value("${spring.redis.password}")
	private String redisPasswd;
	@Value("${spring.redis.port}")
	private Integer redisPort;
	@Value("${spring.redis.database}")
	private Integer redisDatabase;

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress("redis://" + redisHost + ":" + redisPort)
			.setPassword(redisPasswd)
			.setDatabase(redisDatabase);
		return Redisson.create(config);
	}

    @Bean
    public RestTemplate restTemplate(ClientHttpRequestFactory factory){
        RestTemplate restTemplate = new RestTemplate(factory);
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		return restTemplate;
    }

    @Bean
    public ClientHttpRequestFactory simpleClientHttpRequestFactory(){
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setReadTimeout(5000);//ms
        factory.setConnectTimeout(15000);//ms
        return factory;
    }

    @Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

    @Bean
    public HashFunction hashFunction() {
    	return Hashing.sha256();
    }

}
