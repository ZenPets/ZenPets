package co.zenpets.doctors.utils.models.doctors.claims;

import com.google.gson.annotations.SerializedName;

public class Claim {

    @SerializedName("doctorClaimID") private String doctorClaimID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorClaimEmail") private String doctorClaimEmail;
    @SerializedName("doctorClaimPhone") private String doctorClaimPhone;
    @SerializedName("doctorClaimStatus") private String doctorClaimStatus;
    @SerializedName("doctorClaimApproved") private String doctorClaimApproved;
    @SerializedName("doctorClaimTimestamp") private String doctorClaimTimestamp;

    public String getDoctorClaimID() {
        return doctorClaimID;
    }

    public void setDoctorClaimID(String doctorClaimID) {
        this.doctorClaimID = doctorClaimID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorClaimEmail() {
        return doctorClaimEmail;
    }

    public void setDoctorClaimEmail(String doctorClaimEmail) {
        this.doctorClaimEmail = doctorClaimEmail;
    }

    public String getDoctorClaimPhone() {
        return doctorClaimPhone;
    }

    public void setDoctorClaimPhone(String doctorClaimPhone) {
        this.doctorClaimPhone = doctorClaimPhone;
    }

    public String getDoctorClaimStatus() {
        return doctorClaimStatus;
    }

    public void setDoctorClaimStatus(String doctorClaimStatus) {
        this.doctorClaimStatus = doctorClaimStatus;
    }

    public String getDoctorClaimApproved() {
        return doctorClaimApproved;
    }

    public void setDoctorClaimApproved(String doctorClaimApproved) {
        this.doctorClaimApproved = doctorClaimApproved;
    }

    public String getDoctorClaimTimestamp() {
        return doctorClaimTimestamp;
    }

    public void setDoctorClaimTimestamp(String doctorClaimTimestamp) {
        this.doctorClaimTimestamp = doctorClaimTimestamp;
    }
}