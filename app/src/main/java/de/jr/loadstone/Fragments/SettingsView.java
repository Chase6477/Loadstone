package de.jr.loadstone.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import de.jr.loadstone.MainActivity;
import de.jr.loadstone.databinding.SettingsBinding;

public class SettingsView extends Fragment {

    private SettingsBinding binding;
    private MainActivity mainActivity;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mainActivity = (MainActivity) requireActivity();

        mainActivity.hideFab();

        binding = SettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.buttonBackSettings.setOnClickListener(v ->
                NavHostFragment.findNavController(SettingsView.this)
                        .popBackStack()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mainActivity.showFab();
        binding = null;
    }
}
