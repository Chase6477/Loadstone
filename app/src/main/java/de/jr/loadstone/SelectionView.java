package de.jr.loadstone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import de.jr.loadstone.databinding.SelectionBinding;

public class SelectionView extends Fragment {

    private SelectionBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = SelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonStart.setOnClickListener(v ->
                {

                    UserInput.destinationCoordinate = getValidatedCoordinate(binding.destinationCoordinates.getText().toString());

                    NavHostFragment.findNavController(SelectionView.this)
                            .navigate(R.id.action_selectionView_to_compassView);
                }
        );
    }

    private Coordinate getValidatedCoordinate(String coordinate) {

        double lat = Double.parseDouble(coordinate.split(", ")[0]);
        double lon = Double.parseDouble(coordinate.split(", ")[1]);

        return new Coordinate(lat, lon);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}