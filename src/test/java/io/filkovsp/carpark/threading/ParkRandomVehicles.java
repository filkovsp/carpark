package io.filkovsp.carpark.threading;

import io.filkovsp.carpark.model.Vehicle;
import io.filkovsp.carpark.service.CarParkService;
import io.filkovsp.carpark.testutils.RadnomVehicle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Random;

@Component
@ActiveProfiles("unit-test")
public class ParkRandomVehicles implements Runnable {

    private final Random random = new Random();

    @Autowired
    private CarParkService carParkService;

    @Override
    public void run() {
        int spacesAvailable = carParkService.getSpacesAvailableCount();
        List<Vehicle> randomVehicles = RadnomVehicle.generateRandomVehicles(spacesAvailable + 30);

        randomVehicles.forEach(vehicle -> {
            try {
                carParkService.park(vehicle);
            } catch (Exception ex) {
                //
            }

            // Sleep some time, to imitate a pause between the arriving vehicles
            try {
                Thread.sleep(random.nextInt(500, 2500));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
