package com.hotels.agoda.launch.cache;

import java.util.TreeSet;

/**
* RateLimitCache will contain a cache of apiKey vs TreeSet of timestamps when given apiKey was used in last 10 seconds.
*
*/
public class RateLimitCache extends InMemoryCache<String, TreeSet<Long>>{

}
