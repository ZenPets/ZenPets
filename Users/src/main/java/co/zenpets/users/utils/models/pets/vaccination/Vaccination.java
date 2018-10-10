package co.zenpets.users.utils.models.pets.vaccination;

import com.google.gson.annotations.SerializedName;

public class Vaccination {

    @SerializedName("vaccinationID") private String vaccinationID;
    @SerializedName("petID") private String petID;
    @SerializedName("vaccineID") private String vaccineID;
    @SerializedName("vaccineName") private String vaccineName;
    @SerializedName("vaccinationDate") private String vaccinationDate;
    @SerializedName("vaccinationNextDate") private String vaccinationNextDate;
    @SerializedName("vaccinationReminder") private String vaccinationReminder;
    @SerializedName("vaccinationNotes") private String vaccinationNotes;

    public String getVaccinationID() {
        return vaccinationID;
    }

    public void setVaccinationID(String vaccinationID) {
        this.vaccinationID = vaccinationID;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

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

    public String getVaccinationDate() {
        return vaccinationDate;
    }

    public void setVaccinationDate(String vaccinationDate) {
        this.vaccinationDate = vaccinationDate;
    }

    public String getVaccinationNextDate() {
        return vaccinationNextDate;
    }

    public void setVaccinationNextDate(String vaccinationNextDate) {
        this.vaccinationNextDate = vaccinationNextDate;
    }

    public String getVaccinationReminder() {
        return vaccinationReminder;
    }

    public void setVaccinationReminder(String vaccinationReminder) {
        this.vaccinationReminder = vaccinationReminder;
    }

    public String getVaccinationNotes() {
        return vaccinationNotes;
    }

    public void setVaccinationNotes(String vaccinationNotes) {
        this.vaccinationNotes = vaccinationNotes;
    }
}