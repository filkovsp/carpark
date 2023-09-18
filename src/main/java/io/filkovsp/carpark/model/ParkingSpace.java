package io.filkovsp.carpark.model;

import lombok.Data;

@Data
public class ParkingSpace {
    private final String designation;
    private final boolean evEnabled;
    private Vehicle vehicle;

    /*
        TODO:
            parking space can accommodate 2 vehicles if they are
            if those are 2 wheel based, like MOTOBIKE
            in this case it would reasonably fair to apply some discount
            see `two-wheelbase-discount` param in application properties.
            this feature would require refactoring `vehicle` field
            into a list of vehicles.
     */
}
