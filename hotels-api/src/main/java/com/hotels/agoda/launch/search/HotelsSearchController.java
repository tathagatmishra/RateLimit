package com.hotels.agoda.launch.search;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotels.agoda.launch.ratelimit.RateLimitService;

@RestController
public class HotelsSearchController {

	@Autowired
	private HotelsDataService hotelsDataService;

	@Autowired
	private RateLimitService rateLimitService;

	@RequestMapping("key/{apiKey}/hotels/{cityId}")
	public ResponseEntity<List<Hotel>> getHotelsByCityId(@PathVariable String apiKey, @PathVariable String cityId) {

		boolean canAccess = canAccessAPIs(apiKey);

		if (canAccess) {
			List<Hotel> hotelList = hotelsDataService.getHotelsByCityId(cityId);
			return new ResponseEntity<List<Hotel>>(hotelList, HttpStatus.OK);
		} else {
			return new ResponseEntity<List<Hotel>>(Collections.emptyList(), HttpStatus.TOO_MANY_REQUESTS);
		}
	}

	@RequestMapping("key/{apiKey}/hotels/{cityId}/sort/{order}")
	public ResponseEntity<List<Hotel>> getHotelsByCityIdSortedOrder(@PathVariable String apiKey,
			@PathVariable String cityId, @PathVariable String order) {

		boolean canAccess = canAccessAPIs(apiKey);

		if (canAccess) {
			List<Hotel> hotelList = hotelsDataService.getHotelsByCityIdSortedOrder(cityId, order);
			return new ResponseEntity<List<Hotel>>(hotelList, HttpStatus.OK);
		} else {
			return new ResponseEntity<List<Hotel>>(Collections.emptyList(), HttpStatus.TOO_MANY_REQUESTS);
		}

	}

	public boolean canAccessAPIs(String apiKey) {

		Long currentTime = System.currentTimeMillis();
		boolean isBlocked = rateLimitService.isBlocked(apiKey, currentTime);

		if (isBlocked) {
			return false;
		}

		boolean allowAccess = rateLimitService.shouldAllowAccessToApiKey(apiKey, currentTime);

		if (allowAccess) {
			return true;
		} else {
			rateLimitService.blockApiKey(apiKey, currentTime);
			return false;
		}
	}

}
