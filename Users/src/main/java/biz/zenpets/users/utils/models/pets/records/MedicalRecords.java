package biz.zenpets.users.utils.models.pets.records;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class MedicalRecords {

    @SerializedName("error") private Boolean error;
    @SerializedName("records") private ArrayList<MedicalRecord> records = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<MedicalRecord> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<MedicalRecord> records) {
        this.records = records;
    }
}