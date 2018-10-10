package co.zenpets.users.utils.models.groomers.groomers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GroomersAPI {

    /** FETCH A LIST OF GROOMERS IN THE SELECTED CITY (FOR PET PARENTS) **/
    @GET("fetchGroomersList")
    Call<Groomers> fetchGroomersList(
            @Query("cityID") String cityID,
            @Query("originLat") String originLat,
            @Query("originLon") String originLon,
            @Query("pageNumber") String pageNumber);

    /** FETCH THE TOTAL NUMBER OF GROOMER PAGES **/
    @GET("fetchGroomerPages")
    Call<GroomerPages> fetchGroomerPages(@Query("cityID") String cityID);

    /** FETCH THE GROOMER DETAILS **/
    @GET("fetchGroomerDetails")
    Call<Groomer> fetchGroomerDetails(
            @Query("groomerID") String groomerID,
            @Query("originLat") String originLat,
            @Query("originLon") String originLon);
}