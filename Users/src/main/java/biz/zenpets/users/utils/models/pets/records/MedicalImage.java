package biz.zenpets.users.utils.models.pets.records;

import com.google.gson.annotations.SerializedName;

public class MedicalImage {

    @SerializedName("recordImageID") private String recordImageID;
    @SerializedName("medicalRecordID") private String medicalRecordID;
    @SerializedName("recordImageURL") private String recordImageURL;

    public String getRecordImageID() {
        return recordImageID;
    }

    public void setRecordImageID(String recordImageID) {
        this.recordImageID = recordImageID;
    }

    public String getMedicalRecordID() {
        return medicalRecordID;
    }

    public void setMedicalRecordID(String medicalRecordID) {
        this.medicalRecordID = medicalRecordID;
    }

    public String getRecordImageURL() {
        return recordImageURL;
    }

    public void setRecordImageURL(String recordImageURL) {
        this.recordImageURL = recordImageURL;
    }
}