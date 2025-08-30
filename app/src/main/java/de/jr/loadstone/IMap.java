package de.jr.loadstone;

public interface IMap {

    void createMap(Coordinate lastLocation);

    void setGPSMarker(Coordinate gps);

    void moveToGpsMarker();

    void enableGPSMarker(boolean enable);

    Coordinate getDestinationMarker();

    void setDestinationMarker();

}
