package com.hotels.agoda.launch.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.hotels.agoda.launch.ratelimit.RateLimitService;
import com.hotels.agoda.launch.search.Hotel;
import com.hotels.agoda.launch.search.HotelsDataService;
import com.hotels.agoda.launch.search.HotelsSearchController;

@TestPropertySource(locations = "classpath:/properties/ratelimit.properties")
@RunWith(SpringRunner.class)
@SpringBootTest
public class HotelSearchControllerTest {

	@MockBean
	private HotelsDataService hotelsDataService;

	@MockBean
	private RateLimitService rateLimitService;

	@Autowired
	private HotelsSearchController hotelSearchController;

	@Test
	public void testGetHotelsByCityIdWhenApiIsBlocked() {

		Mockito.when(hotelsDataService.getHotelsByCityId(TestConstants.BANGKOK)).thenReturn(getHotels());

		Mockito.when(rateLimitService.isBlocked(Mockito.eq(TestConstants.KEY_ABC), Mockito.anyLong())).thenReturn(true);

		ResponseEntity<List<Hotel>> hotelList  = hotelSearchController.getHotelsByCityId(TestConstants.KEY_ABC, TestConstants.BANGKOK);

		assertEquals(0, hotelList.getBody().size());
		assertEquals(HttpStatus.TOO_MANY_REQUESTS, hotelList.getStatusCode());
	}

	@Test
	public void testGetHotelsByCityIdWhenAccessCountCountExceedsLimit() {

		Mockito.when(hotelsDataService.getHotelsByCityId(TestConstants.BANGKOK)).thenReturn(getHotels());

		Mockito.when(rateLimitService.isBlocked(Mockito.eq(TestConstants.KEY_ABC), Mockito.anyLong())).thenReturn(false);

		Mockito.when(rateLimitService.shouldAllowAccessToApiKey(Mockito.eq(TestConstants.KEY_ABC), Mockito.anyLong()))
				.thenReturn(false);

		ResponseEntity<List<Hotel>> hotelList = hotelSearchController.getHotelsByCityId(TestConstants.KEY_ABC, TestConstants.BANGKOK);

		assertEquals(0, hotelList.getBody().size());
		assertEquals(HttpStatus.TOO_MANY_REQUESTS, hotelList.getStatusCode());
	}

	@Test
	public void testGetHotelsByCityIdWhenAccessCountIsInLimit() {

		Mockito.when(hotelsDataService.getHotelsByCityId(TestConstants.BANGKOK)).thenReturn(getHotels());

		Mockito.when(rateLimitService.isBlocked(Mockito.eq(TestConstants.KEY_ABC), Mockito.anyLong())).thenReturn(false);

		Mockito.when(rateLimitService.shouldAllowAccessToApiKey(Mockito.eq(TestConstants.KEY_ABC), Mockito.anyLong())).thenReturn(true);

		ResponseEntity<List<Hotel>> hotelList  = hotelSearchController.getHotelsByCityId(TestConstants.KEY_ABC, TestConstants.BANGKOK);

		assertEquals(2, hotelList.getBody().size());
		assertEquals(HttpStatus.OK, hotelList.getStatusCode());
	}

	public List<Hotel> getHotels() {

		Hotel hotel1 = new Hotel();
		hotel1.setCity(TestConstants.BANGKOK);
		hotel1.setId("1");
		hotel1.setRoomType("Deluxe");
		hotel1.setPrice(1000.0);

		Hotel hotel2 = new Hotel();
		hotel2.setCity(TestConstants.BANGKOK);
		hotel2.setId("2");
		hotel2.setRoomType("Superior");
		hotel2.setPrice(2000.0);

		List<Hotel> hotelList = new ArrayList<Hotel>();
		hotelList.add(hotel1);
		hotelList.add(hotel2);

		return hotelList;

	}

}
