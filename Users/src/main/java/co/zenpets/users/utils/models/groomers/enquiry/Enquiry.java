package co.zenpets.users.utils.models.groomers.enquiry;

import com.google.gson.annotations.SerializedName;

public class Enquiry {
    @SerializedName("error") private Boolean error;
    @SerializedName("enquiryID") private String enquiryID;
    @SerializedName("groomerID") private String groomerID;
    @SerializedName("userID") private String userID;
    @SerializedName("enquiryDate") private String enquiryDate;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getEnquiryID() {
        return enquiryID;
    }

    public void setEnquiryID(String enquiryID) {
        this.enquiryID = enquiryID;
    }

    public String getGroomerID() {
        return groomerID;
    }

    public void setGroomerID(String groomerID) {
        this.groomerID = groomerID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEnquiryDate() {
        return enquiryDate;
    }

    public void setEnquiryDate(String enquiryDate) {
        this.enquiryDate = enquiryDate;
    }
}