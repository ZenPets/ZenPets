package co.zenpets.users.utils.models.pets.records;

import com.google.gson.annotations.SerializedName;

public class RecordType {

    @SerializedName("recordTypeID") private String recordTypeID;
    @SerializedName("recordTypeName") private String recordTypeName;

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
}