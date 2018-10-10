package co.zenpets.trainers.utils.models.location;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationAPI {

    /* GET ALL COUNTRIES */
    @GET("allCountries")
    Call<CountriesData> allCountries();

    /* FETCH THE COUNTRY ID */
    @GET("getCountryID")
    Call<CountryData> getCountryID(@Query("countryName") String countryName);

    /* GET ALL STATES IN A COUNTRY */
    @GET("allStates")
    Call<StatesData> allStates(@Query("countryID") String countryID);

    /* GET ALL STATE ID */
    @GET("getStateID")
    Call<StateData> getStateID(@Query("stateName") String stateName);

    /* GET ALL CITIES IN A STATE */
    @GET("allCities")
    Call<CitiesData> allCities(@Query("stateID") String stateID);

    /* FETCH THE CITY ID */
    @GET("getCityID")
    Call<CityData> getCityID(@Query("cityName") String cityName);

    /* GET ALL LOCALITIES IN A CITY */
    @GET("allLocalities")
    Call<LocalitiesData> allLocalities(@Query("cityID") String cityID);
}