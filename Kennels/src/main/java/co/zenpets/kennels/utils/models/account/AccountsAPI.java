package co.zenpets.kennels.utils.models.account;

public interface AccountsAPI {

//    /** CHECK IF THE TRAINER ACCOUNT EXISTS **/
//    @GET("trainerExists")
//    Call<Trainer> trainerExists(@Query("trainerEmail") String trainerEmail);
//
//    /** CREATE A NEW TRAINER'S ACCOUNT **/
//    @POST("registerKennelOwner")
//    @FormUrlEncoded
//    Call<Account> registerKennelOwner(
//            @Field("kennelOwnerAuthID") String kennelOwnerAuthID,
//            @Field("kennelOwnerName") String kennelOwnerName,
//            @Field("kennelOwnerDisplayProfile") String kennelOwnerDisplayProfile,
//            @Field("kennelOwnerEmail") String kennelOwnerEmail,
//            @Field("kennelOwnerPhonePrefix") String kennelOwnerPhonePrefix,
//            @Field("kennelOwnerPhoneNumber") String kennelOwnerPhoneNumber,
//            @Field("kennelOwnerAddress") String kennelOwnerAddress,
//            @Field("kennelOwnerPinCode") String kennelOwnerPinCode,
//            @Field("countryID") String countryID,
//            @Field("stateID") String stateID,
//            @Field("cityID") String cityID,
//            @Field("paymentID") String paymentID,
//            @Field("validFrom") String validFrom,
//            @Field("validTo") String validTo);
//
//    /** FETCH THE LOGGED IN KENNEL OWNER'S ID **/
//    @GET("fetchKennelOwnerID")
//    Call<Account> fetchKennelOwnerID(@Query("kennelOwnerAuthID") String kennelOwnerAuthID);
//
//    /** FETCH THE KENNEL OWNER'S PROFILE **/
//    @GET("fetchKennelOwnerProfile")
//    Call<Account> fetchKennelOwnerProfile(@Query("kennelOwnerID") String kennelOwnerID);
//
//    /** FETCH KENNEL OWNER'S PROFILE USING THEIR AUTH ID **/
//    @GET("fetchKennelOwnerProfileAuthID")
//    Call<Account> fetchKennelOwnerProfileAuthID(@Query("kennelOwnerAuthID") String kennelOwnerAuthID);
//
//    /** CREATE A NEW TRAINER'S ACCOUNT **/
//    @POST("updateKennelOwnerToken")
//    @FormUrlEncoded
//    Call<Account> updateKennelOwnerToken(
//            @Field("kennelOwnerID") String kennelOwnerID,
//            @Field("kennelOwnerToken") String kennelOwnerToken);
}