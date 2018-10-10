package co.zenpets.users.utils.models.pets.vaccination;

import com.google.gson.annotations.SerializedName;

public class VaccinationImage {

    @SerializedName("vaccinationImageID") private String vaccinationImageID;
    @SerializedName("vaccinationID") private String vaccinationID;
    @SerializedName("vaccinationImageURL") private String vaccinationImageURL;

    public String getVaccinationImageID() {
        return vaccinationImageID;
    }

    public void setVaccinationImageID(String vaccinationImageID) {
        this.vaccinationImageID = vaccinationImageID;
    }

    public String getVaccinationID() {
        return vaccinationID;
    }

    public void setVaccinationID(String vaccinationID) {
        this.vaccinationID = vaccinationID;
    }

    public String getVaccinationImageURL() {
        return vaccinationImageURL;
    }

    public void setVaccinationImageURL(String vaccinationImageURL) {
        this.vaccinationImageURL = vaccinationImageURL;
    }
}