package co.zenpets.kennels.utils.models.kennels;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface KennelsAPI {

    /** REGISTER A NEW KENNEL **/
    @POST("registerNewKennel")
    @FormUrlEncoded
    Call<Kennel> registerNewKennel(
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
            @Field("kennelPetCapacity") String kennelPetCapacity);

//    /** REGISTER A NEW KENNEL **/
//    @POST("registerNewKennel")
//    @FormUrlEncoded
//    Call<Kennel> registerNewKennel(
//            @Field("kennelOwnerID") String kennelOwnerID,
//            @Field("kennelChargesID") String kennelChargesID,
//            @Field("kennelName") String kennelName,
//            @Field("kennelCoverPhoto") String kennelCoverPhoto,
//            @Field("kennelAddress") String kennelAddress,
//            @Field("kennelPinCode") String kennelPinCode,
//            @Field("countryID") String countryID,
//            @Field("stateID") String stateID,
//            @Field("cityID") String cityID,
//            @Field("kennelLatitude") String kennelLatitude,
//            @Field("kennelLongitude") String kennelLongitude,
//            @Field("kennelPhonePrefix1") String kennelPhonePrefix1,
//            @Field("kennelPhoneNumber1") String kennelPhoneNumber1,
//            @Field("kennelPhonePrefix2") String kennelPhonePrefix2,
//            @Field("kennelPhoneNumber2") String kennelPhoneNumber2,
//            @Field("kennelPetCapacity") String kennelPetCapacity,
//            @Field("kennelValidFrom") String kennelValidFrom,
//            @Field("kennelValidTo") String kennelValidTo);

    /** UPDATE A KENNEL'S LISTING **/
    @POST("updateKennel")
    @FormUrlEncoded
    Call<Kennel> updateKennel(
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
    @POST("updateKennelCoverPhoto")
    @FormUrlEncoded
    Call<Kennel> updateKennelCoverPhoto(
            @Field("kennelID") String kennelID,
            @Field("kennelCoverPhoto") String kennelCoverPhoto);

    /** UPDATE A KENNEL'S PAYMENT **/
    @POST("updateKennelPayment")
    @FormUrlEncoded
    Call<Kennel> updateKennelPayment(
            @Field("kennelID") String kennelID,
            @Field("paymentID") String paymentID);

    /** FETCH A LIST OF KENNELS (KENNEL MANAGER) **/
    @GET("fetchKennelsListByOwner")
    Call<Kennels> fetchKennelsListByOwner(@Query("kennelOwnerID") String kennelOwnerID);

    /** DELETE A KENNEL RECORD **/
    @POST("deleteKennelRecord")
    @FormUrlEncoded
    Call<Kennel> deleteKennelRecord(@Field("kennelID") String kennelID);

    /** FETCH THE KENNEL RECORD DETAILS **/
    @GET("fetchKennelDetails")
    Call<Kennel> fetchKennelDetails(@Query("kennelID") String kennelID);

    /** FETCH THE KENNEL RECORD DETAILS **/
    @GET("fetchOwnerKennels")
    Call<KennelPages> fetchOwnerKennels(@Query("kennelOwnerID") String kennelOwnerID);
}