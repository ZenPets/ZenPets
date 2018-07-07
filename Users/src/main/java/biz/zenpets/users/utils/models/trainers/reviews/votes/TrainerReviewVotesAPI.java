package biz.zenpets.users.utils.models.trainers.reviews.votes;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TrainerReviewVotesAPI {

    /** POST A NEW REVIEW VOTE **/
    @POST("newTrainerReviewVote")
    @FormUrlEncoded
    Call<TrainerReviewVote> newTrainerReviewVote(
            @Field("trainerReviewID") String trainerReviewID,
            @Field("userID") String userID,
            @Field("reviewVoteText") String reviewVoteText,
            @Field("reviewVoteTimestamp") String reviewVoteTimestamp);

    /** FETCH THE POSITIVE VOTES ON A REVIEW **/
    @GET("fetchPositiveTrainerReviewVotes")
    Call<TrainerReviewVotes> fetchPositiveTrainerReviewVotes(
            @Query("trainerReviewID") String trainerReviewID,
            @Query("reviewVoteText") String reviewVoteText);

    /** FETCH THE POSITIVE VOTES ON A REVIEW **/
    @GET("fetchNegativeTrainerReviewVotes")
    Call<TrainerReviewVotes> fetchNegativeTrainerReviewVotes(
            @Query("trainerReviewID") String trainerReviewID,
            @Query("reviewVoteText") String reviewVoteText);

    /** CHECK THE USER'S VOTE ON A REVIEW **/
    @GET("checkUserTrainerReviewVote")
    Call<TrainerReviewVote> checkUserTrainerReviewVote(
            @Query("trainerReviewID") String trainerReviewID,
            @Query("userID") String userID);

    /** UPDATE A REVIEW VOTE TO HELPFUL **/
    @POST("updateTrainerReviewVoteYes")
    @FormUrlEncoded
    Call<TrainerReviewVote> updateTrainerReviewVoteYes(
            @Field("reviewVoteID") String reviewVoteID,
            @Field("reviewVoteText") String reviewVoteText);

    /** UPDATE A REVIEW VOTE TO NOT HELPFUL **/
    @POST("updateTrainerReviewVoteNo")
    @FormUrlEncoded
    Call<TrainerReviewVote> updateTrainerReviewVoteNo(
            @Field("reviewVoteID") String reviewVoteID,
            @Field("reviewVoteText") String reviewVoteText);
}