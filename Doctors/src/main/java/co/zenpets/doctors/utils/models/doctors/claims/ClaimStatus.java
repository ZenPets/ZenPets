package co.zenpets.doctors.utils.models.doctors.claims;

import com.google.gson.annotations.SerializedName;

public class ClaimStatus {

    @SerializedName("doctorClaimID") private String doctorClaimID;
    @SerializedName("doctorClaimStatus") private String doctorClaimStatus;
    @SerializedName("doctorClaimApproved") private String doctorClaimApproved;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorPrefix") private String doctorPrefix;
    @SerializedName("doctorName") private String doctorName;
    @SerializedName("doctorAddress") private String doctorAddress;

    public String getDoctorClaimID() {
        return doctorClaimID;
    }

    public void setDoctorClaimID(String doctorClaimID) {
        this.doctorClaimID = doctorClaimID;
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

    public String getDoctorAddress() {
        return doctorAddress;
    }

    public void setDoctorAddress(String doctorAddress) {
        this.doctorAddress = doctorAddress;
    }
}