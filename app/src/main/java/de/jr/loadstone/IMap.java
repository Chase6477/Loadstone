package de.jr.loadstone;

public interface IMap {

    void setGPSMarker(Coordinate gps);

    void createMap(Coordinate lastLocation, int mapSaveLimit);

    void moveToGpsMarker();

    void enableGPSMarker(boolean enable);

    Coordinate getDestinationMarker();

    void setDestinationMarker();

}
