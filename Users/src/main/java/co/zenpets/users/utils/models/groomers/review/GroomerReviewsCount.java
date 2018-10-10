package co.zenpets.users.utils.models.groomers.review;

import com.google.gson.annotations.SerializedName;

public class GroomerReviewsCount {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("totalReviews") private String totalReviews;

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

    public String getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(String totalReviews) {
        this.totalReviews = totalReviews;
    }
}