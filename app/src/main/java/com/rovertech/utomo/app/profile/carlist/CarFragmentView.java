package com.rovertech.utomo.app.profile.carlist;

import java.util.ArrayList;

/**
 * Created by sagartahelyani on 14-03-2016.
 */
public interface CarFragmentView {

    void setCarList(ArrayList<CarPojo> adapter);

    void setEmptyView();

    void showProgress();

    void hideProgress();
}


