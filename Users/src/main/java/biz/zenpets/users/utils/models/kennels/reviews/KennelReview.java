package biz.zenpets.users.utils.models.kennels.reviews;

import com.google.gson.annotations.SerializedName;

public class KennelReview {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennelReviewID") private String kennelReviewID;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("kennelOwnerName") private String kennelOwnerName;
    @SerializedName("kennelName") private String kennelName;
    @SerializedName("kennelCoverPhoto") private String kennelCoverPhoto;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("kennelRating") private String kennelRating;
    @SerializedName("kennelRecommendStatus") private String kennelRecommendStatus;
    @SerializedName("kennelExperience") private String kennelExperience;
    @SerializedName("kennelReviewTimestamp") private String kennelReviewTimestamp;
    @SerializedName("kennelReplyStatus") private String kennelReplyStatus;
    @SerializedName("kennelReplyText") private String kennelReplyText;
    @SerializedName("kennelReplyPublished") private String kennelReplyPublished;
    @SerializedName("kennelReplyUpdated") private String kennelReplyUpdated;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getKennelReviewID() {
        return kennelReviewID;
    }

    public void setKennelReviewID(String kennelReviewID) {
        this.kennelReviewID = kennelReviewID;
    }

    public String getKennelID() {
        return kennelID;
    }

    public void setKennelID(String kennelID) {
        this.kennelID = kennelID;
    }

    public String getKennelOwnerName() {
        return kennelOwnerName;
    }

    public void setKennelOwnerName(String kennelOwnerName) {
        this.kennelOwnerName = kennelOwnerName;
    }

    public String getKennelName() {
        return kennelName;
    }

    public void setKennelName(String kennelName) {
        this.kennelName = kennelName;
    }

    public String getKennelCoverPhoto() {
        return kennelCoverPhoto;
    }

    public void setKennelCoverPhoto(String kennelCoverPhoto) {
        this.kennelCoverPhoto = kennelCoverPhoto;
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

    public String getKennelRating() {
        return kennelRating;
    }

    public void setKennelRating(String kennelRating) {
        this.kennelRating = kennelRating;
    }

    public String getKennelRecommendStatus() {
        return kennelRecommendStatus;
    }

    public void setKennelRecommendStatus(String kennelRecommendStatus) {
        this.kennelRecommendStatus = kennelRecommendStatus;
    }

    public String getKennelExperience() {
        return kennelExperience;
    }

    public void setKennelExperience(String kennelExperience) {
        this.kennelExperience = kennelExperience;
    }

    public String getKennelReviewTimestamp() {
        return kennelReviewTimestamp;
    }

    public void setKennelReviewTimestamp(String kennelReviewTimestamp) {
        this.kennelReviewTimestamp = kennelReviewTimestamp;
    }

    public String getKennelReplyStatus() {
        return kennelReplyStatus;
    }

    public void setKennelReplyStatus(String kennelReplyStatus) {
        this.kennelReplyStatus = kennelReplyStatus;
    }

    public String getKennelReplyText() {
        return kennelReplyText;
    }

    public void setKennelReplyText(String kennelReplyText) {
        this.kennelReplyText = kennelReplyText;
    }

    public String getKennelReplyPublished() {
        return kennelReplyPublished;
    }

    public void setKennelReplyPublished(String kennelReplyPublished) {
        this.kennelReplyPublished = kennelReplyPublished;
    }

    public String getKennelReplyUpdated() {
        return kennelReplyUpdated;
    }

    public void setKennelReplyUpdated(String kennelReplyUpdated) {
        this.kennelReplyUpdated = kennelReplyUpdated;
    }
}