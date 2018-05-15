package biz.zenpets.trainers.utils.models.notifications;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NotificationsAPI {

    /** SEND A NOTIFICATION TO THE TRAINER / USER OF A NEW MESSAGE TO AN ENQUIRY **/
    @POST("sendEnquiryReplyNotification")
    @FormUrlEncoded
    Call<Notification> sendEnquiryReplyNotification(
            @Field("deviceToken") String deviceToken,
            @Field("notificationTitle") String notificationTitle,
            @Field("notificationMessage") String notificationMessage,
            @Field("notificationReference") String notificationReference,
            @Field("trainerID") String trainerID,
            @Field("moduleID") String moduleID,
            @Field("trainingMasterID") String trainingMasterID);
}