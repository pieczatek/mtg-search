package com.icyjars.mtgcards;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface MtgioService {

    String SERVICE_ENDPOINT = "https://api.magicthegathering.io";

    @GET("/v1/cards")
    Call<Mtgio> getCards(@QueryMap Map<String, String> parameters);

    @GET("/v1/cards/{multiverseid}")
    Call<MtgioSingleCard> getSingleCard(@Path("multiverseid") int multiverseid);

}
