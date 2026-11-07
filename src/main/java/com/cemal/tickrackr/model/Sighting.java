package com.cemal.tickrackr.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sightings", indexes = {
        @Index(columnList = "sightingDate"),
        @Index(columnList = "location")
})
public class Sighting {

    @Id
    private String id;

    private LocalDateTime sightingDate;
    private String location;
    private String species;
    private String latinName;

    protected Sighting() {}

    public Sighting(String id, LocalDateTime sightingDate, String location,
                    String species, String latinName) {
        this.id = id;
        this.sightingDate = sightingDate;
        this.location = location;
        this.species = species;
        this.latinName = latinName;
    }

    public String getId() { return id; }
    public LocalDateTime getSightingDate() { return sightingDate; }
    public String getLocation() { return location; }
    public String getELSpecies() { return species; }
    public String getLatinName() { return latinName; }
}