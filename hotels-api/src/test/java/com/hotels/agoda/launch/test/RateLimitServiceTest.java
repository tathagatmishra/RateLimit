package com.hotels.agoda.launch.test;

import static org.junit.Assert.assertEquals;

import java.util.TreeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.hotels.agoda.launch.cache.BlockedApiKeysCache;
import com.hotels.agoda.launch.cache.RateLimitCache;
import com.hotels.agoda.launch.ratelimit.RateLimitService;

@TestPropertySource(locations = "classpath:/properties/ratelimit.properties")
@RunWith(SpringRunner.class)
@SpringBootTest
public class RateLimitServiceTest {

	@Autowired
	private RateLimitService rateLimitService;

	@MockBean
	private RateLimitCache rateLimitCache;

	@MockBean
	private BlockedApiKeysCache lockedApiKeysCache;

	@Test
	public void testShouldAllowAccessToApiKeyWhenAccessedForTheFirstTime() {

		Mockito.when(rateLimitCache.get(TestConstants.KEY_ABC)).thenReturn(null);

		boolean shouldAllow = rateLimitService.shouldAllowAccessToApiKey(TestConstants.KEY_ABC, 1541569187000L);// 11:09:47, 7th Nov 2018

		assertEquals(true, shouldAllow);
	}

	@Test
	public void testShouldAllowAccessToApiKey() {

		TreeSet<Long> allAccessedTimeStamps = new TreeSet<Long>();
		allAccessedTimeStamps.add(1541569181000L);// 11:09:41
		allAccessedTimeStamps.add(1541569183000L);// 11:09:43
		allAccessedTimeStamps.add(1541569185000L);// 11:09:45

		Mockito.when(rateLimitCache.get(TestConstants.KEY_ABC)).thenReturn(allAccessedTimeStamps);

		boolean shouldAllow = rateLimitService.shouldAllowAccessToApiKey(TestConstants.KEY_ABC, 1541569187000L);// 11:09:47

		assertEquals(false, shouldAllow);
	}

	@Test
	public void testShouldAllowAccessToApiKeyWhenAccessedAfter10Seconds() {

		TreeSet<Long> allAccessedTimeStamps = new TreeSet<Long>();
		allAccessedTimeStamps.add(1541569181000L);// 11:09:41
		allAccessedTimeStamps.add(1541569183000L);// 11:09:43
		allAccessedTimeStamps.add(1541569185000L);// 11:09:45

		Mockito.when(rateLimitCache.get(TestConstants.KEY_ABC)).thenReturn(allAccessedTimeStamps);

		boolean shouldAllow = rateLimitService.shouldAllowAccessToApiKey(TestConstants.KEY_ABC, 1541569195000L);// 11:09:55

		assertEquals(true, shouldAllow);
	}
	
	
	@Test
	public void testShouldAllowAccessToApiKeyWhenAccessedAfter10SecondsOfInitialAccess() {

		TreeSet<Long> allAccessedTimeStamps = new TreeSet<Long>();
		allAccessedTimeStamps.add(1541569181000L);// 11:09:41
		allAccessedTimeStamps.add(1541569183000L);// 11:09:43
		allAccessedTimeStamps.add(1541569185000L);// 11:09:45

		Mockito.when(rateLimitCache.get(TestConstants.KEY_ABC)).thenReturn(allAccessedTimeStamps);

		boolean shouldAllow = rateLimitService.shouldAllowAccessToApiKey(TestConstants.KEY_ABC, 1541569192000L);// 11:09:52

		assertEquals(true, shouldAllow);
	}

	@Test
	public void testIsBlockedWhenApiIsNotBlockedAtAll() {

		Mockito.when(lockedApiKeysCache.get(TestConstants.KEY_ABC)).thenReturn(null);

		boolean isBlocked = rateLimitService.isBlocked(TestConstants.KEY_ABC, 1541569480000L);// 11:14:40

		assertEquals(false, isBlocked);
	}

	@Test
	public void testIsBlockedWhenAccessedWithInBlockingTime() {

		Mockito.when(lockedApiKeysCache.get(TestConstants.KEY_ABC)).thenReturn(1541569181000L);// 11:09:41

		boolean isBlocked = rateLimitService.isBlocked(TestConstants.KEY_ABC, 1541569480000L);// 11:14:40

		assertEquals(true, isBlocked);
	}

	@Test
	public void testIsBlockedWhenAccessedAfter5Minutes() {

		Mockito.when(lockedApiKeysCache.get(TestConstants.KEY_ABC)).thenReturn(1541569181000L);// 11:09:41

		boolean isBlocked = rateLimitService.isBlocked(TestConstants.KEY_ABC, 1541569482000L);// 11:14:42

		assertEquals(false, isBlocked);
	}

}
