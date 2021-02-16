package com.ankit.library_management.imageSliderFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ankit.library_management.R;


public class FragmentSliderOne extends Fragment {
    // make sure you import android.support.v4.app.Fragment


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_slider_one_layout, container, false);


        return view;

    }
}
