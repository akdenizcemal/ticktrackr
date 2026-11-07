package com.cemal.tickrackr.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rejected_records")
public class RejectedRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String rawId;
    private String rawDate;
    private String location;
    private String species;
    private String latinName;
    private String reason;
    private LocalDateTime rejectedAt;

    protected RejectedRecord() {}

    public RejectedRecord(String rawId, String rawDate, String location,
                          String species, String latinName, String reason) {
        this.rawId = rawId;
        this.rawDate = rawDate;
        this.location = location;
        this.species = species;
        this.latinName = latinName;
        this.reason = reason;
        this.rejectedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getRawId() { return rawId; }
    public String getRawDate() { return rawDate; }
    public String getLocation() { return location; }
    public String getSpecies() { return species; }
    public String getLatinName() { return latinName; }
    public String getReason() { return reason; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }
}