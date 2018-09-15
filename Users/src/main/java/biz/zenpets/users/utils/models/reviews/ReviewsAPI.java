package biz.zenpets.users.utils.models.reviews;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReviewsAPI {

    /** POST A NEW DOCTOR REVIEW **/
    @POST("newDoctorReview")
    @FormUrlEncoded
    Call<Review> newDoctorReview(
            @Field("doctorID") String doctorID,
            @Field("userID") String userID,
            @Field("visitReasonID") String visitReasonID,
            @Field("recommendStatus") String recommendStatus,
            @Field("appointmentStatus") String appointmentStatus,
            @Field("doctorExperience") String doctorExperience,
            @Field("reviewTimestamp") String reviewTimestamp);

    /** FETCH THE DOCTOR'S REVIEWS **/
    @GET("fetchDoctorReviews")
    Call<Reviews> fetchDoctorReviews(@Query("doctorID") String doctorID);

    /** FETCH THE DOCTOR'S REVIEWS COUNT **/
    @GET("fetchDoctorReviewCount")
    Call<ReviewCount> fetchDoctorReviewCount(@Query("doctorID") String doctorID);

    /** FETCH A SUBSET OF THE DOCTOR'S REVIEWS **/
    @GET("fetchDoctorReviewsSubset")
    Call<Reviews> fetchDoctorReviewsSubset(@Query("doctorID") String doctorID);

    /** FETCH THE DOCTOR'S POSITIVE REVIEWS **/
    @GET("fetchPositiveReviews")
    Call<Reviews> fetchPositiveReviews(
            @Query("doctorID") String doctorID,
            @Query("recommendStatus") String recommendStatus);

    /** FETCH THE DOCTOR'S NEGATIVE REVIEWS **/
    @GET("fetchNegativeReviews")
    Call<Reviews> fetchNegativeReviews(
            @Query("doctorID") String doctorID,
            @Query("recommendStatus") String recommendStatus);

    /** FETCH THE REVIEW DETAILS**/
    @GET("fetchDoctorReviewDetails")
    Call<Review> fetchDoctorReviewDetails(
            @Query("reviewID") String reviewID,
            @Query("clinicID") String clinicID);

    /** CHECK IF THE USER HAS POSTED A REVIEW FOR A DOCTOR **/
    @GET("checkUserDoctorReview")
    Call<Review> checkUserDoctorReview(
            @Query("userID") String userID,
            @Query("doctorID") String doctorID);
}