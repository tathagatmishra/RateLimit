package com.hotels.agoda.launch.ratelimit;

import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.hotels.agoda.launch.cache.BlockedApiKeysCache;
import com.hotels.agoda.launch.cache.RateLimitCache;

/**
 * @author tathagat
 *
 */
@Service
public class RateLimitService {

	@Autowired
	private RateLimitCache rateLimitCache;

	@Autowired
	private BlockedApiKeysCache lockedApiKeysCache;

	@Autowired
	private Environment env;

    
	/**
	 * 
	 * This method checks if access count is within set limit. 
	 * Removes old entries while taking count of all access.
	 * [synchronized on apiKey]
	 * 
	 * @param apiKey
	 * @param currentMilis
	 * @return true, if access is allowed.
	 */
	public boolean shouldAllowAccessToApiKey(String apiKey, Long currentMilis) {

		synchronized (apiKey) {

			TreeSet<Long> allAccessedTimeStamps = rateLimitCache.get(apiKey);

			String rateLimitStr = env.getProperty(apiKey);

			// If rate limit is not set in config, set it will global value.
			if (StringUtils.isEmpty(rateLimitStr)) {
				rateLimitStr = env.getProperty("global");
			}

			int limit = Integer.parseInt(rateLimitStr);
			if (allAccessedTimeStamps == null) {
				allAccessedTimeStamps = new TreeSet<Long>();
				rateLimitCache.put(apiKey, allAccessedTimeStamps);
			}

			// Remove all timestamps which are more than 10 seconds old.
			while (allAccessedTimeStamps.size() > 0 && allAccessedTimeStamps.first() + 10 * 1000 < currentMilis)
				allAccessedTimeStamps.pollFirst();

			if (allAccessedTimeStamps.size() < limit) {
				allAccessedTimeStamps.add(currentMilis);
				return true;
			} else {
				return false;
			}

		}
	}

    	
	/**
	 * 
	 * This method checks if apiKey is blocked or not.
	 * [synchronized on apiKey]
	 * 
	 * @param apiKey
	 * @param currentTime
	 * @return  true, if blocked else false
	 */
	public boolean isBlocked(String apiKey, Long currentTime) {

		synchronized (apiKey) {
			Long blockedTime = lockedApiKeysCache.get(apiKey);

			if (blockedTime == null) {
				return false;
			} else {
				//Checks if blocked time is more than 5 minutes old.
				if (currentTime - blockedTime > 5 * 60 * 1000) {
					lockedApiKeysCache.remove(apiKey);
					return false;
				}
			}
			return true;
		}
	}
	

	
	/**
	 * Blocks apiKey, by adding an entry into lockedApiKeysCache.
	 * 
	 * @param apiKey
	 * @param currentMilis
	 */
	public void blockApiKey(String apiKey, Long currentMilis) {
		lockedApiKeysCache.put(apiKey, currentMilis);
	}

}
