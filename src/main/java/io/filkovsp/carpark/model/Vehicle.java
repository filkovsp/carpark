package io.filkovsp.carpark.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public class Vehicle {
    private final String regNumber;
    private final WheelBase wheelBase;
    private final EngineType engineType;
}
