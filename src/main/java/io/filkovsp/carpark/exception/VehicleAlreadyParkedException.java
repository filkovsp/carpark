package io.filkovsp.carpark.exception;

public class VehicleAlreadyParkedException extends Exception {
    public VehicleAlreadyParkedException(String message) {
        super(message);
    }
}
