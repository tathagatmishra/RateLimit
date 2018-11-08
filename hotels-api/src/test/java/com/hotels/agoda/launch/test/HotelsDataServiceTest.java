package com.hotels.agoda.launch.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.hotel.agoda.launch.constants.Constants;
import com.hotels.agoda.launch.cache.HotelsCache;
import com.hotels.agoda.launch.search.Hotel;
import com.hotels.agoda.launch.search.HotelsDataService;

@TestPropertySource(locations = "classpath:/properties/ratelimit.properties")
@RunWith(SpringRunner.class)
@SpringBootTest
public class HotelsDataServiceTest {

	@Autowired
	HotelsDataService hotelsDataService;

	@Test
	public void testGetHotelsByCityId() {

		List<Hotel> hotelsInCity = hotelsDataService.getHotelsByCityId(TestConstants.AMSTERDAM);

		assertEquals(6, hotelsInCity.size());

		hotelsInCity = hotelsDataService.getHotelsByCityId(TestConstants.BANGKOK);

		assertEquals(7, hotelsInCity.size());

	}

	@Test
	public void testGetHotelsByCityIdInSortedOrder() {

		List<Hotel> hotelsInCityAscending = hotelsDataService.getHotelsByCityIdSortedOrder(TestConstants.AMSTERDAM, Constants.ASC);

		assertEquals(hotelsInCityAscending.get(0).getPrice(), 1000.0, 0);

		assertEquals(hotelsInCityAscending.get(hotelsInCityAscending.size() - 1).getPrice(), 30000.0, 0);

		List<Hotel> hotelsInCityDecsending = hotelsDataService.getHotelsByCityIdSortedOrder(TestConstants.AMSTERDAM, Constants.DESC);

		assertEquals(hotelsInCityDecsending.get(0).getPrice(), 30000.0, 0);

		assertEquals(hotelsInCityDecsending.get(hotelsInCityDecsending.size() - 1).getPrice(), 1000.0, 0);

	}

}
