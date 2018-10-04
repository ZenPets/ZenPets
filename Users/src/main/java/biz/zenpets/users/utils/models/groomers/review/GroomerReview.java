package biz.zenpets.users.utils.models.groomers.review;

import com.google.gson.annotations.SerializedName;

public class GroomerReview {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("reviewID") private String reviewID;
    @SerializedName("groomerID") private String groomerID;
    @SerializedName("groomerName") private String groomerName;
    @SerializedName("groomerLogo") private String groomerLogo;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("groomerRating") private String groomerRating;
    @SerializedName("groomerRecommendStatus") private String groomerRecommendStatus;
    @SerializedName("groomerExperience") private String groomerExperience;
    @SerializedName("groomerReviewTimestamp") private String groomerReviewTimestamp;
    @SerializedName("groomerReviewDate") private String groomerReviewDate;
    @SerializedName("groomerReplyStatus") private String groomerReplyStatus;
    @SerializedName("groomerReplyText") private String groomerReplyText;
    @SerializedName("groomerReplyPublished") private String groomerReplyPublished;
    @SerializedName("groomerReplyUpdated") private String groomerReplyUpdated;

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

    public String getReviewID() {
        return reviewID;
    }

    public void setReviewID(String reviewID) {
        this.reviewID = reviewID;
    }

    public String getGroomerID() {
        return groomerID;
    }

    public void setGroomerID(String groomerID) {
        this.groomerID = groomerID;
    }

    public String getGroomerName() {
        return groomerName;
    }

    public void setGroomerName(String groomerName) {
        this.groomerName = groomerName;
    }

    public String getGroomerLogo() {
        return groomerLogo;
    }

    public void setGroomerLogo(String groomerLogo) {
        this.groomerLogo = groomerLogo;
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

    public String getGroomerRating() {
        return groomerRating;
    }

    public void setGroomerRating(String groomerRating) {
        this.groomerRating = groomerRating;
    }

    public String getGroomerRecommendStatus() {
        return groomerRecommendStatus;
    }

    public void setGroomerRecommendStatus(String groomerRecommendStatus) {
        this.groomerRecommendStatus = groomerRecommendStatus;
    }

    public String getGroomerExperience() {
        return groomerExperience;
    }

    public void setGroomerExperience(String groomerExperience) {
        this.groomerExperience = groomerExperience;
    }

    public String getGroomerReviewTimestamp() {
        return groomerReviewTimestamp;
    }

    public void setGroomerReviewTimestamp(String groomerReviewTimestamp) {
        this.groomerReviewTimestamp = groomerReviewTimestamp;
    }

    public String getGroomerReviewDate() {
        return groomerReviewDate;
    }

    public void setGroomerReviewDate(String groomerReviewDate) {
        this.groomerReviewDate = groomerReviewDate;
    }

    public String getGroomerReplyStatus() {
        return groomerReplyStatus;
    }

    public void setGroomerReplyStatus(String groomerReplyStatus) {
        this.groomerReplyStatus = groomerReplyStatus;
    }

    public String getGroomerReplyText() {
        return groomerReplyText;
    }

    public void setGroomerReplyText(String groomerReplyText) {
        this.groomerReplyText = groomerReplyText;
    }

    public String getGroomerReplyPublished() {
        return groomerReplyPublished;
    }

    public void setGroomerReplyPublished(String groomerReplyPublished) {
        this.groomerReplyPublished = groomerReplyPublished;
    }

    public String getGroomerReplyUpdated() {
        return groomerReplyUpdated;
    }

    public void setGroomerReplyUpdated(String groomerReplyUpdated) {
        this.groomerReplyUpdated = groomerReplyUpdated;
    }
}