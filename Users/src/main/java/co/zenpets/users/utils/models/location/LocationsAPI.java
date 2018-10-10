package co.zenpets.users.utils.models.location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationsAPI {

    /* GET ALL COUNTRIES */
    @GET("allCountries")
    Call<Countries> allCountries();

    /* FETCH THE COUNTRY ID */
    @GET("getCountryID")
    Call<Country> getCountryID(@Query("countryName") String countryName);

    /* GET ALL STATES IN A COUNTRY */
    @GET("allStates")
    Call<States> allStates(@Query("countryID") String countryID);

    /* GET THE STATE ID */
    @GET("getStateID")
    Call<State> getStateID(@Query("stateName") String stateName);

    /* GET ALL CITIES IN A STATE */
    @GET("allCities")
    Call<Cities> allCities(@Query("stateID") String stateID);

    /* GET THE CITY ID */
    @GET("getCityID")
    Call<City> getCityID(@Query("cityName") String cityName);

    /* GET ALL LOCALITIES IN A CITY */
    @GET("allLocalities")
    Call<Localities> allLocalities(@Query("cityID") String cityID);

    /* FETCH ALL LOCALITIES IN A CITY (BY THE CITY NAME) */
    @GET("fetchLocalitiesByCity")
    Call<Localities> fetchLocalitiesByCity(@Query("cityName") String cityName);

    /* GET THE LOCALITY ID */
    @GET("getLocalityID")
    Call<Locality> getLocalityID(@Query("localityName") String localityName);
}