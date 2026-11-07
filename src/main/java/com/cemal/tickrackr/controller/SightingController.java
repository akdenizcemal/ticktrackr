package com.cemal.tickrackr.controller;

import com.cemal.tickrackr.repository.LocationCount;
import com.cemal.tickrackr.repository.MonthlyTrend;
import com.cemal.tickrackr.repository.WeeklyTrend;
import com.cemal.tickrackr.service.SightingService;
import com.cemal.tickrackr.model.Sighting;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class SightingController {

    private final SightingService sightingService;

    public SightingController(SightingService sightingService) {
        this.sightingService = sightingService;
    }
    @GetMapping("/api/sightings")
    public List<Sighting> searchSightings(
            @RequestParam(required = false) String location,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endDate
    ) {
        return sightingService.searchSightings(location, startDate, endDate);
    }
    @GetMapping("/api/reports/sightings-by-location")
    public List<LocationCount> getSightingsByLocation() {
        return sightingService.getSightingsByLocation();
    }
    @GetMapping("/api/reports/monthly-trends")
    public List<MonthlyTrend> getMonthlyTrends() {
        return sightingService.getMonthlyTrends();
    }
    @GetMapping("/api/reports/weekly-trends")
    public List<WeeklyTrend> getWeeklyTrends() {
        return sightingService.getWeeklyTrends();
    }

}