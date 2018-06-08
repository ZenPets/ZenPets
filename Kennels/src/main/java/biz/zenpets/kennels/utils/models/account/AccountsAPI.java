package biz.zenpets.kennels.utils.models.account;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AccountsAPI {

    /** CREATE A NEW TRAINER'S ACCOUNT **/
    @POST("registerKennelOwner")
    @FormUrlEncoded
    Call<Account> registerKennelOwner(
            @Field("kennelOwnerAuthID") String kennelOwnerAuthID,
            @Field("kennelOwnerName") String kennelOwnerName,
            @Field("kennelOwnerDisplayProfile") String kennelOwnerDisplayProfile,
            @Field("kennelOwnerEmail") String kennelOwnerEmail,
            @Field("kennelOwnerPhonePrefix") String kennelOwnerPhonePrefix,
            @Field("kennelOwnerPhoneNumber") String kennelOwnerPhoneNumber,
            @Field("kennelOwnerAddress") String kennelOwnerAddress,
            @Field("kennelOwnerPinCode") String kennelOwnerPinCode,
            @Field("countryID") String countryID,
            @Field("stateID") String stateID,
            @Field("cityID") String cityID);

    /** CHECK IF A KENNEL OWNER'S ACCOUNT EXISTS **/
    @GET("kennelOwnerExists")
    Call<Account> kennelOwnerExists(@Query("kennelOwnerEmail") String kennelOwnerEmail);

    /** FETCH THE LOGGED IN KENNEL OWNER'S ID **/
    @GET("fetchKennelOwnerID")
    Call<Account> fetchKennelOwnerID(@Query("kennelOwnerAuthID") String kennelOwnerAuthID);

    /** FETCH KENNEL OWNER'S PROFILE USING THEIR AUTH ID **/
    @GET("fetchKennelOwnerProfileAuthID")
    Call<Account> fetchKennelOwnerProfileAuthID(@Query("kennelOwnerAuthID") String kennelOwnerAuthID);

    /** CREATE A NEW TRAINER'S ACCOUNT **/
    @POST("updateKennelOwnerToken")
    @FormUrlEncoded
    Call<Account> updateKennelOwnerToken(
            @Field("kennelOwnerID") String kennelOwnerID,
            @Field("kennelOwnerToken") String kennelOwnerToken);
}