package com.hotels.agoda.launch;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hotels.agoda.launch.cache.BlockedApiKeysCache;
import com.hotels.agoda.launch.cache.HotelsCache;
import com.hotels.agoda.launch.cache.RateLimitCache;

@Configuration
public class HotelsConfiguration {

	@Bean
	HotelsCache hotelsCache() {
		return new HotelsCache();
	}
	
	@Bean
	RateLimitCache rateLimitCache() {
		return new RateLimitCache();
	}
	
	@Bean
	BlockedApiKeysCache blockedApiKeysCache() {
		return new BlockedApiKeysCache();
	}
}
