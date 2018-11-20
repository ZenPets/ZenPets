package co.zenpets.kennels.utils.models.enquiries;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EnquiriesAPI {

    /** FETCH THE KENNEL'S ENQUIRIES FOR THE DASHBOARD **/
    @GET("fetchKennelDashboardEnquiries")
    Call<Enquiries> fetchKennelDashboardEnquiries(@Query("kennelID") String kennelID);

    /** FETCH ALL KENNEL ENQUIRIES **/
    @GET("fetchKennelEnquiries")
    Call<Enquiries> fetchKennelEnquiries(@Query("kennelID") String kennelID);

    /** GET NUMBER OF USER MESSAGES ON A KENNEL **/
    @GET("fetchKennelEnquiryUserMessagesCount")
    Call<MessagesCount> fetchKennelEnquiryUserMessagesCount(
            @Query("kennelEnquiryID") String kennelEnquiryID,
            @Query("userID") String userID);

    /** FETCH THE LAST KENNEL ENQUIRY MESSAGE DETAILS **/
    @GET("fetchLastKennelEnquiryDetails")
    Call<Enquiry> fetchLastKennelEnquiryDetails(
            @Query("kennelEnquiryID") String kennelEnquiryID,
            @Query("userID") String userID);

    /** GET NUMBER OF UNREAD MESSAGES IN AN ENQUIRY  **/
    @GET("fetchKennelEnquiryUnreadCount")
    Call<UnreadCount> fetchKennelEnquiryUnreadCount(
            @Query("kennelEnquiryID") String kennelEnquiryID,
            @Query("userID") String userID);
}