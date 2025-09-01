package de.jr.loadstone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import de.jr.loadstone.databinding.CompassBinding;

public class CompassView extends Fragment {

    private final int[][] compassStatus = {
            {R.color.gps_0, R.string.compass_uncalibrated},
            {R.color.gps_1, R.string.compass_bad},
            {R.color.gps_2, R.string.compass_ok},
            {R.color.gps_3, R.string.compass_good},
            {R.color.white, R.string.compass_no_data}
    };
    private final float[] results = new float[3];

    private SharedPreferences prefs;
    private DeviceRotation deviceRotation;
    private Coordinate destination;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private CompassBinding binding;
    private Coordinate current = new Coordinate(0, 0);
    private Smoothing smoothing;
    private int compassAccurac = 4;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        deviceRotation = new DeviceRotation(sensorManager);

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        updateCompassAccuracyText();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        if (getArguments() != null) {
            destination = new Coordinate(
                    getArguments().getDouble("lat", -1),
                    getArguments().getDouble("lon", -1)
            );
        }

        boolean calmSmoothing = prefs.getBoolean(getString(R.string.calm_smoothing), true);
        boolean medianSmoothing = prefs.getBoolean(getString(R.string.median_smoothing), true);

        smoothing = new Smoothing(
                (long) getPrefFloatValue(R.string.time_in_milliseconds, 500),
                getPrefFloatValue(R.string.angle, 10),
                (int) getPrefFloatValue(R.string.buffer_size, 21),
                calmSmoothing,
                medianSmoothing
        );

        deviceRotation.setOrientationListener((angles, delta) -> {
            float value = smoothing.getSmoothedValue(
                    (float) (Math.toDegrees(angles[0]) + 360) % 360,
                    delta
            );

            binding.compassNeedle.setRotation(results[1] - value);
            binding.compassBackground.setRotation(-value);
        });


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    current.latitude = location.getLatitude();
                    current.longitude = location.getLongitude();
                    updateLocationText();
                }
            }
        };

        binding.buttonBackSelection.setOnClickListener(v ->
            NavHostFragment.findNavController(CompassView.this)
                    .navigate(R.id.action_compassView_to_selectionView, destinationArgs())
        );

        binding.buttonMap.setOnClickListener(v ->
            NavHostFragment.findNavController(CompassView.this)
                    .navigate(R.id.action_compassView_to_mapView, destinationArgs())
        );
    }

    private Bundle destinationArgs() {
        Bundle args = new Bundle();

        args.putDouble("lat", destination.latitude);
        args.putDouble("lon", destination.longitude);

        return args;
    }

    // Yes, all these casts are needed
    private float getPrefFloatValue(int id, double defaultValue) {
        return Float.parseFloat(prefs.getString(getString(id), String.valueOf(defaultValue)));
    }

    @SuppressLint("MissingPermission")
    private void startListenerUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                500L)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

        fusedLocationClient.getLastLocation().addOnSuccessListener(
                lastLocation -> {
                    current = new Coordinate(lastLocation.getLatitude(), lastLocation.getLongitude());
                    updateLocationText();
                }
        );

        deviceRotation.setAccuracyListener((sensor, accuracy) -> {
            if (sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD)
                return;
            compassAccurac = accuracy;
            updateCompassAccuracyText();
        });
    }

    private void updateCompassAccuracyText() {
        binding.textCompassStatus.setTextColor(ContextCompat.getColor(requireContext(), compassStatus[compassAccurac][0]));
        binding.textCompassStatus.setText(getString(compassStatus[compassAccurac][1]));
    }

    private void updateLocationText() {
        String pos = getString(R.string.gps) + "\nLat: " + current.latitude + "\nLon: " + current.longitude;
        Location.distanceBetween(current.latitude, current.longitude, destination.latitude, destination.longitude, results);
        pos += "\n" + results[0] + " m";
        binding.textPosition.setText(pos);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CompassBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        startListenerUpdates();
        deviceRotation.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        deviceRotation.pause();
    }
}