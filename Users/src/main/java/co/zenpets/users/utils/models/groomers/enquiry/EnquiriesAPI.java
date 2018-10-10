package co.zenpets.users.utils.models.groomers.enquiry;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface EnquiriesAPI {

    /** CHECK IF THE MASTER GROOMER ENQUIRY RECORD HAS BEEN CREATED **/
    @GET("checkMasterGroomerEnquiry")
    Call<Enquiry> checkMasterGroomerEnquiry(
            @Query("groomerID") String groomerID,
            @Query("userID") String userID);

    /** CREATE A MASTER GROOMER ENQUIRY RECORD **/
    @POST("createMasterGroomerEnquiryRecord")
    @FormUrlEncoded
    Call<Enquiry> createMasterGroomerEnquiryRecord(
            @Field("groomerID") String groomerID,
            @Field("userID") String userID);
}