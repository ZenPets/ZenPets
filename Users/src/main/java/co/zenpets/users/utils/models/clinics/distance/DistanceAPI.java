package co.zenpets.users.utils.models.clinics.distance;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DistanceAPI {

    /** GET THE DISTANCE TO THE CLINIC **/
    @GET("json")
    Call<String> json(
            @Query("origin") String origin,
            @Query("destination") String destination,
            @Query("sensor") String sensor,
            @Query("key") String key);
}