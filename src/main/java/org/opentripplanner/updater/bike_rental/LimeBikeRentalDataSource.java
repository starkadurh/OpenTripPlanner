package org.opentripplanner.updater.bike_rental;

import com.fasterxml.jackson.databind.JsonNode;
import org.opentripplanner.routing.bike_rental.BikeRentalStation;
import org.opentripplanner.util.NonLocalizedString;

import java.util.HashSet;

public class LimeBikeRentalDataSource extends GenericJsonBikeStationDataSource   {
    private String networkName;

    public LimeBikeRentalDataSource(String networkName) {
        super("");
        if (networkName != null && !networkName.isEmpty()) {
            this.networkName = networkName;
        } else {
            this.networkName = "LimeBike";
        }
    }

    public BikeRentalStation makeStation(JsonNode stationNode) {

        BikeRentalStation brstation = new BikeRentalStation();
        stationNode = stationNode.path("attributes");

        brstation.networks = new HashSet<String>();
        brstation.networks.add(this.networkName);

        brstation.id = "Lime_"+stationNode.path("last_three").toString();
        brstation.name =  new NonLocalizedString("Limebike " + brstation.id);
        brstation.x = stationNode.path("longitude").asDouble();
        brstation.y = stationNode.path("latitude").asDouble();
        brstation.bikesAvailable = 1;
        brstation.spacesAvailable = 0;
        brstation.isFloatingBike = true;
        brstation.allowDropoff = false;

        return brstation;
    }

}
