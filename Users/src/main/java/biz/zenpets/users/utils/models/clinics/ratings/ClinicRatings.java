package biz.zenpets.users.utils.models.clinics.ratings;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class ClinicRatings {

    @SerializedName("error") private Boolean error;
    @SerializedName("ratings") private ArrayList<ClinicRating> ratings = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<ClinicRating> getRatings() {
        return ratings;
    }

    public void setRatings(ArrayList<ClinicRating> ratings) {
        this.ratings = ratings;
    }
}