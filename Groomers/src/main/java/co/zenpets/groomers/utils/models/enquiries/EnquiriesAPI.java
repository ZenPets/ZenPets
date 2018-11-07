package co.zenpets.groomers.utils.models.enquiries;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EnquiriesAPI {

    /** FETCH ALL GROOMER ENQUIRIES **/
    @GET("fetchGroomerEnquiries")
    Call<Enquiries> fetchGroomerEnquiries(@Query("groomerID") String groomerID);

    /** GET NUMBER OF USER MESSAGES IN A GROOMER ENQUIRY THREAD **/
    @GET("fetchGroomerEnquiryUserMessagesCount")
    Call<MessagesCount> fetchGroomerEnquiryUserMessagesCount(
            @Query("enquiryID") String enquiryID,
            @Query("userID") String userID);

    /** FETCH THE LAST GROOMER ENQUIRY MESSAGE DETAILS **/
    @GET("fetchLastGroomerEnquiryDetails")
    Call<Enquiry> fetchLastGroomerEnquiryDetails(
            @Query("enquiryID") String enquiryID,
            @Query("userID") String userID);

    /** GET NUMBER OF UNREAD MESSAGES IN AN ENQUIRY  **/
    @GET("fetchGroomerEnquiryUnreadCount")
    Call<UnreadCount> fetchGroomerEnquiryUnreadCount(
            @Query("enquiryID") String enquiryID,
            @Query("userID") String userID);
}