
package com.cemal.tickrackr.service;

import com.cemal.tickrackr.client.ElancoSightingDto;
import com.cemal.tickrackr.model.RejectedRecord;
import com.cemal.tickrackr.model.Sighting;
import com.cemal.tickrackr.repository.RejectedRecordRepository;
import com.cemal.tickrackr.repository.SightingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IngestService {

    private final SightingRepository sightingRepository;
    private final RejectedRecordRepository rejectedRecordRepository;

    public IngestService(
            SightingRepository sightingRepository,
            RejectedRecordRepository rejectedRecordRepository
    ) {
        this.sightingRepository = sightingRepository;
        this.rejectedRecordRepository = rejectedRecordRepository;
    }

    public void ingest(List<ElancoSightingDto> records) {

        if (records == null) {
            throw new IllegalArgumentException("Records cannot be null");
        }

        Set<String> incomingIds = new HashSet<>();

        for (ElancoSightingDto record : records) {
            if (record != null && record.id() != null
                    && !record.id().isBlank()) {
                incomingIds.add(record.id());
            }
        }

        Set<String> existingIds = new HashSet<>();

        for (Sighting sighting : sightingRepository.findAllById(incomingIds)) {
            existingIds.add(sighting.getId());
        }

        Set<String> processedIds = new HashSet<>();

        for (ElancoSightingDto record : records) {

            ValidationResult result =
                    validate(record, existingIds, processedIds);

            if (result.rejectionReason() != null) {

                rejectedRecordRepository.save(
                        new RejectedRecord(
                                record == null ? null : record.id(),
                                record == null ? null : record.date(),
                                record == null ? null : record.location(),
                                record == null ? null : record.species(),
                                record == null ? null : record.latinName(),
                                result.rejectionReason()
                        )
                );

                continue;
            }

            Sighting sighting = new Sighting(
                    record.id(),
                    result.sightingDate(),
                    record.location(),
                    record.species(),
                    record.latinName()
            );

            sightingRepository.save(sighting);
        }
    }

    private ValidationResult validate(
            ElancoSightingDto record,
            Set<String> existingIds,
            Set<String> processedIds
    ) {

        if (record == null) {
            return new ValidationResult("Record is null", null);
        }

        if (record.id() == null || record.id().isBlank()) {
            return new ValidationResult("Missing ID", null);
        }

        if (existingIds.contains(record.id())
                || !processedIds.add(record.id())) {
            return new ValidationResult("Duplicate ID", null);
        }

        if (record.date() == null || record.date().isBlank()) {
            return new ValidationResult("Missing date", null);
        }

        LocalDateTime sightingDate;

        try {
            sightingDate = LocalDateTime.parse(record.date());
        } catch (DateTimeParseException e) {
            return new ValidationResult("Invalid date format", null);
        }

        if (record.location() == null || record.location().isBlank()) {
            return new ValidationResult("Missing location", null);
        }

        if (record.species() == null || record.species().isBlank()) {
            return new ValidationResult("Missing species", null);
        }

        if (record.latinName() == null || record.latinName().isBlank()) {
            return new ValidationResult("Missing Latin name", null);
        }

        return new ValidationResult(null, sightingDate);
    }

    private record ValidationResult(
            String rejectionReason,
            LocalDateTime sightingDate
    ) {}
}
