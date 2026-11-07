package com.cemal.tickrackr.repository;

import com.cemal.tickrackr.model.Sighting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface SightingRepository extends JpaRepository<Sighting, String> {

    List<Sighting> findByLocation(String location);

    List<Sighting> findBySightingDateBetween(
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    List<Sighting> findByLocationAndSightingDateBetween(
            String location,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    @Query("""
        SELECT s.location AS location, COUNT(s) AS count
        FROM Sighting s
        GROUP BY s.location
        ORDER BY COUNT(s) DESC
        """)
    List<LocationCount> countSightingsByLocation();

    @Query("""
        SELECT
            FUNCTION('YEAR', s.sightingDate) AS year,
            FUNCTION('MONTH', s.sightingDate) AS month,
            COUNT(s) AS count
        FROM Sighting s
        GROUP BY
            FUNCTION('YEAR', s.sightingDate),
            FUNCTION('MONTH', s.sightingDate)
        ORDER BY
            FUNCTION('YEAR', s.sightingDate),
            FUNCTION('MONTH', s.sightingDate)
        """)
    List<MonthlyTrend> countSightingsByMonth();

    @Query(value = """
        SELECT
            ISO_YEAR(sighting_date) AS "year",
            ISO_WEEK(sighting_date) AS "week",
            COUNT(*) AS "count"
        FROM sightings
        GROUP BY
            ISO_YEAR(sighting_date),
            ISO_WEEK(sighting_date)
        ORDER BY
            ISO_YEAR(sighting_date),
            ISO_WEEK(sighting_date)
        """, nativeQuery = true)
    List<WeeklyTrend> countSightingsByWeek();
}

