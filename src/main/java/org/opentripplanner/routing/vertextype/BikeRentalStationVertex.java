package org.opentripplanner.routing.vertextype;

import org.opentripplanner.common.MavenVersion;
import org.opentripplanner.routing.bike_rental.BikeRentalStation;
import org.opentripplanner.routing.core.TraverseMode;
import org.opentripplanner.routing.graph.Graph;
import org.opentripplanner.routing.graph.Vertex;

import java.util.Set;

/**
 * A vertex for a bike rental station.
 * It is connected to the streets by a StreetBikeRentalLink.
 * To allow transitions on and off a bike, it has RentABike* loop edges.
 *
 * @author laurent
 *
 * TODO if we continue using this for car rental and flex systems, change name to VehicleRentalStationVertex
 */
public class BikeRentalStationVertex extends Vertex {

    private static final long serialVersionUID = MavenVersion.VERSION.getUID();

    private int bikesAvailable;

    private int spacesAvailable;

    private boolean pickupAllowed;

    /**
     * isFloatingBike denotes whether a bike is dockless or not.
     */
    public final boolean isFloatingBike;

    private String id;

    /** Some car rental systems and flex transit systems work exactly like bike rental, but with cars. */
    private boolean isCarStation;

    /**
     * List of compatible network names. Null (default) to be compatible with all.
     */
    public Set<String> networks;

    public BikeRentalStationVertex(Graph g, BikeRentalStation station) {
        //FIXME: raw_name can be null if bike station is made from graph updater
        super(g, "bike rental station " + station.id, station.x, station.y, station.name);
        this.setId(station.id);
        this.networks = station.networks;
        this.setBikesAvailable(station.bikesAvailable);
        this.setSpacesAvailable(station.spacesAvailable);
        this.isCarStation = station.isCarStation;
        this.setPickupAllowed(station.allowPickup);
        this.isFloatingBike = station.isFloatingBike;
    }

    public int getBikesAvailable() {
        return bikesAvailable;
    }

    public int getSpacesAvailable() {
        return spacesAvailable;
    }

    public void setBikesAvailable(int bikes) {
        this.bikesAvailable = bikes;
    }

    public void setSpacesAvailable(int spaces) {
        this.spacesAvailable = spaces;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Tell the routing algorithm what kind of vehicle is being rented or dropped off here.
     * Some car rental systems and flex transit systems work exactly like bike rental, but with cars.
     * We can model them as bike rental systems by changing only this one detail.
     */
    public TraverseMode getVehicleMode () {
         return isCarStation ? TraverseMode.CAR : TraverseMode.BICYCLE;
    }

    public boolean isPickupAllowed() {
        return pickupAllowed;
    }

    public void setPickupAllowed(boolean pickupAllowed) {
        this.pickupAllowed = pickupAllowed;
    }
}
