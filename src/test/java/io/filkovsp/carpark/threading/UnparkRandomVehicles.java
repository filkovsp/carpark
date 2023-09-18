package io.filkovsp.carpark.threading;

import io.filkovsp.carpark.model.ParkingSession;
import io.filkovsp.carpark.persistence.ParkingSessionRepository;
import io.filkovsp.carpark.service.CarParkService;
import io.filkovsp.carpark.testutils.RadnomVehicle;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Random;

@Log4j2
@Component
@ActiveProfiles("unit-test")
public class UnparkRandomVehicles implements Runnable {

    private final Random random = new Random();

    @Autowired
    private CarParkService carParkService;

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;


    @Override
    public void run() {

        // Delay for couple secs to let the car park to reach its capacity from the parking thread:
        try {
            Thread.sleep(10_000 + random.nextInt(5_000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while(carParkService.getParkedVehiclesCount() > 0) {

            // Simulate that it takes for 2....7 secs for a vehicle to actually leave the car park
            try {
                Thread.sleep(2_000 + random.nextInt(5_000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            RadnomVehicle.getRandomVehicleFromParking(carParkService.getStoreys()).ifPresent(vehicle -> {
                try {
                    carParkService.leave(vehicle);
                } catch (Exception ex ) {
                    throw new RuntimeException(ex);
                }
            });
        }

        List<ParkingSession> parkingSessions = parkingSessionRepository.findAll();
        double totalAmount = parkingSessions.stream().mapToDouble(ParkingSession::getParkingCharge).sum();
        log.debug(String.format("Parking is empty, we've made £%.2f today! Bye now!", totalAmount));
    }
}
