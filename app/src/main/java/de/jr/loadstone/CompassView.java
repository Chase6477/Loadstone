package de.jr.loadstone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

    private SortedFixedList<Float> sortedFixedList; //smoothness of the sensor rotation values
    private float smoothnessValue = -1;
    private int iteration;
    private int maxIterationCount;
    private float maxAngle;
    private int smoothnessListSize;
    private DeviceRotation deviceRotation;

    private boolean medianSmoothing;
    private boolean iterationSmoothing;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private CompassBinding binding;

    private float sensorRotation;
    private final float[] results = new float[3];

    SharedPreferences prefs;


    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SensorManager sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);
        deviceRotation = new DeviceRotation(sensorManager);

        deviceRotation.setOrientationListener(angles -> {
            sensorRotation = (float) (Math.toDegrees(angles[0]) + 360) % 360;
            sortedFixedList.add(sensorRotation);
            float value = getPlausibleRotationValue();
            binding.compassNeedle.setRotation(results[1] - value);
            binding.textRotation.setText(value + "\n" + sensorRotation);
            binding.compassBackground.setRotation(-value);
        });

        deviceRotation.setAccuracyListener((sensor, accuracy) -> {
            if (sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD)
                return;

            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    binding.textRotation.setTextColor(Color.rgb(255, 0, 0));
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    binding.textRotation.setTextColor(Color.rgb(255, 165, 0));
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    binding.textRotation.setTextColor(Color.rgb(255, 255, 0));
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    binding.textRotation.setTextColor(Color.rgb(0, 128, 0));
                    break;

            }
        });

    }

    public float getPlausibleRotationValue() {
        float result = sortedFixedList.get(sortedFixedList.size - 1) - sortedFixedList.get(0);
        iteration++;


        if (result > maxAngle && medianSmoothing) {
            smoothnessValue = -1;
            return sortedFixedList.getMedian();
        } else if (iterationSmoothing) {
            if (smoothnessValue == -1 || iteration >= maxIterationCount) {
                iteration = 0;
                smoothnessValue = sortedFixedList.getMedian();
            }
            return smoothnessValue;
        } else {
            return sensorRotation;
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = CompassBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());

        iterationSmoothing =  prefs.getBoolean("settings_switch_iterational_smoothing", true);
        medianSmoothing =  prefs.getBoolean("settings_switch_median_smoothing", true);
        maxIterationCount = Integer.parseInt(prefs.getString("settings_text_iterational_smoothing", "100"));
        maxAngle = Float.parseFloat(prefs.getString("settings_text_median_smoothing_angle", "10"));
        smoothnessListSize = Integer.parseInt(prefs.getString("settings_text_median_smoothing_size", "21"));



        sortedFixedList = new SortedFixedList<>(smoothnessListSize, 0.f);


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String pos = "Lat: " + lat + "\nLon: " + lon;
                    Location.distanceBetween(lat, lon, UserInput.destinationCoordinate.latitude, UserInput.destinationCoordinate.longitude, results);
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

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100L)
                .setMaxUpdateDelayMillis(500L)
                .build();

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
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