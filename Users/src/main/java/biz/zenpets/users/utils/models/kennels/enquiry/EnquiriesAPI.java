package biz.zenpets.users.utils.models.kennels.enquiry;

import biz.zenpets.users.utils.models.trainers.enquiry.TrainingEnquiry;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EnquiriesAPI {

    /** CHECK IF THE MASTER KENNEL ENQUIRY RECORD HAS BEEN CREATED **/
    @GET("checkMasterKennelEnquiry")
    Call<TrainingEnquiry> checkMasterKennelEnquiry(
            @Query("kennelID") String kennelID,
            @Query("userID") String userID);

    /** CREATE A MASTER KENNEL ENQUIRY RECORD **/
    @POST("createMasterKennelEnquiryRecord")
    @FormUrlEncoded
    Call<TrainingEnquiry> createMasterKennelEnquiryRecord(
            @Field("kennelID") String kennelID,
            @Field("userID") String userID);
}