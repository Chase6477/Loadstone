package de.jr.loadstone;

public interface IGeoCoding {

    Coordinate getLocation(String location);

    String[] getCompletion(String location);


}
