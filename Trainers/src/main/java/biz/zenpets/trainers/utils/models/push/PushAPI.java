package biz.zenpets.trainers.utils.models.push;

import biz.zenpets.trainers.utils.models.trainers.Trainer;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PushAPI {

    /** CREATE A NEW TRAINER'S ACCOUNT **/
    @POST("trainerRegistration")
    @FormUrlEncoded
    Call<Trainer> trainerRegistration(
            @Field("deviceToken") String deviceToken,
            @Field("notificationTitle") String notificationTitle,
            @Field("notificationMessage") String notificationMessage,
            @Field("notificationReference") String notificationReference,
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
}