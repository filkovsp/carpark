package io.filkovsp.carpark.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Storey {
    private final String level;
    private final List<ParkingSpace> parkingSpaces;

    public Storey(String level) {
        this.level = level;
        this.parkingSpaces = Collections.synchronizedList(new ArrayList<>());
    }
}
