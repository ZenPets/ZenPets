package co.zenpets.users.utils.models.consultations.replies;

import com.google.gson.annotations.SerializedName;

public class ConsultationReply {

    @SerializedName("replyID") private String replyID;
    @SerializedName("consultationID") private String consultationID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorPrefix") private String doctorPrefix;
    @SerializedName("doctorName") private String doctorName;
    @SerializedName("doctorDisplayProfile") private String doctorDisplayProfile;
    @SerializedName("stateName") private String stateName;
    @SerializedName("cityName") private String cityName;
    @SerializedName("replyText") private String replyText;
    @SerializedName("replyTimestamp") private String replyTimestamp;

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

    public String getDoctorPrefix() {
        return doctorPrefix;
    }

    public void setDoctorPrefix(String doctorPrefix) {
        this.doctorPrefix = doctorPrefix;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorDisplayProfile() {
        return doctorDisplayProfile;
    }

    public void setDoctorDisplayProfile(String doctorDisplayProfile) {
        this.doctorDisplayProfile = doctorDisplayProfile;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
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