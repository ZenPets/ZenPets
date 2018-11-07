package co.zenpets.users.utils.models.reviews;

import com.google.gson.annotations.SerializedName;

public class ReviewCount {
    @SerializedName("error") private Boolean error;
    @SerializedName("total_reviews") private String total_reviews;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getTotal_reviews() {
        return total_reviews;
    }

    public void setTotal_reviews(String total_reviews) {
        this.total_reviews = total_reviews;
    }
}