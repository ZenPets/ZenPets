package co.zenpets.users.utils.models.trainers.enquiry;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TrainingEnquiryAPI {

    /** CHECK IF THE MASTER TRAINING ENQUIRY RECORD HAS BEEN CREATED **/
    @GET("checkMasterTrainingEnquiry")
    Call<TrainingEnquiry> checkMasterTrainingEnquiry(
            @Query("trainerModuleID") String trainerModuleID,
            @Query("userID") String userID);

    /** CREATE A MASTER TRAINING ENQUIRY RECORD **/
    @POST("createMasterTrainingEnquiryRecord")
    @FormUrlEncoded
    Call<TrainingEnquiry> createMasterTrainingEnquiryRecord(
            @Field("trainerModuleID") String trainerModuleID,
            @Field("userID") String userID);
}