package de.jr.loadstone;

import androidx.lifecycle.ViewModel;

public class ActivityViewModel extends ViewModel {

    private Coordinate destination;

    private float lastMapZoom;
    private Coordinate lastMapLocation;


    public void setDestination(Coordinate destination) {
        this.destination = destination;
    }

    public Coordinate getDestination() {
        return destination;
    }

    public Coordinate getLastMapLocation() {
        return lastMapLocation;
    }

    public void setLastMapLocation(Coordinate lastMapLocation) {
        this.lastMapLocation = lastMapLocation;
    }

    public float getLastMapZoom() {
        return lastMapZoom;
    }

    public void setLastMapZoom(float lastMapZoom) {
        this.lastMapZoom = lastMapZoom;
    }
}
