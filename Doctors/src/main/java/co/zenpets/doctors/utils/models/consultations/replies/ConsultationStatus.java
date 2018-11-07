package co.zenpets.doctors.utils.models.consultations.replies;

import com.google.gson.annotations.SerializedName;

public class ConsultationStatus {

    @SerializedName("error") private Boolean error;
    @SerializedName("replyID") private String replyID;
    @SerializedName("consultationID") private String consultationID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("replyText") private String replyText;
    @SerializedName("replyTimestamp") private String replyTimestamp;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getReplyID() {
        return replyID;
    }

    public void setReplyID(String replyID) {
        this.replyID = replyID;
    }

    public String getConsultationID() {
        return consultationID;
    }

    public void setConsultationID(String consultationID) {
        this.consultationID = consultationID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public String getReplyTimestamp() {
        return replyTimestamp;
    }

    public void setReplyTimestamp(String replyTimestamp) {
        this.replyTimestamp = replyTimestamp;
    }
}