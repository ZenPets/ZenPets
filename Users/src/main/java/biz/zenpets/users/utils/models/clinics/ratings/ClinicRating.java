package biz.zenpets.users.utils.models.clinics.ratings;

import com.google.gson.annotations.SerializedName;

public class ClinicRating {

    @SerializedName("error") private Boolean error;
    @SerializedName("clinicRatingID") private String clinicRatingID;
    @SerializedName("clinicID") private String clinicID;
    @SerializedName("userID") private String userID;
    @SerializedName("clinicRating") private String clinicRating;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getClinicRatingID() {
        return clinicRatingID;
    }

    public void setClinicRatingID(String clinicRatingID) {
        this.clinicRatingID = clinicRatingID;
    }

    public String getClinicID() {
        return clinicID;
    }

    public void setClinicID(String clinicID) {
        this.clinicID = clinicID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getClinicRating() {
        return clinicRating;
    }

    public void setClinicRating(String clinicRating) {
        this.clinicRating = clinicRating;
    }
}