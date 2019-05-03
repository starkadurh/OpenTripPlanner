package org.opentripplanner.updater.bike_rental;


import com.fasterxml.jackson.databind.JsonNode;
import org.opentripplanner.routing.bike_rental.BikeRentalStation;
import org.opentripplanner.util.NonLocalizedString;

import java.util.HashSet;

/**
 * Build a BikeRentalStation object from Publibike data source JsonNode object.
 *
 * @see GenericJsonBikeRentalDataSource
 */
public class PubliBikeRentalDataSource extends GenericJsonBikeStationDataSource  {

    private String networkName;

    public PubliBikeRentalDataSource(String networkName) {
        super("");
        if (networkName != null && !networkName.isEmpty()) {
            this.networkName = networkName;
        } else {
            this.networkName = "PubliBike";
        }
    }

    public BikeRentalStation makeStation(JsonNode stationNode) {

        BikeRentalStation brstation = new BikeRentalStation();

        brstation.networks = new HashSet<String>();
        brstation.networks.add(this.networkName);

        brstation.id = "PubliBike_"+stationNode.path("id").toString();
        brstation.x = stationNode.path("longitude").asDouble();
        brstation.y = stationNode.path("latitude").asDouble();
        brstation.name =  new NonLocalizedString(brstation.id);
        brstation.bikesAvailable = stationNode.path("vehicles").size();

        if (!stationNode.path("state").path("name").asText().equals("Active")) {
            brstation.bikesAvailable = 0;
        }

        return brstation;
    }

}
