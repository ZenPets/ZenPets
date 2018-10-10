package co.zenpets.users.utils.models.kennels.notifications;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NotificationsAPI {

    /** SEND A NOTIFICATION TO THE KENNEL / USER OF A NEW MESSAGE ON AN ENQUIRY **/
    @POST("sendKennelReplyNotification")
    @FormUrlEncoded
    Call<Notification> sendKennelReplyNotification(
            @Field("deviceToken") String deviceToken,
            @Field("notificationTitle") String notificationTitle,
            @Field("notificationMessage") String notificationMessage,
            @Field("notificationReference") String notificationReference,
            @Field("kennelEnquiryID") String kennelEnquiryID,
            @Field("kennelID") String kennelID,
            @Field("userName") String userName,
            @Field("userDisplayProfile") String userDisplayProfile);
}