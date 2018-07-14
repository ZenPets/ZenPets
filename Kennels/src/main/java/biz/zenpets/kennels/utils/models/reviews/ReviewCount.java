package biz.zenpets.kennels.utils.models.reviews;

import com.google.gson.annotations.SerializedName;

public class ReviewCount {

    @SerializedName("error") private Boolean error;
    @SerializedName("totalReviews") private String totalReviews;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(String totalReviews) {
        this.totalReviews = totalReviews;
    }
}