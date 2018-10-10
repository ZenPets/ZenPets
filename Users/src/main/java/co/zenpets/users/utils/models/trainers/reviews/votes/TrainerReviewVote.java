package co.zenpets.users.utils.models.trainers.reviews.votes;

import com.google.gson.annotations.SerializedName;

public class TrainerReviewVote {

    @SerializedName("error") private Boolean error;
    @SerializedName("reviewVoteID") private String reviewVoteID;
    @SerializedName("trainerReviewID") private String trainerReviewID;
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

    public String getTrainerReviewID() {
        return trainerReviewID;
    }

    public void setTrainerReviewID(String trainerReviewID) {
        this.trainerReviewID = trainerReviewID;
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