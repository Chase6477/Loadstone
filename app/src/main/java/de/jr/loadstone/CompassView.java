package de.jr.loadstone;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.material.chip.ChipGroup;

import de.jr.loadstone.databinding.CompassBinding;


public class CompassView extends Fragment {

    private final SortedFixedList<Float> sortedFixedList = new SortedFixedList<>(21, 0.f); //smoothness of the sensor rotation values
    private final float MINIMAL_MARGIN = 10.f;
    private float smoothnessValue = -1;
    private byte iteration;
    private DeviceRotation deviceRotation;

    private boolean ENABLE_MEDIAN_SMOOTHING = true;
    private boolean ENABLE_ITERATION_SMOOTHING = false;


    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    private TextView positionText;
    private ImageView compassNeedle;
    private ImageView compassBackground;
    private TextView textRotation;

    private float sensorRotation;
    private final float[] results = new float[3];


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
            compassNeedle.setRotation(results[1] - value);
            textRotation.setText(value + "\n" + sensorRotation);
            compassBackground.setRotation(-value);
        });

        deviceRotation.setAccuracyListener((sensor, accuracy) -> {
            if (sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD)
                return;

            switch (accuracy) {
                case SensorManager.SENSOR_STATUS_UNRELIABLE:
                    textRotation.setTextColor(Color.rgb(255, 0, 0));
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_LOW:
                    textRotation.setTextColor(Color.rgb(255, 165, 0));
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM:
                    textRotation.setTextColor(Color.rgb(255, 255, 0));
                    break;
                case SensorManager.SENSOR_STATUS_ACCURACY_HIGH:
                    textRotation.setTextColor(Color.rgb(0, 128, 0));
                    break;

            }
        });

    }

    public float getPlausibleRotationValue() {
        float result = sortedFixedList.get(sortedFixedList.size - 1) - sortedFixedList.get(0);
        System.out.println(result);
        iteration++;

        System.out.println(ENABLE_MEDIAN_SMOOTHING + ", " + ENABLE_ITERATION_SMOOTHING);


        if (result > MINIMAL_MARGIN && ENABLE_MEDIAN_SMOOTHING) {
            smoothnessValue = -1;
            System.out.println("med");
            return sortedFixedList.getMedian();
        } else if (ENABLE_ITERATION_SMOOTHING) {
            System.out.println("ite");
            if (smoothnessValue == -1 || iteration >= 100) {
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
        CompassBinding.inflate(inflater, container, false);
        return inflater.inflate(R.layout.compass, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        positionText = view.findViewById(R.id.textPosition);
        compassNeedle = view.findViewById(R.id.compassNeedle);
        compassBackground = view.findViewById(R.id.compassBackground);
        textRotation = view.findViewById(R.id.textRotation);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());


        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    double lat = location.getLatitude();
                    double lon = location.getLongitude();
                    String pos = "Lat: " + lat + "\nLon: " + lon;
                    Location.distanceBetween(lat, lon, UserInput.destinationCoordinate.latitude, UserInput.destinationCoordinate.longitude, results);
                    pos += "\n" + results[0];
                    positionText.setText(pos);
                }
            }
        };

        ChipGroup chipGroup = view.findViewById(R.id.chipGroup);

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {

            ENABLE_MEDIAN_SMOOTHING = false;
            ENABLE_ITERATION_SMOOTHING = false;

            if (checkedIds.isEmpty())
                return;

            if (checkedIds.contains(R.id.chipMedianSmooth))
                ENABLE_MEDIAN_SMOOTHING = true;
            if (checkedIds.contains(R.id.chipIterationSmooth))
                ENABLE_ITERATION_SMOOTHING = true;

        });


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