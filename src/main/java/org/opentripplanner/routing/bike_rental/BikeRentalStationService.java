package org.opentripplanner.routing.bike_rental;

import java.io.Serializable;
import java.util.*;

import org.opentripplanner.routing.bike_park.BikePark;

public class BikeRentalStationService implements Serializable {
    private static final long serialVersionUID = -1288992939159246764L;

    private Set<BikeRentalStation> bikeRentalStations = new HashSet<>();

    private Map<String, BikeRentalRegion> bikeRentalRegions = new HashMap<>();

    private Set<BikePark> bikeParks = new HashSet<>();

    public Collection<BikeRentalStation> getBikeRentalStations() {
        return bikeRentalStations;
    }

    public void addBikeRentalStation(BikeRentalStation bikeRentalStation) {
        // Remove old reference first, as adding will be a no-op if already present
        bikeRentalStations.remove(bikeRentalStation);
        bikeRentalStations.add(bikeRentalStation);
    }

    public void removeBikeRentalStation(BikeRentalStation bikeRentalStation) {
        bikeRentalStations.remove(bikeRentalStation);
    }

    public Map<String, BikeRentalRegion> getBikeRentalRegions() {
        return bikeRentalRegions;
    }

    public void addBikeRentalRegion(BikeRentalRegion bikeRentalRegion) {
        bikeRentalRegions.put(bikeRentalRegion.network, bikeRentalRegion);
    }

    public Collection<BikePark> getBikeParks() {
        return bikeParks;
    }

    public void addBikePark(BikePark bikePark) {
        // Remove old reference first, as adding will be a no-op if already present
        bikeParks.remove(bikePark);
        bikeParks.add(bikePark);
    }

    public void removeBikePark(BikePark bikePark) {
        bikeParks.remove(bikePark);
    }
}
