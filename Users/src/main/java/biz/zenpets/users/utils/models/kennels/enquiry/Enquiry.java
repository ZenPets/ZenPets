package biz.zenpets.users.utils.models.kennels.enquiry;

import com.google.gson.annotations.SerializedName;

public class Enquiry {
    @SerializedName("error") private Boolean error;
    @SerializedName("kennelEnquiryID") private String kennelEnquiryID;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("userID") private String userID;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getKennelEnquiryID() {
        return kennelEnquiryID;
    }

    public void setKennelEnquiryID(String kennelEnquiryID) {
        this.kennelEnquiryID = kennelEnquiryID;
    }

    public String getKennelID() {
        return kennelID;
    }

    public void setKennelID(String kennelID) {
        this.kennelID = kennelID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}