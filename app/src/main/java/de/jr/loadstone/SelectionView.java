package de.jr.loadstone;

import android.Manifest;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.config.Configuration;

import de.jr.loadstone.databinding.SelectionBinding;

public class SelectionView extends Fragment {

    private SelectionBinding binding;

    private OSMMap mapView;

    private FusedLocationProviderClient fusedClient;

    private LocationListener locationListener;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = SelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapView = new OSMMap(binding.selectionMap, new Coordinate(0, 0));

        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext());

        fusedClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        double lat = location.getLatitude();
                        double lon = location.getLongitude();

                        mapView.setGPSMarker(new Coordinate(lat, lon));
                        mapView.moveToGpsMarker();
                    }
                });

        locationListener = locationResult ->
                mapView.setGPSMarker(new Coordinate(locationResult.getLatitude(), locationResult.getLongitude()));


        startLocationRequests(fusedClient);

        binding.buttonStart.setOnClickListener(v ->
                {
                    Bundle args = new Bundle();

                    args.putDouble("lat", mapView.getDestinationMarker().latitude);
                    args.putDouble("lon", mapView.getDestinationMarker().longitude);

                    NavHostFragment.findNavController(SelectionView.this)
                            .navigate(R.id.action_selectionView_to_compassView, args);
                }
        );
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    private void startLocationRequests(FusedLocationProviderClient fusedClient) {

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                5000L)
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
    }

    @RequiresPermission(allOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onResume() {
        super.onResume();
        startLocationRequests(fusedClient);
    }
}