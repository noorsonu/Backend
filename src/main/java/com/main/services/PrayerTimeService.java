package com.main.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.main.entities.PrayerTime;
import com.main.repositories.PrayerTimeRepository;
import java.util.List;
import java.util.Optional;

@Service
public class PrayerTimeService {

    @Autowired
    private PrayerTimeRepository prayerTimeRepository;

    public List<PrayerTime> getAllPrayerTimes() {
        List<PrayerTime> prayerTimes = prayerTimeRepository.findAll();
        if (prayerTimes.isEmpty()) {
            initializeDefaultPrayerTimes();
            prayerTimes = prayerTimeRepository.findAll();
        }
        return prayerTimes;
    }

    public PrayerTime updatePrayerTime(String name, String time) {
        Optional<PrayerTime> existingPrayerTime = prayerTimeRepository.findByName(name);
        
        if (existingPrayerTime.isPresent()) {
            PrayerTime prayerTime = existingPrayerTime.get();
            prayerTime.setTime(time);
            return prayerTimeRepository.save(prayerTime);
        } else {
            throw new RuntimeException("Prayer time not found: " + name);
        }
    }

    public void initializeDefaultPrayerTimes() {
        if (prayerTimeRepository.count() == 0) {
            prayerTimeRepository.save(new PrayerTime("Fajr", "05:30", "🌅"));
            prayerTimeRepository.save(new PrayerTime("Dhuhr", "12:15", "☀️"));
            prayerTimeRepository.save(new PrayerTime("Asr", "15:45", "🌤️"));
            prayerTimeRepository.save(new PrayerTime("Maghrib", "18:20", "🌅"));
            prayerTimeRepository.save(new PrayerTime("Isha", "19:45", "🌙"));
        }
    }
}