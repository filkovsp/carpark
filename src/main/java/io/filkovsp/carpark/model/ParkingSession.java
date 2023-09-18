package io.filkovsp.carpark.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "ParkingSession")
@NoArgsConstructor
public class ParkingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String regNumber;

    @Column(nullable = false)
    private String parkingSpace;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime arrivedAt;

    @Setter
    @Column(columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime leftAt;

    @Setter
    @Column
    private double parkingCharge;

    public ParkingSession(String regNumber, String parkingSpace, LocalDateTime arrivedAt) {
        this.regNumber = regNumber;
        this.parkingSpace = parkingSpace;
        this.arrivedAt = arrivedAt;
    }
}
