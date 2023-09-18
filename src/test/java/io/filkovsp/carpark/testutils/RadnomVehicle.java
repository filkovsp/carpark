package io.filkovsp.carpark.testutils;

import io.filkovsp.carpark.model.EngineType;
import io.filkovsp.carpark.model.ParkingSpace;
import io.filkovsp.carpark.model.Storey;
import io.filkovsp.carpark.model.Vehicle;
import io.filkovsp.carpark.model.WheelBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class RadnomVehicle {

    private static final Random random = new Random();

    public static List<Vehicle> generateRandomVehicles(int count) {

        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String regNumber = UUID.randomUUID().toString();

            int wheelBaseOrdinal = random.nextInt(WheelBase.values().length);
            WheelBase wheelbase = WheelBase.values()[wheelBaseOrdinal];

            int enginTypeOrdinal = random.nextInt(EngineType.values().length);
            EngineType engineType = EngineType.values()[enginTypeOrdinal];

            vehicles.add(new Vehicle(regNumber.toUpperCase().substring(0, 8), wheelbase, engineType));
        }
        return vehicles;
    }

    public static Optional<Vehicle> getRandomVehicleFromParking(List<Storey> storeys) {
        Optional<ParkingSpace> optionalParkingSpace = storeys.stream()
                .flatMap(storey -> storey.getParkingSpaces().stream()
                        .filter(parkingSpace -> Objects.nonNull(parkingSpace.getVehicle())))
                .findAny();

        return optionalParkingSpace.map(ParkingSpace::getVehicle);

    }
}
