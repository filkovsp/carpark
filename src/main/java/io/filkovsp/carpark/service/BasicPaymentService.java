package io.filkovsp.carpark.service;

import io.filkovsp.carpark.model.ParkingSession;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Log4j2
@Service
public class BasicPaymentService implements PaymentService {

    @Value("${car-park.parking-cost}")
    private double parkingCost;

    @Override
    public double calculateParkingCharge(Duration duration, ParkingSession parkingSession) {
        /*
            TODO
                 parking session here needed for additional steps that we might want to perform here
                 before closing session. for example:
                 - applying addition discounts, loyalty campaigns etc.
         */
        double wholeHours = Math.ceil(duration.toSeconds() / 3600.0);
        double parkingCharge = wholeHours * parkingCost;

        String message = String.format("%.2f to be paid for parking %s",
                parkingCharge, parkingSession.getRegNumber());

        log.debug(message);
        return parkingCharge;

        /*
            TODO: here could be implementation for wiring with some concrete payment system.
         */
    }
}
