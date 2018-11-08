package com.hotels.agoda.launch.search;

import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.agoda.launch.constants.Constants;
import com.hotels.agoda.launch.cache.HotelsCache;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@Service
public class HotelsDataService {

	@Autowired
	HotelsCache hotelsCache;

	/**
	 * 
	 * @param cityId
	 * @return list of Hotels with city as cityId
	 */
	public List<Hotel> getHotelsByCityId(String cityId) {

		return hotelsCache.getAllValues().stream().filter(s -> s.getCity().equalsIgnoreCase(cityId))
				.collect(Collectors.toList());
	}

	/**
	 * @param cityId
	 * @param order
	 * @return list of Hotels with city as cityId,sorted in order defined by order
	 */
	public List<Hotel> getHotelsByCityIdSortedOrder(String cityId, String order) {

		Comparator<Hotel> comparator = (h1, h2) -> Double.compare(h1.getPrice(), h2.getPrice());

		if (Constants.ASC.equals(order)) {
			return hotelsCache.getAllValues().stream().filter(s -> s.getCity().equalsIgnoreCase(cityId))
					.sorted(comparator).collect(Collectors.toList());
		} else if (Constants.DESC.equals(order)) {
			return hotelsCache.getAllValues().stream().filter(s -> s.getCity().equalsIgnoreCase(cityId))
					.sorted(comparator.reversed()).collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	@PostConstruct
	public void init() {

		String fileName = "src/main/resources/assets/hoteldb.csv";

		CSVReader csvReader = null;

		try {
			csvReader = new CSVReaderBuilder(new FileReader(fileName)).withSkipLines(1).build();
			String[] line;
			while ((line = csvReader.readNext()) != null) {
				Hotel hotel = new Hotel();
				hotel.setCity(line[0]);
				hotel.setId(line[1]);
				hotel.setRoomType(line[2]);
				hotel.setPrice(Double.parseDouble(line[3]));

				hotelsCache.put(hotel.getId(), hotel);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
