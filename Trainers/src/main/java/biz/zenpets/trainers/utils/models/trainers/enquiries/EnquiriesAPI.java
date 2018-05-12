package biz.zenpets.trainers.utils.models.trainers.enquiries;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EnquiriesAPI {

    /** FETCH ALL TRAINING ENQUIRIES **/
    @GET("fetchTrainingEnquiries")
    Call<Enquiries> fetchTrainingEnquiries(@Query("trainerID") String trainerID);

    /** GET NUMBER OF USER MESSAGES TO THE TRAINING MODULE **/
    @GET("fetchEnquiryUserMessagesCount")
    Call<MessagesCount> fetchEnquiryUserMessagesCount(
            @Query("trainingMasterID") String trainingMasterID,
            @Query("userID") String userID);

    /** FETCH THE LAST TRAINING ENQUIRY MESSAGE DETAILS **/
    @GET("fetchLastTrainingEnquiryDetails")
    Call<Enquiry> fetchLastTrainingEnquiryDetails(
            @Query("trainingMasterID") String trainingMasterID,
            @Query("userID") String userID);
}