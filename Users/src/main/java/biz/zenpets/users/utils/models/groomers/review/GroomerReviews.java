package biz.zenpets.users.utils.models.groomers.review;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GroomerReviews {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("reviews") private ArrayList<GroomerReview> reviews = null;

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

    public ArrayList<GroomerReview> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<GroomerReview> reviews) {
        this.reviews = reviews;
    }
}