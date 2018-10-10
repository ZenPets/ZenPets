package co.zenpets.users.utils.models.kennels.enquiry;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EnquiryMessagesAPI {

    /** FETCH ALL KENNEL ENQUIRY MESSAGES **/
    @GET("fetchKennelEnquiryMessages")
    Call<EnquiryMessages> fetchKennelEnquiryMessages(@Query("kennelEnquiryID") String kennelEnquiryID);

    /** POST A NEW KENNEL ENQUIRY MESSAGE (USER) **/
    @POST("newKennelEnquiryUserMessage")
    @FormUrlEncoded
    Call<EnquiryMessage> newKennelEnquiryUserMessage(
            @Field("kennelEnquiryID") String kennelEnquiryID,
            @Field("kennelID") String kennelID,
            @Field("userID") String userID,
            @Field("kennelEnquiryMessage") String kennelEnquiryMessage,
            @Field("kennelEnquiryTimestamp") String kennelEnquiryTimestamp);
}