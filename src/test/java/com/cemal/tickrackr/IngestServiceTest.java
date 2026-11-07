
package com.cemal.tickrackr;

import com.cemal.tickrackr.client.ElancoSightingDto;
import com.cemal.tickrackr.model.RejectedRecord;
import com.cemal.tickrackr.model.Sighting;
import com.cemal.tickrackr.repository.RejectedRecordRepository;
import com.cemal.tickrackr.repository.SightingRepository;
import com.cemal.tickrackr.service.IngestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngestServiceTest {

	@Mock
	private SightingRepository sightingRepository;

	@Mock
	private RejectedRecordRepository rejectedRecordRepository;

	@InjectMocks
	private IngestService ingestService;

	@Test
	void validRecordIsSavedAsSighting() {

		ElancoSightingDto record = new ElancoSightingDto(
				"test-1",
				"2024-01-01T12:00:00",
				"London",
				"Tick",
				"Ixodes ricinus"
		);

		ingestService.ingest(List.of(record));

		verify(sightingRepository).save(any(Sighting.class));
		verify(rejectedRecordRepository, never()).save(any(RejectedRecord.class));
	}

	@Test
	void recordWithMissingIdIsRejected() {

		ElancoSightingDto record = new ElancoSightingDto(
				null,
				"2024-01-01T12:00:00",
				"London",
				"Tick",
				"Ixodes ricinus"
		);

		ingestService.ingest(List.of(record));

		ArgumentCaptor<RejectedRecord> captor =
				ArgumentCaptor.forClass(RejectedRecord.class);

		verify(rejectedRecordRepository).save(captor.capture());

		assertEquals("Missing ID", captor.getValue().getReason());
		verify(sightingRepository, never()).save(any(Sighting.class));
	}

	@Test
	void recordWithInvalidDateIsRejected() {

		ElancoSightingDto record = new ElancoSightingDto(
				"test-2",
				"not-a-date",
				"London",
				"Tick",
				"Ixodes ricinus"
		);

		ingestService.ingest(List.of(record));

		ArgumentCaptor<RejectedRecord> captor =
				ArgumentCaptor.forClass(RejectedRecord.class);

		verify(rejectedRecordRepository).save(captor.capture());

		assertEquals("Invalid date format", captor.getValue().getReason());
		verify(sightingRepository, never()).save(any(Sighting.class));
	}

	@Test
	void existingIdIsRejectedAsDuplicate() {

		Sighting existing = new Sighting(
				"test-3",
				LocalDateTime.parse("2024-01-01T12:00:00"),
				"London",
				"Tick",
				"Ixodes ricinus"
		);

		when(sightingRepository.findAllById(any()))
				.thenReturn(List.of(existing));

		ElancoSightingDto record = new ElancoSightingDto(
				"test-3",
				"2024-01-02T12:00:00",
				"London",
				"Tick",
				"Ixodes ricinus"
		);

		ingestService.ingest(List.of(record));

		ArgumentCaptor<RejectedRecord> captor =
				ArgumentCaptor.forClass(RejectedRecord.class);

		verify(rejectedRecordRepository).save(captor.capture());

		assertEquals("Duplicate ID", captor.getValue().getReason());
		verify(sightingRepository, never()).save(any(Sighting.class));
	}

	@Test
	void duplicateIdsWithinSameBatchOnlySaveOneSighting() {

		ElancoSightingDto first = new ElancoSightingDto(
				"test-4",
				"2024-01-01T12:00:00",
				"London",
				"Tick",
				"Ixodes ricinus"
		);

		ElancoSightingDto second = new ElancoSightingDto(
				"test-4",
				"2024-01-02T12:00:00",
				"Manchester",
				"Tick",
				"Ixodes ricinus"
		);

		ingestService.ingest(List.of(first, second));

		verify(sightingRepository, times(1)).save(any(Sighting.class));

		ArgumentCaptor<RejectedRecord> captor =
				ArgumentCaptor.forClass(RejectedRecord.class);

		verify(rejectedRecordRepository).save(captor.capture());

		assertEquals("Duplicate ID", captor.getValue().getReason());
	}
}
