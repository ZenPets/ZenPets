package biz.zenpets.users.utils.models.kennels.reviews;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class KennelReviews {
    @SerializedName("error") private Boolean error;
    @SerializedName("reviews") private ArrayList<KennelReview> reviews = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<KennelReview> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<KennelReview> reviews) {
        this.reviews = reviews;
    }
}