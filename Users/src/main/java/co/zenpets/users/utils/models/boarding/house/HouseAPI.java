package co.zenpets.users.utils.models.boarding.house;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HouseAPI {

    /** ADD THE HOME BOARDING HOUSE DETAILS **/
    @POST("addBoardingHouseDetails")
    @FormUrlEncoded
    Call<House> addBoardingHouseDetails(
            @Field("boardingID") String boardingID,
            @Field("boardingUnitID") String boardingUnitID,
            @Field("detailsDog") String detailsDog,
            @Field("detailsCat") String detailsCat,
            @Field("detailsSmoking") String detailsSmoking,
            @Field("detailsVaping") String detailsVaping);

    /** FETCH THE HOME BOARDING DETAILS WITH THE USER'S ID **/
    @GET("fetchBoardingHouseDetails")
    Call<House> fetchBoardingHouseDetails(@Query("userID") String userID);

    /** FETCH THE LIST OF BOARDING UNITS **/
    @GET("fetchBoardingUnits") Call<Units> fetchBoardingUnits();
}