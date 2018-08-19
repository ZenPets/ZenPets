package biz.zenpets.users.utils.models.boarding;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface BoardingsAPI {

    /** FETCH THE LIST OF HOME BOARDINGS IN A CITY **/
    @GET("fetchBoardingsList")
    Call<Boardings> fetchBoardingsList(
            @Query("cityID") String cityID,
            @Query("pageNumber") String pageNumber,
            @Query("originLat") String originLat,
            @Query("originLon") String originLon);

    /** FETCH THE TOTAL NUMBER OF BOARDING PAGES **/
    @GET("fetchBoardingPages")
    Call<BoardingPages> fetchBoardingPages(@Query("cityID") String cityID);

    /** CHECK IF THE USER HAS ENABLED HOME BOARDING **/
    @GET("checkBoardingStatus")
    Call<Boarding> checkBoardingStatus(@Query("userID") String userID);

    /** REGISTER A USER FOR HOME BOARDING **/
    @POST("registerHomeBoarding")
    @FormUrlEncoded
    Call<Boarding> registerHomeBoarding(
            @Field("userID") String userID,
            @Field("boardingAddress") String boardingAddress,
            @Field("boardingPincode") String boardingPincode,
            @Field("cityID") String cityID,
            @Field("stateID") String stateID,
            @Field("countryID") String countryID,
            @Field("boardingLatitude") String boardingLatitude,
            @Field("boardingLongitude") String boardingLongitude,
            @Field("boardingExperience") String boardingExperience,
            @Field("boardingSince") String boardingSince,
            @Field("boardingPrice") String boardingPrice,
            @Field("boardingDate") String boardingDate,
            @Field("boardingActive") String boardingActive);

    /** FETCH THE HOME BOARDING DETAILS WITH THE USER'S ID **/
    @GET("fetchBoardingDetailsByUser")
    Call<Boarding> fetchBoardingDetailsByUser(@Query("userID") String userID);
}