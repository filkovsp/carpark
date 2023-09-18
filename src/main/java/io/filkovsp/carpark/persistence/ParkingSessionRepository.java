package io.filkovsp.carpark.persistence;

import io.filkovsp.carpark.model.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional(readOnly = true)
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, UUID> {

    @Query(value = "SELECT s FROM ParkingSession s WHERE regNumber = :regNumber")
    List<ParkingSession> findByRegNumber(@Param("regNumber") String regNumber);

    @Transactional
    @Modifying
    @Query("UPDATE ParkingSession SET leftAt = :leftAt WHERE id = :id")
    int updateLeftAtById(@Param("id") UUID id, @Param("leftAt") LocalDateTime leftAt);

}
