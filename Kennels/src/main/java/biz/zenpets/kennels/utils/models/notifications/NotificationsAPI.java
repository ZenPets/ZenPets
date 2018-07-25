package biz.zenpets.kennels.utils.models.notifications;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NotificationsAPI {

    /** SEND A NOTIFICATION TO THE TRAINER / USER OF A NEW MESSAGE TO AN ENQUIRY **/
    @POST("sendKennelReplyNotification")
    @FormUrlEncoded
    Call<Notification> sendKennelReplyNotification(
            @Field("deviceToken") String deviceToken,
            @Field("notificationTitle") String notificationTitle,
            @Field("notificationMessage") String notificationMessage,
            @Field("notificationReference") String notificationReference,
            @Field("kennelEnquiryID") String kennelEnquiryID,
            @Field("kennelID") String kennelID,
            @Field("kennelName") String kennelName,
            @Field("kennelCoverPhoto") String kennelCoverPhoto,
            @Field("kennelOwnerID") String kennelOwnerID);
}