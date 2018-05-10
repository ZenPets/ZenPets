package biz.zenpets.users.utils.models.consultations.views;

import com.google.gson.annotations.SerializedName;

public class ConsultationView {

    @SerializedName("consultationViewID") private String consultationViewID;
    @SerializedName("userID") private String userID;
    @SerializedName("consultationID") private String consultationID;

    public String getConsultationViewID() {
        return consultationViewID;
    }

    public void setConsultationViewID(String consultationViewID) {
        this.consultationViewID = consultationViewID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getConsultationID() {
        return consultationID;
    }

    public void setConsultationID(String consultationID) {
        this.consultationID = consultationID;
    }
}