package biz.zenpets.users.utils.models.pets.vaccines;

import com.google.gson.annotations.SerializedName;

public class Vaccine {

    @SerializedName("vaccineID") private String vaccineID;
    @SerializedName("vaccineName") private String vaccineName;
    @SerializedName("vaccineDescription") private String vaccineDescription;

    public String getVaccineID() {
        return vaccineID;
    }

    public void setVaccineID(String vaccineID) {
        this.vaccineID = vaccineID;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getVaccineDescription() {
        return vaccineDescription;
    }

    public void setVaccineDescription(String vaccineDescription) {
        this.vaccineDescription = vaccineDescription;
    }
}