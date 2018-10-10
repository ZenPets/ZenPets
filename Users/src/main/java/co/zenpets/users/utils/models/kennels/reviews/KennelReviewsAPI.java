package co.zenpets.users.utils.models.kennels.reviews;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface KennelReviewsAPI {

    /** POST A NEW REVIEW FOR A KENNEL **/
    @POST("newKennelReview")
    @FormUrlEncoded
    Call<KennelReview> newKennelReview(
            @Field("kennelID") String kennelID,
            @Field("userID") String userID,
            @Field("kennelRating") String kennelRating,
            @Field("kennelRecommendStatus") String kennelRecommendStatus,
            @Field("kennelExperience") String kennelExperience,
            @Field("kennelReviewTimestamp") String kennelReviewTimestamp,
            @Field("kennelReviewDate") String kennelReviewDate);

    /** FETCH ALL REVIEWS OF A KENNEL **/
    @GET("fetchKennelReviews")
    Call<KennelReviews> fetchKennelReviews(@Query("kennelID") String kennelID);

    /** FETCH A SUBSET OF A KENNEL'S REVIEWS **/
    @GET("fetchKennelReviewsSubset")
    Call<KennelReviews> fetchKennelReviewsSubset(@Query("kennelID") String kennelID);

    /** FETCH THE COUNT OF ALL REVIEWS OF A KENNEL **/
    @GET("fetchKennelReviewCount")
    Call<KennelReviewsCount> fetchKennelReviewCount(@Query("kennelID") String kennelID);

    /** FETCH ALL POSITIVE REVIEWS OF A KENNEL **/
    @GET("fetchPositiveKennelReviews")
    Call<KennelReviews> fetchPositiveKennelReviews(@Query("kennelID") String kennelID);

    /** FETCH ALL NEGATIVE REVIEWS OF A KENNEL **/
    @GET("fetchNegativeKennelReviews")
    Call<KennelReviews> fetchNegativeKennelReviews(@Query("kennelID") String kennelID);

    /** FETCH THE DETAILS OF A REVIEW LEFT FOR A KENNEL **/
    @GET("fetchKennelReviewDetails")
    Call<KennelReview> fetchKennelReviewDetails(@Query("kennelReviewID") String kennelReviewID);

    /** CHECK IF THE USER HAS POSTED A REVIEW FOR A KENNEL **/
    @GET("checkUserKennelReview")
    Call<KennelReview> checkUserKennelReview(
            @Query("userID") String userID,
            @Query("kennelID") String kennelID);

    /** FETCH THE KENNEL'S AVERAGE RATINGS **/
    @GET("fetchKennelRatings")
    Call<KennelRating> fetchKennelRatings(@Query("kennelID") String kennelID);

    /** UPDATE A KENNEL REVIEW **/
    @POST("updateKennelReview")
    @FormUrlEncoded
    Call<KennelReview> updateKennelReview(
            @Field("kennelReviewID") String kennelReviewID,
            @Field("kennelRating") String kennelRating,
            @Field("kennelRecommendStatus") String kennelRecommendStatus,
            @Field("kennelExperience") String kennelExperience,
            @Field("kennelReviewTimestamp") String kennelReviewTimestamp,
            @Field("kennelReviewDate") String kennelReviewDate);
}