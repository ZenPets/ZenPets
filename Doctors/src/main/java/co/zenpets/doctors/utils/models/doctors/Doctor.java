package co.zenpets.doctors.utils.models.doctors;

import com.google.gson.annotations.SerializedName;

public class Doctor {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("clinicID") private String clinicID;
    @SerializedName("userID") private String userID;
    @SerializedName("doctorPrefix") private String doctorPrefix;
    @SerializedName("doctorName") private String doctorName;
    @SerializedName("doctorAddress") private String doctorAddress;
    @SerializedName("doctorDisplayProfile") private String doctorDisplayProfile;
    @SerializedName("doctorPhonePrefix") private String doctorPhonePrefix;
    @SerializedName("doctorPhoneNumber") private String doctorPhoneNumber;
    @SerializedName("doctorGender") private String doctorGender;
    @SerializedName("doctorSummary") private String doctorSummary;
    @SerializedName("doctorExperience") private String doctorExperience;
    @SerializedName("doctorCharges") private String doctorCharges;
    @SerializedName("currencySymbol") private String currencySymbol;
    @SerializedName("doctorVotes") private String doctorVotes;
    @SerializedName("doctorLikes") private String doctorLikes;
    @SerializedName("doctorLikesPercent") private String doctorLikesPercent;
    @SerializedName("doctorClaimed") private String doctorClaimed;
    @SerializedName("doctorToken") private String doctorToken;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getClinicID() {
        return clinicID;
    }

    public void setClinicID(String clinicID) {
        this.clinicID = clinicID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public String getDoctorDisplayProfile() {
        return doctorDisplayProfile;
    }

    public void setDoctorDisplayProfile(String doctorDisplayProfile) {
        this.doctorDisplayProfile = doctorDisplayProfile;
    }

    public String getDoctorPhonePrefix() {
        return doctorPhonePrefix;
    }

    public void setDoctorPhonePrefix(String doctorPhonePrefix) {
        this.doctorPhonePrefix = doctorPhonePrefix;
    }

    public String getDoctorPhoneNumber() {
        return doctorPhoneNumber;
    }

    public void setDoctorPhoneNumber(String doctorPhoneNumber) {
        this.doctorPhoneNumber = doctorPhoneNumber;
    }

    public String getDoctorGender() {
        return doctorGender;
    }

    public void setDoctorGender(String doctorGender) {
        this.doctorGender = doctorGender;
    }

    public String getDoctorSummary() {
        return doctorSummary;
    }

    public void setDoctorSummary(String doctorSummary) {
        this.doctorSummary = doctorSummary;
    }

    public String getDoctorExperience() {
        return doctorExperience;
    }

    public void setDoctorExperience(String doctorExperience) {
        this.doctorExperience = doctorExperience;
    }

    public String getDoctorCharges() {
        return doctorCharges;
    }

    public void setDoctorCharges(String doctorCharges) {
        this.doctorCharges = doctorCharges;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
    }

    public String getDoctorVotes() {
        return doctorVotes;
    }

    public void setDoctorVotes(String doctorVotes) {
        this.doctorVotes = doctorVotes;
    }

    public String getDoctorLikes() {
        return doctorLikes;
    }

    public void setDoctorLikes(String doctorLikes) {
        this.doctorLikes = doctorLikes;
    }

    public String getDoctorLikesPercent() {
        return doctorLikesPercent;
    }

    public void setDoctorLikesPercent(String doctorLikesPercent) {
        this.doctorLikesPercent = doctorLikesPercent;
    }

    public String getDoctorClaimed() {
        return doctorClaimed;
    }

    public void setDoctorClaimed(String doctorClaimed) {
        this.doctorClaimed = doctorClaimed;
    }

    public String getDoctorToken() {
        return doctorToken;
    }

    public void setDoctorToken(String doctorToken) {
        this.doctorToken = doctorToken;
    }
}