package com.rovertech.utomo.app.profile;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rovertech.utomo.app.R;
import com.rovertech.utomo.app.main.dashboard.DashboardActivity;
import com.rovertech.utomo.app.temp.DemoActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class CarListFragment extends Fragment {

    private View parentView;
    private ProfileActivity activity;

    public CarListFragment() {
        // Required empty public constructor
    }

    public static CarListFragment newInstance() {
        CarListFragment fragment = new CarListFragment();

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.fragment_car_list, container, false);
        init();
        return parentView;
    }

    private void init() {
        activity = (ProfileActivity) getActivity();
    }

}