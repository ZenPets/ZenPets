package co.zenpets.trainers.utils.models.trainers.reviews;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("message") private String message;
    @SerializedName("trainerReviewID") private String trainerReviewID;
    @SerializedName("trainerID") private String trainerID;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("recommendStatus") private String recommendStatus;
    @SerializedName("trainerExperience") private String trainerExperience;
    @SerializedName("reviewTimestamp") private String reviewTimestamp;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTrainerReviewID() {
        return trainerReviewID;
    }

    public void setTrainerReviewID(String trainerReviewID) {
        this.trainerReviewID = trainerReviewID;
    }

    public String getTrainerID() {
        return trainerID;
    }

    public void setTrainerID(String trainerID) {
        this.trainerID = trainerID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayProfile() {
        return userDisplayProfile;
    }

    public void setUserDisplayProfile(String userDisplayProfile) {
        this.userDisplayProfile = userDisplayProfile;
    }

    public String getRecommendStatus() {
        return recommendStatus;
    }

    public void setRecommendStatus(String recommendStatus) {
        this.recommendStatus = recommendStatus;
    }

    public String getTrainerExperience() {
        return trainerExperience;
    }

    public void setTrainerExperience(String trainerExperience) {
        this.trainerExperience = trainerExperience;
    }

    public String getReviewTimestamp() {
        return reviewTimestamp;
    }

    public void setReviewTimestamp(String reviewTimestamp) {
        this.reviewTimestamp = reviewTimestamp;
    }
}