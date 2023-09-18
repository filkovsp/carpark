package io.filkovsp.carpark.service;

import io.filkovsp.carpark.exception.CarParkFullException;
import io.filkovsp.carpark.exception.ParkingSessionNotFound;
import io.filkovsp.carpark.exception.VehicleAlreadyParkedException;
import io.filkovsp.carpark.model.EngineType;
import io.filkovsp.carpark.model.ParkingSession;
import io.filkovsp.carpark.model.ParkingSpace;
import io.filkovsp.carpark.model.Storey;
import io.filkovsp.carpark.model.Vehicle;
import io.filkovsp.carpark.model.WheelBase;
import io.filkovsp.carpark.persistence.ParkingSessionRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CarParkServiceTest {

    private static final double PARKING_COST = 2.0;

    @Mock
    private ParkingSessionRepository parkingSessionRepository;

    @Mock
    private BasicPaymentService paymentService;

    @InjectMocks
    private BasicCarParkService carParkService;

    @BeforeEach
    public void setup() {
        List<Storey> storeys = new ArrayList<>();
        Storey emptyStorey = new Storey("G");
        storeys.add(emptyStorey);

        List<ParkingSpace> parkingSpaces = emptyStorey.getParkingSpaces();
        parkingSpaces.add(new ParkingSpace("G-001", true));
        parkingSpaces.add(new ParkingSpace("G-002", true));
        parkingSpaces.add(new ParkingSpace("G-003", false));
        parkingSpaces.add(new ParkingSpace("G-004", false));

        ReflectionTestUtils.setField(carParkService, "storeys", storeys);
    }

    @Nested
    class ParkingTests {
        @Test
        @DisplayName("Should park a vehicle int an empty car park")
        @SneakyThrows
        public void canParkVehicleIntoAnEmptyStorey() {
            Vehicle vehicle = new Vehicle("AB12CDE", WheelBase.FOUR_WHEELER, EngineType.COMBUSTION);
            when(parkingSessionRepository.findByRegNumber(anyString())).thenReturn(Collections.emptyList());

            assertTrue(carParkService.park(vehicle));
            assertEquals(1, carParkService.getParkedVehiclesCount());
            assertEquals(3, carParkService.getSpacesAvailableCount());
        }

        @Test
        @DisplayName("Should throw VehicleAlreadyParkedException exception")
        public void canParkIfAlreadyParked() {
            Vehicle vehicle = new Vehicle("AB12CDE", WheelBase.FOUR_WHEELER, EngineType.COMBUSTION);
            ParkingSession parkingSession = new ParkingSession("AB12CDE", "G-001", LocalDateTime.now());
            when(parkingSessionRepository.findByRegNumber(anyString())).thenReturn(List.of(parkingSession));

            assertThrows(VehicleAlreadyParkedException.class, () -> carParkService.park(vehicle));
        }

        @Test
        @DisplayName("Should fail if parking is full")
        @SneakyThrows
        public void testFullCarPark() {
            carParkService.park(new Vehicle("AB11CDE", WheelBase.FOUR_WHEELER, EngineType.COMBUSTION));
            carParkService.park(new Vehicle("AB22DE", WheelBase.FOUR_WHEELER, EngineType.COMBUSTION));
            carParkService.park(new Vehicle("AB33CDE", WheelBase.FOUR_WHEELER, EngineType.COMBUSTION));
            carParkService.park(new Vehicle("AB44CDE", WheelBase.FOUR_WHEELER, EngineType.COMBUSTION));

            Vehicle vehicle = new Vehicle("AB55CDE", WheelBase.FOUR_WHEELER, EngineType.COMBUSTION);
            assertThrows(CarParkFullException.class, () -> carParkService.park(vehicle));
        }
    }

    @Nested
    class LeavingTests {
        @Test
        @DisplayName("Should be able to leave")
        @SneakyThrows
        public void testCanLeave() {
            Vehicle vehicle = new Vehicle("AB12CDE", WheelBase.FOUR_WHEELER, EngineType.COMBUSTION);
            ParkingSession parkingSession = new ParkingSession("AB12CDE", "G-001", LocalDateTime.now());

            when(paymentService.calculateParkingCharge(any(), any())).thenReturn(PARKING_COST);
            assertTrue(carParkService.park(vehicle));

            when(parkingSessionRepository.findByRegNumber(anyString())).thenReturn(List.of(parkingSession));
            assertTrue(carParkService.leave(vehicle));
            assertEquals(0, carParkService.getParkedVehiclesCount());
        }

        @Test
        @DisplayName("Should fail to leave if never parked")
        public void testCantLeave() {
            Vehicle vehicle = new Vehicle("AB12CDE", WheelBase.FOUR_WHEELER, EngineType.COMBUSTION);
            assertThrows(ParkingSessionNotFound.class, () -> carParkService.leave(vehicle));
        }
    }
}
