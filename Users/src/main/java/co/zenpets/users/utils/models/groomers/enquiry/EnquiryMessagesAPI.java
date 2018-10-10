package co.zenpets.users.utils.models.groomers.enquiry;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EnquiryMessagesAPI {

    /** FETCH ALL GROOMER ENQUIRY MESSAGES **/
    @GET("fetchGroomerEnquiryMessages")
    Call<EnquiryMessages> fetchGroomerEnquiryMessages(@Query("enquiryID") String enquiryID);

    /** POST A NEW GROOMER ENQUIRY MESSAGE (USER) **/
    @POST("newGroomerEnquiryUserMessage")
    @FormUrlEncoded
    Call<EnquiryMessage> newGroomerEnquiryUserMessage(
            @Field("enquiryID") String enquiryID,
            @Field("groomerID") String groomerID,
            @Field("userID") String userID,
            @Field("enquiryMessage") String enquiryMessage,
            @Field("enquiryTimestamp") String enquiryTimestamp);
}