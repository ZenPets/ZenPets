package biz.zenpets.users.utils.models.groomers.review;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GroomerReviewsAPI {

    /** POST A NEW REVIEW FOR A GROOMER **/
    @POST("newGroomerReview")
    @FormUrlEncoded
    Call<GroomerReview> newGroomerReview(
            @Field("groomerID") String groomerID,
            @Field("userID") String userID,
            @Field("groomerRating") String groomerRating,
            @Field("groomerRecommendStatus") String groomerRecommendStatus,
            @Field("groomerExperience") String groomerExperience,
            @Field("groomerReviewTimestamp") String groomerReviewTimestamp,
            @Field("groomerReviewDate") String groomerReviewDate);

    /** FETCH ALL REVIEWS OF A GROOMER **/
    @GET("fetchGroomerReviews")
    Call<GroomerReviews> fetchGroomerReviews(@Query("groomerID") String groomerID);

    /** FETCH A SUBSET OF A GROOMER'S REVIEWS **/
    @GET("fetchGroomerReviewsSubset")
    Call<GroomerReviews> fetchGroomerReviewsSubset(@Query("groomerID") String groomerID);

    /** FETCH THE COUNT OF ALL REVIEWS OF A GROOMER **/
    @GET("fetchGroomerReviewCount")
    Call<GroomerReviewsCount> fetchGroomerReviewCount(@Query("groomerID") String groomerID);

    /** FETCH ALL POSITIVE REVIEWS OF A GROOMER **/
    @GET("fetchPositiveGroomerReviews")
    Call<GroomerReviews> fetchPositiveGroomerReviews(@Query("groomerID") String groomerID);

    /** FETCH ALL NEGATIVE REVIEWS OF A GROOMER **/
    @GET("fetchNegativeGroomerReviews")
    Call<GroomerReviews> fetchNegativeGroomerReviews(@Query("groomerID") String groomerID);

    /** FETCH THE DETAILS OF A REVIEW LEFT FOR A GROOMER **/
    @GET("fetchGroomerReviewDetails")
    Call<GroomerReview> fetchGroomerReviewDetails(@Query("reviewID") String reviewID);

    /** CHECK IF THE USER HAS POSTED A REVIEW FOR A GROOMER **/
    @GET("checkUserGroomerReview")
    Call<GroomerReview> checkUserGroomerReview(
            @Query("userID") String userID,
            @Query("groomerID") String groomerID);

    /** FETCH THE GROOMER'S AVERAGE RATINGS **/
    @GET("fetchGroomerRatings")
    Call<GroomerRating> fetchGroomerRatings(@Query("groomerID") String groomerID);

    /** UPDATE A GROOMER REVIEW **/
    @POST("updateGroomerReview")
    @FormUrlEncoded
    Call<GroomerReview> updateGroomerReview(
            @Field("reviewID") String reviewID,
            @Field("groomerRating") String groomerRating,
            @Field("groomerRecommendStatus") String groomerRecommendStatus,
            @Field("groomerExperience") String groomerExperience);
}