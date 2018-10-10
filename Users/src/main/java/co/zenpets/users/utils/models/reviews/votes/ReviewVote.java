package co.zenpets.users.utils.models.reviews.votes;

import com.google.gson.annotations.SerializedName;

public class ReviewVote {

    @SerializedName("error") private Boolean error;
    @SerializedName("reviewVoteID") private String reviewVoteID;
    @SerializedName("reviewID") private String reviewID;
    @SerializedName("userID") private String userID;
    @SerializedName("reviewVoteText") private String reviewVoteText;
    @SerializedName("reviewVoteTimestamp") private String reviewVoteTimestamp;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getReviewVoteID() {
        return reviewVoteID;
    }

    public void setReviewVoteID(String reviewVoteID) {
        this.reviewVoteID = reviewVoteID;
    }

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getReviewVoteText() {
        return reviewVoteText;
    }

    public void setReviewVoteText(String reviewVoteText) {
        this.reviewVoteText = reviewVoteText;
    }

    public String getReviewVoteTimestamp() {
        return reviewVoteTimestamp;
    }

    public void setReviewVoteTimestamp(String reviewVoteTimestamp) {
        this.reviewVoteTimestamp = reviewVoteTimestamp;
    }
}