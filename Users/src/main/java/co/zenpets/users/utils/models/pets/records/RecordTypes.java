package co.zenpets.users.utils.models.pets.records;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class RecordTypes {

    @SerializedName("error") private Boolean error;
    @SerializedName("records") private ArrayList<RecordType> records = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<RecordType> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<RecordType> records) {
        this.records = records;
    }
}