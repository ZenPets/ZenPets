package biz.zenpets.kennels.utils.models.enquiries;

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

    /** POST A NEW TRAINING ENQUIRY MESSAGE (USER) **/
    @POST("newKennelEnquiryOwnerMessage")
    @FormUrlEncoded
    Call<EnquiryMessage> newKennelEnquiryOwnerMessage(
            @Field("kennelEnquiryID") String kennelEnquiryID,
            @Field("kennelID") String kennelID,
            @Field("userID") String userID,
            @Field("kennelEnquiryMessage") String kennelEnquiryMessage,
            @Field("kennelEnquiryTimestamp") String kennelEnquiryTimestamp);
}