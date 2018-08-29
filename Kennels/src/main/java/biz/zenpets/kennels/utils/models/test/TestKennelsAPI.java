package biz.zenpets.kennels.utils.models.test;

import biz.zenpets.kennels.utils.models.kennels.KennelPages;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TestKennelsAPI {

    /** REGISTER A NEW KENNEL **/
    @POST("registerNewTestKennel")
    @FormUrlEncoded
    Call<TestKennel> registerNewTestKennel(
            @Field("kennelOwnerID") String kennelOwnerID,
            @Field("kennelName") String kennelName,
            @Field("kennelCoverPhoto") String kennelCoverPhoto,
            @Field("kennelAddress") String kennelAddress,
            @Field("kennelPinCode") String kennelPinCode,
            @Field("countryID") String countryID,
            @Field("stateID") String stateID,
            @Field("cityID") String cityID,
            @Field("kennelLatitude") String kennelLatitude,
            @Field("kennelLongitude") String kennelLongitude,
            @Field("kennelPhonePrefix1") String kennelPhonePrefix1,
            @Field("kennelPhoneNumber1") String kennelPhoneNumber1,
            @Field("kennelPhonePrefix2") String kennelPhonePrefix2,
            @Field("kennelPhoneNumber2") String kennelPhoneNumber2,
            @Field("kennelSmallCapacity") String kennelSmallCapacity,
            @Field("kennelMediumCapacity") String kennelMediumCapacity,
            @Field("kennelLargeCapacity") String kennelLargeCapacity,
            @Field("kennelXLargeCapacity") String kennelXLargeCapacity,
            @Field("kennelVerified") String kennelVerified);

    /** UPDATE A KENNEL'S LISTING **/
    @POST("updateTestKennel")
    @FormUrlEncoded
    Call<TestKennel> updateTestKennel(
            @Field("kennelID") String kennelID,
            @Field("kennelName") String kennelName,
            @Field("kennelCoverPhoto") String kennelCoverPhoto,
            @Field("kennelAddress") String kennelAddress,
            @Field("kennelPinCode") String kennelPinCode,
            @Field("countryID") String countryID,
            @Field("stateID") String stateID,
            @Field("cityID") String cityID,
            @Field("kennelLatitude") String kennelLatitude,
            @Field("kennelLongitude") String kennelLongitude,
            @Field("kennelPhonePrefix1") String kennelPhonePrefix1,
            @Field("kennelPhoneNumber1") String kennelPhoneNumber1,
            @Field("kennelPhonePrefix2") String kennelPhonePrefix2,
            @Field("kennelPhoneNumber2") String kennelPhoneNumber2,
            @Field("kennelPetCapacity") String kennelPetCapacity);

    /** UPDATE A KENNEL'S COVER PHOTO **/
    @POST("updateTestKennelCoverPhoto")
    @FormUrlEncoded
    Call<TestKennel> updateTestKennelCoverPhoto(
            @Field("kennelID") String kennelID,
            @Field("kennelCoverPhoto") String kennelCoverPhoto);

    /** FETCH A LIST OF KENNELS (KENNEL MANAGER) **/
    @GET("fetchTestKennelsListByOwner")
    Call<TestKennels> fetchTestKennelsListByOwner(@Query("kennelOwnerID") String kennelOwnerID);

    /** DELETE A KENNEL RECORD **/
    @POST("deleteTestKennelRecord")
    @FormUrlEncoded
    Call<TestKennel> deleteTestKennelRecord(@Field("kennelID") String kennelID);

    /** FETCH THE KENNEL'S DETAILS **/
    @GET("fetchTestKennelDetails")
    Call<TestKennel> fetchTestKennelDetails(@Query("kennelID") String kennelID);

    /** FETCH THE TOTAL NUMBER OF KENNEL LISTINGS BY A KENNEL OWNER **/
    @GET("fetchOwnerTestKennels")
    Call<KennelPages> fetchOwnerTestKennels(@Query("kennelOwnerID") String kennelOwnerID);
}