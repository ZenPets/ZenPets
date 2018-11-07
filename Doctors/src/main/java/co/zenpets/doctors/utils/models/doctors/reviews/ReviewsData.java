package co.zenpets.doctors.utils.models.doctors.reviews;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ReviewsData {

    @SerializedName("error") private Boolean error;
    @SerializedName("reviews") private ArrayList<ReviewData> reviews = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<ReviewData> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<ReviewData> reviews) {
        this.reviews = reviews;
    }
}