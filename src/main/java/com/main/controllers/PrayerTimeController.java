package com.main.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.main.entities.PrayerTime;
import com.main.services.PrayerTimeService;
import java.util.List;

@RestController
@RequestMapping("/api/prayer-times")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PrayerTimeController {

    @Autowired
    private PrayerTimeService prayerTimeService;

    @GetMapping
    public ResponseEntity<List<PrayerTime>> getAllPrayerTimes() {
        List<PrayerTime> prayerTimes = prayerTimeService.getAllPrayerTimes();
        return ResponseEntity.ok(prayerTimes);
    }

    @PutMapping
    public ResponseEntity<PrayerTime> updatePrayerTime(@RequestBody PrayerTime prayerTime) {
        PrayerTime updated = prayerTimeService.updatePrayerTime(prayerTime.getName(), prayerTime.getTime());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/init")
    public ResponseEntity<String> initializePrayerTimes() {
        prayerTimeService.initializeDefaultPrayerTimes();
        return ResponseEntity.ok("Prayer times initialized");
    }
}