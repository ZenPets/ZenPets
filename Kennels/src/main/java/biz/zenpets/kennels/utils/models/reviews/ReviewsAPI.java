package biz.zenpets.kennels.utils.models.reviews;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReviewsAPI {

    /** FETCH ALL REVIEWS OF A KENNEL **/
    @GET("fetchKennelReviewCount")
    Call<ReviewCount> fetchKennelReviewCount(@Query("kennelID") String kennelID);

    /** FETCH A SUBSET OF A KENNEL'S REVIEWS **/
    @GET("fetchKennelReviewsSubset")
    Call<Reviews> fetchKennelReviewsSubset(@Query("kennelID") String kennelID);

    /** FETCH ALL REVIEWS OF A KENNEL **/
    @GET("fetchKennelReviews")
    Call<Reviews> fetchKennelReviews(@Query("kennelID") String kennelID);

    /** UPDATE A KENNEL OWNER'S REPLY **/
    @POST("updateKennelReviewReply")
    @FormUrlEncoded
    Call<Review> updateKennelReviewReply(
            @Field("kennelReviewID") String kennelReviewID,
            @Field("kennelReplyText") String kennelReplyText,
            @Field("kennelReplyTimestamp") String kennelReplyTimestamp);
}