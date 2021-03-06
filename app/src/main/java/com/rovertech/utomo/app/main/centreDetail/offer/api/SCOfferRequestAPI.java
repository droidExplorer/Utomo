package com.rovertech.utomo.app.main.centreDetail.offer.api;

import com.rovertech.utomo.app.helper.AppConstant;
import com.rovertech.utomo.app.main.centreDetail.offer.model.SCOfferResp;
import com.rovertech.utomo.app.offers.model.AdminOfferResp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by raghavthakkar on 13-04-2016.
 */
public interface SCOfferRequestAPI {

    String SERVICECENTERID = "ServiceCentreID";

    @GET(AppConstant.FETCH_SC_OFFER + "{" + SERVICECENTERID + "}")
    Call<SCOfferResp> SCOfferApi(@Path(SERVICECENTERID) int ServiceCentreID);

}
