package co.zenpets.users.utils.models.trainers.reviews;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class TrainerReviews {

    @SerializedName("error") private Boolean error;
    @SerializedName("reviews") private ArrayList<TrainerReview> reviews = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<TrainerReview> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<TrainerReview> reviews) {
        this.reviews = reviews;
    }
}