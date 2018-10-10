package co.zenpets.users.utils.models.kennels.kennels;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface KennelsAPI {

    /** FETCH A LIST OF KENNELS (FOR PET PARENTS) **/
    @GET("fetchKennelsListByCity")
    Call<Kennels> fetchKennelsListByCity(
            @Query("cityID") String cityID,
            @Query("originLat") String originLat,
            @Query("originLon") String originLon,
            @Query("pageNumber") String pageNumber);

    /** FETCH A LIST OF KENNELS (FOR PET PARENTS) **/
    @GET("fetchKennelsListByCityTest")
    Call<Kennels> fetchKennelsListByCityTest(
            @Query("cityID") String cityID,
            @Query("pageNumber") String pageNumber);

    /** FETCH THE TOTAL NUMBER OF KENNEL PAGES **/
    @GET("fetchKennelPages")
    Call<KennelPages> fetchKennelPages(@Query("cityID") String cityID);

    /** FETCH THE TOTAL NUMBER OF KENNEL PAGES **/
    @GET("searchKennels")
    Call<Kennels> searchKennels(
            @Query("cityID") String cityID,
            @Query("searchTerm") String searchTerm);

    /** FETCH THE KENNEL'S DETAILS **/
    @GET("fetchKennelDetails")
    Call<Kennel> fetchKennelDetails(
            @Query("kennelID") String kennelID,
            @Query("originLat") String originLat,
            @Query("originLon") String originLon);

    /** FETCH THE KENNEL OWNER'S PROFILE **/
    @GET("fetchKennelOwnerProfile")
    Call<Account> fetchKennelOwnerProfile(@Query("kennelOwnerID") String kennelOwnerID);
}