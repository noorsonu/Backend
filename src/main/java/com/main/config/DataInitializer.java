package com.main.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.main.services.PrayerTimeService;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PrayerTimeService prayerTimeService;

    @Override
    public void run(String... args) throws Exception {
        prayerTimeService.initializeDefaultPrayerTimes();
    }
}