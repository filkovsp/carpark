package io.filkovsp.carpark.service;

import io.filkovsp.carpark.model.ParkingSession;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentServiceTest {

    private static final double PARKING_COST = 2.0;
    private static final PaymentService paymentService = new BasicPaymentService();

    @BeforeAll
    public static void setup() {
        ReflectionTestUtils.setField(paymentService, "parkingCost", PARKING_COST);
    }

    @Test
    @DisplayName("Should charge basic cost if parking duration is within an hour")
    public void testParkingCostWithinAnHour() {
        LocalDateTime end =  LocalDateTime.now();
        LocalDateTime start = end.minusMinutes(50);
        Duration duration = Duration.between(start, end);
        ParkingSession parkingSession = new ParkingSession("AB12CDE", "G-001", start);

        double parkingCharge = paymentService.calculateParkingCharge(duration, parkingSession);
        assertEquals(2.0, parkingCharge);
    }

    @Test
    @DisplayName("Should charge basic cost if parking duration is within an hour")
    public void testParkingCostOverAnHour() {
        LocalDateTime end =  LocalDateTime.now();
        LocalDateTime start = end.minusMinutes(70);
        Duration duration = Duration.between(start, end);
        ParkingSession parkingSession = new ParkingSession("AB12CDE", "G-001", start);

        double parkingCharge = paymentService.calculateParkingCharge(duration, parkingSession);
        assertEquals(4.0, parkingCharge);
    }
}
