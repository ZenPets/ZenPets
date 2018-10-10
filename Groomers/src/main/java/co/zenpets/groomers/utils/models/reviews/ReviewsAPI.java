package co.zenpets.groomers.utils.models.reviews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReviewsAPI {

    /** FETCH ALL REVIEWS OF A KENNEL **/
    @GET("fetchGroomerReviewCount")
    Call<ReviewCount> fetchGroomerReviewCount(@Query("groomerID") String groomerID);

    /** FETCH A SUBSET OF A KENNEL'S REVIEWS **/
    @GET("fetchGroomerReviewsSubset")
    Call<Reviews> fetchGroomerReviewsSubset(@Query("groomerID") String groomerID);

    /** FETCH ALL REVIEWS OF A KENNEL **/
    @GET("fetchGroomerReviews")
    Call<Reviews> fetchGroomerReviews(@Query("groomerID") String groomerID);

//    /** POST A REPLY ON A REVIEW **/
//    @POST("postKennelReviewReply")
//    @FormUrlEncoded
//    Call<Review> postKennelReviewReply(
//            @Field("kennelReviewID") String kennelReviewID,
//            @Field("kennelReplyStatus") String kennelReplyStatus,
//            @Field("kennelReplyText") String kennelReplyText,
//            @Field("kennelReplyPublished") String kennelReplyPublished);
//
//    /** UPDATE A KENNEL OWNER'S REPLY **/
//    @POST("updateKennelReviewReply")
//    @FormUrlEncoded
//    Call<Review> updateKennelReviewReply(
//            @Field("kennelReviewID") String kennelReviewID,
//            @Field("kennelReplyText") String kennelReplyText,
//            @Field("kennelReplyUpdated") String kennelReplyUpdated);
}