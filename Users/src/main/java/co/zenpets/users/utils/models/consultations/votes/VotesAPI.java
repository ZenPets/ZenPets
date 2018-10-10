package co.zenpets.users.utils.models.consultations.votes;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface VotesAPI {

    /** PUBLISH THE USER'S VOTE ON A DOCTOR'S REPLY **/
    @POST("newReplyVote")
    @FormUrlEncoded
    Call<Vote> newReplyVote(
            @Field("replyID") String replyID,
            @Field("userID") String userID,
            @Field("voteStatus") String voteStatus,
            @Field("voteTimestamp") String voteTimestamp);

    /** FETCH "YES" REPLY VOTES **/
    @GET("fetchYesVotes")
    Call<Votes> fetchYesVotes(
            @Query("replyID") String replyID,
            @Query("voteStatus") String voteStatus);

    /** FETCH "NO" REPLY VOTES **/
    @GET("fetchNoVotes")
    Call<Votes> fetchNoVotes(
            @Query("replyID") String replyID,
            @Query("voteStatus") String voteStatus);

    /** CHECK THE USER'S VOTE STATUS ON A REPLY **/
    @GET("checkUserVote")
    Call<Vote> checkUserVote(
            @Query("replyID") String replyID,
            @Query("userID") String userID);

    /** UPDATE THE USER'S VOTE TO "YES" **/
    @POST("updateReplyVoteYes")
    @FormUrlEncoded
    Call<Vote> updateReplyVoteYes(
            @Field("voteID") String voteID,
            @Field("voteStatus") String voteStatus);

    /** UPDATE THE USER'S VOTE TO "NO" **/
    @POST("updateReplyVoteNo")
    @FormUrlEncoded
    Call<Vote> updateReplyVoteNo(
            @Field("voteID") String voteID,
            @Field("voteStatus") String voteStatus);
}