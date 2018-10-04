package biz.zenpets.users.utils.models.groomers.review;

import com.google.gson.annotations.SerializedName;

public class GroomerRating {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("avgGroomerRating") private String avgGroomerRating;

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

    public String getAvgGroomerRating() {
        return avgGroomerRating;
    }

    public void setAvgGroomerRating(String avgGroomerRating) {
        this.avgGroomerRating = avgGroomerRating;
    }
}