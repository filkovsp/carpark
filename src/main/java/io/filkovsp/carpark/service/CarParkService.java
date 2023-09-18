package io.filkovsp.carpark.service;

import io.filkovsp.carpark.exception.CarParkFullException;
import io.filkovsp.carpark.exception.ParkingSessionNotFound;
import io.filkovsp.carpark.exception.VehicleAlreadyParkedException;
import io.filkovsp.carpark.model.Storey;
import io.filkovsp.carpark.model.Vehicle;

import java.util.List;

public interface CarParkService {
    List<Storey> getStoreys();
//    BlockingQueue<Vehicle> getParkingQueue();
    boolean park(Vehicle vehicle) throws VehicleAlreadyParkedException, CarParkFullException;
    boolean leave(Vehicle vehicle) throws ParkingSessionNotFound;
    int getParkedVehiclesCount();
    int getSpacesAvailableCount();
}
