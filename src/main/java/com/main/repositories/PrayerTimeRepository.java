package com.main.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.main.entities.PrayerTime;
import java.util.Optional;

@Repository
public interface PrayerTimeRepository extends JpaRepository<PrayerTime, Long> {
    Optional<PrayerTime> findByName(String name);
}