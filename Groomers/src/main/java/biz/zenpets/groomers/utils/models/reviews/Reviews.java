package biz.zenpets.groomers.utils.models.reviews;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Reviews {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("reviews") private ArrayList<Review> reviews = null;

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

    public ArrayList<Review> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<Review> reviews) {
        this.reviews = reviews;
    }
}