package de.jr.loadstone.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import de.jr.loadstone.R;

public class PermissionView extends Fragment {

    private ActivityResultLauncher<String> requestPermissionLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(), isGranted -> {

                    if (isGranted) {
                        switchToSelectionView();

                    } else if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        toastText(R.string.coarse_location_warning);

                    } else {
                        toastText(R.string.denied_location_warning);
                    }
                });
    }

    private void toastText(int id) {
        Toast.makeText(requireContext(),
                id,
                Toast.LENGTH_SHORT).show();
    }

    private void switchToSelectionView() {
        if (getView() == null)
            return;

        getView().post(() ->
                NavHostFragment.findNavController(PermissionView.this)
                        .navigate(R.id.action_permissionView_to_selectionView)
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.permission, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
