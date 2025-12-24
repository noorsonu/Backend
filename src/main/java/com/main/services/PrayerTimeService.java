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
        
        // Sort prayer times in correct order
        String[] order = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
        prayerTimes.sort((a, b) -> {
            int indexA = java.util.Arrays.asList(order).indexOf(a.getName());
            int indexB = java.util.Arrays.asList(order).indexOf(b.getName());
            return Integer.compare(indexA, indexB);
        });
        
        return prayerTimes;
    }

    public PrayerTime updatePrayerTime(String name, String time) {
        Optional<PrayerTime> existingPrayerTime = prayerTimeRepository.findByName(name);
        
        if (existingPrayerTime.isPresent()) {
            PrayerTime prayerTime = existingPrayerTime.get();
            prayerTime.setTime(time);
            return prayerTimeRepository.save(prayerTime);
        } else {
            // Create new prayer time if it doesn't exist
            String icon = getIconForPrayer(name);
            PrayerTime newPrayerTime = new PrayerTime(name, time, icon);
            return prayerTimeRepository.save(newPrayerTime);
        }
    }
    
    private String getIconForPrayer(String name) {
        switch (name.toLowerCase()) {
            case "fajr": return "🌅";
            case "dhuhr": return "☀️";
            case "asr": return "🌤️";
            case "maghrib": return "🌅";
            case "isha": return "🌙";
            default: return "🕌";
        }
    }

    public void initializeDefaultPrayerTimes() {
        String[] prayerNames = {"Fajr", "Dhuhr", "Asr", "Maghrib", "Isha"};
        String[] defaultTimes = {"05:30", "12:15", "15:45", "18:20", "19:45"};
        
        for (int i = 0; i < prayerNames.length; i++) {
            if (!prayerTimeRepository.findByName(prayerNames[i]).isPresent()) {
                String icon = getIconForPrayer(prayerNames[i]);
                prayerTimeRepository.save(new PrayerTime(prayerNames[i], defaultTimes[i], icon));
            }
        }
    }
}