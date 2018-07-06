package biz.zenpets.users.utils.models.adoptions.notifications;

import biz.zenpets.users.utils.models.adoptions.messages.AdoptionMessages;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AdoptionNotificationAPI {

    /** FETCH USER'S PROFILE **/
    @GET("fetchAdoptionParticipants")
    Call<AdoptionMessages> fetchAdoptionParticipants(
            @Query("adoptionID") String adoptionID,
            @Query("userID") String userID);

    /** SEND A NOTIFICATION TO THE USERS PARTICIPATING ON AN ADOPTION LISTING **/
    @POST("sendAdoptionReplyNotification")
    @FormUrlEncoded
    Call<AdoptionNotification> sendAdoptionReplyNotification(
            @Field("deviceToken") String deviceToken,
            @Field("notificationTitle") String notificationTitle,
            @Field("notificationMessage") String notificationMessage,
            @Field("notificationReference") String notificationReference,
            @Field("adoptionID") String adoptionID,
            @Field("userID") String userID,
            @Field("userName") String userName,
            @Field("userDisplayProfile") String userDisplayProfile);
}