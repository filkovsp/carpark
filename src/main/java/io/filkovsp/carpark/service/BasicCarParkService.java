package io.filkovsp.carpark.service;

import io.filkovsp.carpark.exception.CarParkFullException;
import io.filkovsp.carpark.exception.ParkingSessionNotFound;
import io.filkovsp.carpark.exception.VehicleAlreadyParkedException;
import io.filkovsp.carpark.model.ParkingSession;
import io.filkovsp.carpark.model.ParkingSpace;
import io.filkovsp.carpark.model.Storey;
import io.filkovsp.carpark.model.Vehicle;
import io.filkovsp.carpark.persistence.ParkingSessionRepository;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j2
@Service
public class BasicCarParkService implements CarParkService {

    @Getter
    @Autowired
    @Qualifier("Storeys")
    private List<Storey> storeys;

    @Autowired
    private ParkingSessionRepository parkingSessionRepository;

    @Autowired
    private PaymentService paymentService;

    @Override
    @Synchronized
    public boolean park(Vehicle vehicle) throws VehicleAlreadyParkedException, CarParkFullException {

        if (getSpacesAvailableCount() == 0) {
            throw new CarParkFullException("Car park is full!");
        }

        List<ParkingSession> sessions = parkingSessionRepository.findByRegNumber(vehicle.getRegNumber());
        if (sessions.size() > 0) {
            throw new VehicleAlreadyParkedException("Vehicle " + vehicle.getRegNumber() + " has been already parked!");
        }

        Optional<ParkingSpace> probablyEmptyParkingSpace = storeys.stream()
                .flatMap(storey -> storey.getParkingSpaces().stream()
                        .filter(parkingSpace -> Objects.isNull(parkingSpace.getVehicle())))
                .findAny();

        if (probablyEmptyParkingSpace.isPresent()) {

            ParkingSpace emptyParkingSpace =  probablyEmptyParkingSpace.get();
            parkVehicleIntoSpace(emptyParkingSpace, vehicle);

            return true;
        }

        return false;
    }

    @Override
    @Synchronized
    public boolean leave(Vehicle vehicle) throws ParkingSessionNotFound {
        List<ParkingSession> parkingSessions = parkingSessionRepository.findByRegNumber(vehicle.getRegNumber());
        if (parkingSessions.size() == 0) {
            throw new ParkingSessionNotFound("No session found for vehicle " + vehicle.getRegNumber());
        }

        // TODO: here is a room for a bug in case if there are multiple sessions
        //  were squeezed into the Parking Session Repository:
        ParkingSession parkingSession = parkingSessions.get(0);

        return unparkVehicle(parkingSession, vehicle);
    }

    @Override
    public int getParkedVehiclesCount() {
        return storeys.stream()
                .mapToInt(storey -> {
                    long numberOfOccupiedSpaces = storey.getParkingSpaces().stream()
                            .filter(parkingSpace -> Objects.nonNull(parkingSpace.getVehicle())).count();
                    return (int) numberOfOccupiedSpaces;
                }).sum();
    }

    @Override
    public int getSpacesAvailableCount() {
        return storeys.stream()
                .mapToInt(storey -> {
                    long numberOfEmptySpaces = storey.getParkingSpaces().stream()
                            .filter(parkingSpace -> Objects.isNull(parkingSpace.getVehicle())).count();
                    return (int) numberOfEmptySpaces;
                }).sum();
    }

    private void parkVehicleIntoSpace(ParkingSpace parkingSpace, Vehicle vehicle) {
        parkingSpace.setVehicle(vehicle);
        log.debug("Parked vehicle to an empty parking space: " + parkingSpace.getDesignation());

        LocalDateTime arrivedAt = LocalDateTime.now();
        ParkingSession parkingSession = new ParkingSession(vehicle.getRegNumber(),
                parkingSpace.getDesignation(),
                LocalDateTime.now());

        parkingSessionRepository.save(parkingSession);

        String updateMessage = String.format("Updated parking session for vehicle %s (arrived at %s)",
                vehicle.getRegNumber(),
                arrivedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        log.debug(updateMessage);
    }

    private boolean unparkVehicle(ParkingSession parkingSession, Vehicle vehicle) {

        Optional<ParkingSpace> optionalParkingSpace = storeys.stream()
                .flatMap(storey -> storey.getParkingSpaces().stream()
                        .filter(parkingSpace -> vehicle.equals(parkingSpace.getVehicle())))
                .findAny();

        if (optionalParkingSpace.isPresent()) {

            LocalDateTime leftAt = LocalDateTime.now();
            Duration duration = Duration.between(parkingSession.getArrivedAt(), LocalDateTime.now());
            double parkingCharge = paymentService.calculateParkingCharge(duration, parkingSession);

            parkingSession.setLeftAt(leftAt);
            parkingSession.setParkingCharge(parkingCharge);

            parkingSessionRepository.save(parkingSession);

            String updateMessage = String.format("Updated parking session for vehicle %s (left at %s)",
                    vehicle.getRegNumber(),
                    leftAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));

            log.debug(updateMessage);

            ParkingSpace parkingSpace =  optionalParkingSpace.get();
            parkingSpace.setVehicle(null);

            return true;
        }

        return false;
    }
}
