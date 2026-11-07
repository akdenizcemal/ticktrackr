package com.cemal.tickrackr.service;

import com.cemal.tickrackr.repository.LocationCount;
import com.cemal.tickrackr.repository.MonthlyTrend;
import com.cemal.tickrackr.repository.SightingRepository;
import com.cemal.tickrackr.repository.WeeklyTrend;
import org.springframework.stereotype.Service;
import com.cemal.tickrackr.model.Sighting;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SightingService {

    private final SightingRepository sightingRepository;

    public SightingService(SightingRepository sightingRepository) {
        this.sightingRepository = sightingRepository;
    }
    public List<Sighting> searchSightings(
            String location,
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
        if ((startDate == null && endDate != null) ||
                (startDate != null && endDate == null)) {
            throw new IllegalArgumentException(
                    "Both start date and end date must be provided"
            );
        }

        if (startDate != null && endDate != null &&
                startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date"
            );
        }
        if (location != null && startDate != null && endDate != null) {
            return sightingRepository.findByLocationAndSightingDateBetween(
                    location, startDate, endDate
            );
        }

        if (location != null) {
            return sightingRepository.findByLocation(location);
        }

        if (startDate != null && endDate != null) {
            return sightingRepository.findBySightingDateBetween(
                    startDate, endDate
            );
        }

        return sightingRepository.findAll();
    }
    public List<LocationCount> getSightingsByLocation() {
        return sightingRepository.countSightingsByLocation();
    }
    public List<MonthlyTrend> getMonthlyTrends() {
        return sightingRepository.countSightingsByMonth();
    }
    public List<WeeklyTrend> getWeeklyTrends() {
        return sightingRepository.countSightingsByWeek();
    }

}