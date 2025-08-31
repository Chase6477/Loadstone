package de.jr.loadstone;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class OSMMap implements IMap {

    private final MapView map;

    private final IMapController mapController;
    private final Marker gpsMarker;
    private boolean isEnabled = true;

    public OSMMap(MapView mapView, Coordinate lastLocation, int mapSaveLimit) {
        this.map = mapView;
        mapController = map.getController();

        gpsMarker = new Marker(map);

        createMap(lastLocation, mapSaveLimit);
    }

    @Override
    public void createMap(Coordinate lastLocation, int mapSaveLimit) {
        mapController.setZoom(12.0);
        map.setMultiTouchControls(true);
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);
        map.setMaxZoomLevel(20.0);
        map.setMinZoomLevel(2.0);

        Configuration.getInstance().setTileFileSystemCacheMaxBytes((long) mapSaveLimit * 1024 * 1024);
        Configuration.getInstance().setTileFileSystemCacheTrimBytes((long) (mapSaveLimit * 0.75 * 1024 * 1024));

        map.getOverlays().add(gpsMarker);

        gpsMarker.setInfoWindow(null);
        gpsMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        setGPSMarker(lastLocation);
    }

    @Override
    public void moveToGpsMarker() {
        mapController.setCenter(gpsMarker.getPosition());
    }

    @Override
    public void setGPSMarker(Coordinate gps) {
        if (isEnabled) {
            gpsMarker.setPosition(coordinateToGeoPoint(gps));
        }
    }

    @Override
    public void enableGPSMarker(boolean enable) {
        this.isEnabled = enable;
    }

    @Override
    public Coordinate getDestinationMarker() {
        return new Coordinate(map.getMapCenter().getLatitude(), map.getMapCenter().getLongitude());
    }

    @Override
    public void setDestinationMarker() {

    }

    private GeoPoint coordinateToGeoPoint(Coordinate coordinate) {
        return new GeoPoint(coordinate.latitude, coordinate.longitude);
    }
}
