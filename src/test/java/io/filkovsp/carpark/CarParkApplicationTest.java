package io.filkovsp.carpark;

import io.filkovsp.carpark.model.ParkingSession;
import io.filkovsp.carpark.model.ParkingSpace;
import io.filkovsp.carpark.model.Vehicle;
import io.filkovsp.carpark.persistence.ParkingSessionRepository;
import io.filkovsp.carpark.service.CarParkService;
import io.filkovsp.carpark.testutils.RadnomVehicle;
import io.filkovsp.carpark.threading.ParkRandomVehicles;
import io.filkovsp.carpark.threading.UnparkRandomVehicles;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Log4j2
@SpringBootTest(classes = {CarParkApplication.class})
@ActiveProfiles({"local", "unit-test"})
class CarParkApplicationTest {

	private final Random random = new Random();

	@Autowired
	private ParkingSessionRepository parkingSessionRepository;

	@Autowired
	private CarParkService carParkService;

	@Autowired
	private ParkRandomVehicles parkRandomVehicles;

	@Autowired
	private UnparkRandomVehicles unparkRandomVehicles;

	@BeforeEach
	public void setup() {
		log.debug("Filling in Car Park with some random vehicles, just slightly below the capacity");

		carParkService.getStoreys().forEach(storey -> {
			// we don't want to shuffle the original list:
			ArrayList<ParkingSpace> parkingSpaces = new ArrayList<>(storey.getParkingSpaces());
			Collections.shuffle(parkingSpaces);

			int capacity = storey.getParkingSpaces().size();
			List<Vehicle> randomVehicles = RadnomVehicle.generateRandomVehicles(capacity - 5);
			int i = 0;
			int maxFilling = capacity - 5;

			while (i < maxFilling) {
				Vehicle randomVehicle = randomVehicles.get(i);
				ParkingSpace parkingSpace = parkingSpaces.get(i);
				parkingSpace.setVehicle(randomVehicle);
				LocalDateTime arrivedAt = LocalDateTime.now().minusMinutes(random.nextInt(360));
				parkingSessionRepository.save(new ParkingSession(randomVehicle.getRegNumber(), parkingSpace.getDesignation(), arrivedAt));
				i++;
			}

			log.debug(maxFilling + " random vehicles were added to storey " + storey.getLevel());
		});
	}

	@Test
	@SneakyThrows
	void smokeTest() {
		Thread parkRandomVehiclesThread1 = new Thread(parkRandomVehicles);
		parkRandomVehiclesThread1.setName("parking-1");
		parkRandomVehiclesThread1.start();

		Thread parkRandomVehiclesThread2 = new Thread(parkRandomVehicles);
		parkRandomVehiclesThread2.setName("parking-2");
		parkRandomVehiclesThread2.start();

		Thread unparkRandomVehiclesThread = new Thread(unparkRandomVehicles);
		unparkRandomVehiclesThread.setName("leaving");
		unparkRandomVehiclesThread.start();

		unparkRandomVehiclesThread.join();
	}

}
