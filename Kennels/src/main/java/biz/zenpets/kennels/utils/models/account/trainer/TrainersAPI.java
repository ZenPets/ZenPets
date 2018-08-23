package biz.zenpets.kennels.utils.models.account.trainer;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TrainersAPI {

    /** CHECK IF THE TRAINER ACCOUNT EXISTS **/
    @GET("trainerExists")
    Call<Trainer> trainerExists(@Query("trainerEmail") String trainerEmail);

    /** CREATE A NEW TRAINER'S ACCOUNT **/
    @POST("trainerRegistration")
    @FormUrlEncoded
    Call<Trainer> trainerRegistration(
            @Field("trainerAuthID") String trainerAuthID,
            @Field("trainerName") String trainerName,
            @Field("trainerEmail") String trainerEmail,
            @Field("trainerPhonePrefix") String trainerPhonePrefix,
            @Field("trainerPhoneNumber") String trainerPhoneNumber,
            @Field("trainerAddress") String trainerAddress,
            @Field("trainerPincode") String trainerPincode,
            @Field("trainerLandmark") String trainerLandmark,
            @Field("trainerLatitude") String trainerLatitude,
            @Field("trainerLongitude") String trainerLongitude,
            @Field("countryID") String countryID,
            @Field("stateID") String stateID,
            @Field("cityID") String cityID,
            @Field("trainerGender") String trainerGender,
            @Field("trainerProfile") String trainerProfile,
            @Field("trainerDisplayProfile") String trainerDisplayProfile);

    /** FETCH THE LOGGED IN TRAINER'S ID **/
    @GET("fetchTrainerID")
    Call<Trainer> fetchTrainerID(@Query("trainerAuthID") String trainerAuthID);

    /** FETCH THE LOGGED IN TRAINER'S PROFILE **/
    @GET("fetchTrainerProfile")
    Call<Trainer> fetchTrainerProfile(@Query("trainerID") String trainerID);

    /** UPDATE THE TRAINER'S DEVICE TOKEN **/
    @POST("updateTrainerToken")
    @FormUrlEncoded
    Call<Trainer> updateTrainerToken(
            @Field("trainerID") String trainerID,
            @Field("trainerToken") String trainerToken);
}