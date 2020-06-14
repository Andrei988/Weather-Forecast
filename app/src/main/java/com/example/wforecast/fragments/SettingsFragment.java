package com.example.wforecast.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wforecast.R;
import com.example.wforecast.utils.Common;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";
    private CheckBox checkBoxCelsius;
    private CheckBox checkBoxFahrenheit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        checkBoxCelsius = view.findViewById(R.id.checkBoxCelsius);
        checkBoxFahrenheit = view.findViewById(R.id.checkBoxFahrenheit);
        if (Common.UNITS.equals("metric")) {
            checkBoxCelsius.setChecked(true);
        } else if (Common.UNITS.equals("imperial")) {
            checkBoxFahrenheit.setChecked(true);
        }
        checkBoxFahrenheit.setOnClickListener(v -> {
            if (checkBoxFahrenheit.isChecked()) {
                checkBoxCelsius.setChecked(false);
                Common.UNITS = "imperial";
                Toast.makeText(getContext(), "Changed to Fahrenheit", Toast.LENGTH_SHORT).show();
            }
        });

        checkBoxCelsius.setOnClickListener(v -> {
            if (checkBoxCelsius.isChecked()) {
                checkBoxFahrenheit.setChecked(false);
                Common.UNITS = "metric";
                Toast.makeText(getContext(), "Changed to Celsius", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Common.UNITS.equals("metric")) {
            checkBoxFahrenheit.setChecked(false);
            checkBoxCelsius.setChecked(true);
        } else if (Common.UNITS.equals("imperial")) {
            checkBoxFahrenheit.setChecked(true);
            checkBoxCelsius.setChecked(false);
        }

    }
}
