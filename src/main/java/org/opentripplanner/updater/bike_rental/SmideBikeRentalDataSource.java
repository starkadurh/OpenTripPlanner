package org.opentripplanner.updater.bike_rental;

import com.fasterxml.jackson.databind.JsonNode;
import org.opentripplanner.routing.bike_rental.BikeRentalStation;
import org.opentripplanner.util.NonLocalizedString;

import java.util.HashSet;

public class SmideBikeRentalDataSource extends GenericJsonBikeStationDataSource  {

    private String networkName;

    public SmideBikeRentalDataSource(String networkName) {
        super("");
        if (networkName != null && !networkName.isEmpty()) {
            this.networkName = networkName;
        } else {
            this.networkName = "SmideBike";
        }
    }

    public BikeRentalStation makeStation(JsonNode stationNode) {

        BikeRentalStation brstation = new BikeRentalStation();

        brstation.networks = new HashSet<>();
        brstation.networks.add(this.networkName);

        brstation.id = "Smide_"+stationNode.path("id").toString();
        brstation.name =  new NonLocalizedString("Smidebike " + brstation.id);
        brstation.x = stationNode.path("location").path("lng").asDouble();
        brstation.y = stationNode.path("location").path("lat").asDouble();
        brstation.bikesAvailable = 1 ;
        brstation.spacesAvailable = 0;
        brstation.isFloatingBike = true;
        brstation.allowDropoff = false;

        return brstation;
    }
}
