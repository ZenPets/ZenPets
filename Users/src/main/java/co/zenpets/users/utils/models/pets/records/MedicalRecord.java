package co.zenpets.users.utils.models.pets.records;

import com.google.gson.annotations.SerializedName;

public class MedicalRecord {

    @SerializedName("medicalRecordID") private String medicalRecordID;
    @SerializedName("recordTypeID") private String recordTypeID;
    @SerializedName("recordTypeName") private String recordTypeName;
    @SerializedName("userID") private String userID;
    @SerializedName("petID") private String petID;
    @SerializedName("medicalRecordNotes") private String medicalRecordNotes;
    @SerializedName("medicalRecordDate") private String medicalRecordDate;

    public String getMedicalRecordID() {
        return medicalRecordID;
    }

    public void setMedicalRecordID(String medicalRecordID) {
        this.medicalRecordID = medicalRecordID;
    }

    public String getRecordTypeID() {
        return recordTypeID;
    }

    public void setRecordTypeID(String recordTypeID) {
        this.recordTypeID = recordTypeID;
    }

    public String getRecordTypeName() {
        return recordTypeName;
    }

    public void setRecordTypeName(String recordTypeName) {
        this.recordTypeName = recordTypeName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getMedicalRecordNotes() {
        return medicalRecordNotes;
    }

    public void setMedicalRecordNotes(String medicalRecordNotes) {
        this.medicalRecordNotes = medicalRecordNotes;
    }

    public String getMedicalRecordDate() {
        return medicalRecordDate;
    }

    public void setMedicalRecordDate(String medicalRecordDate) {
        this.medicalRecordDate = medicalRecordDate;
    }
}