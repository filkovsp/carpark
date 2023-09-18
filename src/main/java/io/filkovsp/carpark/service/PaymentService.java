package io.filkovsp.carpark.service;

import io.filkovsp.carpark.model.ParkingSession;

import java.time.Duration;

public interface PaymentService {
    double calculateParkingCharge(Duration duration, ParkingSession parkingSession);
}
