package biz.zenpets.users.utils.models.reviews.votes;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ReviewVotesAPI {

    /** POST A NEW REVIEW VOTE **/
    @POST("newReviewVote")
    @FormUrlEncoded
    Call<ReviewVote> newReviewVote(
            @Field("reviewID") String reviewID,
            @Field("userID") String userID,
            @Field("reviewVoteText") String reviewVoteText,
            @Field("reviewVoteTimestamp") String reviewVoteTimestamp);

    /** FETCH THE POSITIVE VOTES ON A REVIEW **/
    @GET("fetchNegativeReviewVotes")
    Call<ReviewVotes> fetchNegativeReviewVotes(
            @Query("reviewID") String reviewID,
            @Query("reviewVoteText") String reviewVoteText);

    /** FETCH THE POSITIVE VOTES ON A REVIEW **/
    @GET("fetchPositiveReviewVotes")
    Call<ReviewVotes> fetchPositiveReviewVotes(
            @Query("reviewID") String reviewID,
            @Query("reviewVoteText") String reviewVoteText);

    /** CHECK THE USER'S VOTE ON A REVIEW **/
    @GET("checkUserReviewVote")
    Call<ReviewVote> checkUserReviewVote(
            @Query("reviewID") String reviewID,
            @Query("userID") String userID);

    /** UPDATE A REVIEW VOTE TO HELPFUL **/
    @POST("updateReviewVoteYes")
    @FormUrlEncoded
    Call<ReviewVote> updateReviewVoteYes(
            @Field("reviewVoteID") String reviewVoteID,
            @Field("reviewVoteText") String reviewVoteText);

    /** UPDATE A REVIEW VOTE TO NOT HELPFUL **/
    @POST("updateReviewVoteNo")
    @FormUrlEncoded
    Call<ReviewVote> updateReviewVoteNo(
            @Field("reviewVoteID") String reviewVoteID,
            @Field("reviewVoteText") String reviewVoteText);
}