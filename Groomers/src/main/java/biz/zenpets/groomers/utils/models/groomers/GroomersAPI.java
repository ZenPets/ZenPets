package biz.zenpets.groomers.utils.models.groomers;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GroomersAPI {

    /** FETCH THE LOGGED IN GROOMER ACCOUNT ID **/
    @GET("fetchGroomerID")
    Call<Groomer> fetchGroomerID(@Query("groomerAuthID") String groomerAuthID);

    /** CHECK IF THE GROOMER ACCOUNT EXISTS **/
    @GET("groomerExists")
    Call<Groomer> groomerExists(@Query("contactEmail") String contactEmail);

    /** REGISTER A NEW GROOMER ACCOUNT **/
    @POST("registerGroomerAccount")
    @FormUrlEncoded
    Call<Groomer> registerGroomerAccount(
            @Field("groomerAuthID") String groomerAuthID,
            @Field("groomerName") String groomerName,
            @Field("groomerLogo") String groomerLogo,
            @Field("contactName") String contactName,
            @Field("contactEmail") String contactEmail,
            @Field("contactPhonePrefix1") String contactPhonePrefix1,
            @Field("contactPhoneNumber1") String contactPhoneNumber1,
            @Field("contactPhonePrefix2") String contactPhonePrefix2,
            @Field("contactPhoneNumber2") String contactPhoneNumber2,
            @Field("groomerAddress") String groomerAddress,
            @Field("groomerPincode") String groomerPincode,
            @Field("countryID") String countryID,
            @Field("stateID") String stateID,
            @Field("cityID") String cityID,
            @Field("groomerLatitude") String groomerLatitude,
            @Field("groomerLongitude") String groomerLongitude,
            @Field("paymentID") String paymentID,
            @Field("groomerValidFrom") String groomerValidFrom,
            @Field("groomerValidTo") String groomerValidTo,
            @Field("groomerVerified") String groomerVerified);

    /** FETCH THE GROOMER'S PROFILE **/
    @GET("fetchGroomerProfile")
    Call<Groomer> fetchGroomerProfile(@Query("groomerID") String groomerID);

    /** FETCH THE GROOMER DETAILS **/
    @GET("fetchGroomerDetails")
    Call<Groomer> fetchGroomerDetails(@Query("groomerID") String groomerID);

    /** UPDATE THE GROOMER ACCOUNT TOKEN **/
    @POST("updateGroomerToken")
    @FormUrlEncoded
    Call<Groomer> updateGroomerToken(
            @Field("groomerID") String groomerID,
            @Field("groomerToken") String groomerToken);
}