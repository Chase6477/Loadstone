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
            {R.color.gps_3, R.string.compass_good}
    };
    private final float[] results = new float[3];
    SharedPreferences prefs;
    private SortedFixedList<Float> sortedFixedList;
    private SortedFixedList<Float> sortedFixedList180;
    /**
     * Is needed to calculate the median at the 0 - 360 gap by shifting it by 180 degrees
     * // Removing it causes the needle to be imprecise at ~ 330 - 30 degrees
     **/

    private long timeSinceLast;
    private float smoothedHoldValue = -1;
    private long maxTimeSinceLast;
    private float maxAngle;
    private DeviceRotation deviceRotation;
    private boolean medianSmoothing;
    private boolean calmSmoothing;
    private double destinationLat;
    private double destinationLon;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private CompassBinding binding;
    private float sensorRotation;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        calmSmoothing = prefs.getBoolean(getString(R.string.calm_smoothing), true);
        medianSmoothing = prefs.getBoolean(getString(R.string.median_smoothing), true);

        if (getArguments() != null) {
            destinationLat = getArguments().getDouble("lat", -1);
            destinationLon = getArguments().getDouble("lon", -1);
            if (destinationLat == -1 || destinationLon == -1) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        }

        if (medianSmoothing || calmSmoothing) {
            int smoothnessListSize = (int) getPrefFloatValue(R.string.buffer_size, 21);
            maxTimeSinceLast = (long) getPrefFloatValue(R.string.time_in_milliseconds, 500);
            maxAngle = getPrefFloatValue(R.string.angle, 10);

            sortedFixedList = new SortedFixedList<>(smoothnessListSize, 0.f);
            sortedFixedList180 = new SortedFixedList<>(smoothnessListSize, 0.f);
        }


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String pos = getString(R.string.gps) + "\nLat: " + lat + "\nLon: " + lon;
                    Location.distanceBetween(lat, lon, destinationLat, destinationLon, results);
                    pos += "\n" + results[0];
                    binding.textPosition.setText(pos);
                }
            }
        };

        binding.buttonBackCompass.setOnClickListener(v ->
                NavHostFragment.findNavController(CompassView.this)
                        .navigate(R.id.action_compassView_to_selectionView)
        );
    }

    // Yes, all these casts are needed
    private float getPrefFloatValue(int id, double defaultValue) {
        return Float.parseFloat(prefs.getString(getString(id), String.valueOf(defaultValue)));
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                500L)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        deviceRotation = new DeviceRotation(sensorManager);

        deviceRotation.setOrientationListener((angles, delta) -> {
            sensorRotation = (float) (Math.toDegrees(angles[0]) + 360) % 360;
            float value = getSmoothedValue(sensorRotation, delta);
            binding.compassNeedle.setRotation(results[1] - value);
            binding.compassBackground.setRotation(-value);
        });

        deviceRotation.setAccuracyListener((sensor, accuracy) -> {
            if (sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD)
                return;
            binding.textCompassStatus.setTextColor(ContextCompat.getColor(requireContext(), compassStatus[accuracy][0]));
            binding.textCompassStatus.setText(getString(compassStatus[accuracy][1]));
        });
    }


    public float getSmoothedValue(float sensorRotation, long delta) {

        if (!(calmSmoothing || medianSmoothing))
            return sensorRotation;

        sortedFixedList.add(sensorRotation);
        sortedFixedList180.add((sensorRotation + 180) % 360);
        timeSinceLast += delta;

        float median = sortedFixedList.getMedian();

        float result = (sortedFixedList.get(sortedFixedList.size - 1) - sortedFixedList.get(0));

        if (result > 180)
            median = (sortedFixedList180.getMedian() - 180) % 360;

        result %= 360;

        if (result > maxAngle && medianSmoothing) {
            smoothedHoldValue = -1;
            return median;
        }

        if (calmSmoothing) {
            if (timeSinceLast >= maxTimeSinceLast || smoothedHoldValue == -1) {
                timeSinceLast = 0;
                smoothedHoldValue = median;
            }
            return smoothedHoldValue;
        }
        return sensorRotation;


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
        startLocationUpdates();
        deviceRotation.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
        deviceRotation.pause();
    }
}