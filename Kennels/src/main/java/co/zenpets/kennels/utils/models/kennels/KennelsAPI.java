package co.zenpets.kennels.utils.models.kennels;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface KennelsAPI {

    /** CHECK IF THE KENNEL ACCOUNT EXISTS **/
    @GET("kennelAccountExists")
    Call<Kennel> kennelAccountExists(@Query("kennelEmail") String kennelEmail);

    /** REGISTER A NEW KENNEL **/
    @POST("registerNewKennel")
    @FormUrlEncoded
    Call<Kennel> registerNewKennel(
            @Field("kennelAuthID") String kennelAuthID,
            @Field("kennelName") String kennelName,
            @Field("kennelEmail") String kennelEmail,
            @Field("kennelCoverPhoto") String kennelCoverPhoto,
            @Field("kennelContactName") String kennelContactName,
            @Field("kennelContactPhonePrefix") String kennelContactPhonePrefix,
            @Field("kennelContactPhoneNumber") String kennelContactPhoneNumber,
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
            @Field("kennelPetCapacity") String kennelPetCapacity,
            @Field("kennelValidFrom") String kennelValidFrom,
            @Field("kennelValidTo") String kennelValidTo,
            @Field("kennelVerified") String kennelVerified);

//    /** REGISTER A NEW KENNEL **/
//    @POST("registerNewKennel")
//    @FormUrlEncoded
//    Call<Kennel> registerNewKennel(
//            @Field("kennelName") String kennelName,
//            @Field("kennelEmail") String kennelEmail,
//            @Field("kennelCoverPhoto") String kennelCoverPhoto,
//            @Field("kennelContactName") String kennelContactName,
//            @Field("kennelContactPhonePrefix") String kennelContactPhonePrefix,
//            @Field("kennelContactPhoneNumber") String kennelContactPhoneNumber,
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
//            @Field("kennelValidTo") String kennelValidTo,
//            @Field("kennelPaymentID") String kennelPaymentID,
//            @Field("kennelVerified") String kennelVerified);

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

    /** FETCH THE KENNEL ID (FOR SAVING THE KENNEL ID IN THE APPLICATION) **/
    @GET("fetchKennelID")
    Call<Kennel> fetchKennelID(@Query("kennelAuthID") String kennelAuthID);

    /** CREATE A NEW TRAINER'S ACCOUNT **/
    @POST("updateKennelToken")
    @FormUrlEncoded
    Call<Kennel> updateKennelToken(
            @Field("kennelID") String kennelID,
            @Field("kennelToken") String kennelToken);
}