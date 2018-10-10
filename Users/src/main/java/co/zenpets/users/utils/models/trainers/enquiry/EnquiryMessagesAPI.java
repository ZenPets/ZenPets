package co.zenpets.users.utils.models.trainers.enquiry;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EnquiryMessagesAPI {

    /** FETCH ALL TRAINING ENQUIRY MESSAGES **/
    @GET("fetchEnquiryMessages")
    Call<EnquiryMessages> fetchEnquiryMessages(@Query("trainingMasterID") String trainingMasterID);

    /** POST A NEW TRAINING ENQUIRY MESSAGE (USER) **/
    @POST("newEnquiryUserMessage")
    @FormUrlEncoded
    Call<EnquiryMessage> newEnquiryUserMessage(
            @Field("trainingMasterID") String trainingMasterID,
            @Field("trainerID") String trainerID,
            @Field("userID") String userID,
            @Field("trainingSlaveMessage") String trainingSlaveMessage,
            @Field("trainerSlaveTimestamp") String trainerSlaveTimestamp);
}