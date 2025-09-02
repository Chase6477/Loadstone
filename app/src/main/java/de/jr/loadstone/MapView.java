package de.jr.loadstone;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.config.Configuration;

import de.jr.loadstone.databinding.MapBinding;

public class MapView extends Fragment {

    private MapBinding binding;

    private OSMMap mapView;

    private FusedLocationProviderClient fusedClient;

    private LocationListener locationListener;

    private Coordinate destination;

    private Coordinate lastMapPosition;

    private float lastMapZoom;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = MapBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            destination = new Coordinate(
                    getArguments().getDouble("lat", -1),
                    getArguments().getDouble("lon", -1)
            );
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        mapView = new OSMMap(
                binding.selectionMap, new Coordinate(0, 0),
                (int) Float.parseFloat(prefs.getString(getString(R.string.max_map_data), String.valueOf(512)))
        );

        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());

        fusedClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        mapView.setGPSMarker(new Coordinate(lat, lon));
                        mapView.moveToGpsMarker();
                        mapView.enableGPSMarker(true);
                        mapView.enableDestinationMarker(true);
                        mapView.setDestinationMarker(destination);

                        if (lastMapPosition != null) {
                            mapView.moveCenterTo(lastMapPosition);
                            mapView.setZoom(lastMapZoom);
                        }
                    }
                });

        locationListener = locationResult ->
                mapView.setGPSMarker(new Coordinate(locationResult.getLatitude(), locationResult.getLongitude()));


        startLocationRequests(fusedClient);

        binding.mapBackCompass.setOnClickListener(v ->
                    NavHostFragment.findNavController(MapView.this)
                            .navigate(R.id.action_mapView_to_compassView, destinationArgs())
        );

        binding.mapBackSelection.setOnClickListener(v ->
                    NavHostFragment.findNavController(MapView.this)
                            .navigate(R.id.action_mapView_to_selectionView, destinationArgs())
        );
    }

    private Bundle destinationArgs() {
        Bundle args = new Bundle();

        args.putDouble("lat", destination.latitude);
        args.putDouble("lon", destination.longitude);

        return args;
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void startLocationRequests(FusedLocationProviderClient fusedClient) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                (int) Float.parseFloat(prefs.getString(getString(R.string.gps_refresh_rate), String.valueOf(500))))
                .build();

        fusedClient.requestLocationUpdates(locationRequest, locationListener, Looper.getMainLooper());

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        fusedClient.removeLocationUpdates(locationListener);
        lastMapPosition = mapView.getCenterPosition();
        lastMapZoom = mapView.getZoom();
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onResume() {
        super.onResume();
        startLocationRequests(fusedClient);
    }
}