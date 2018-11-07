package co.zenpets.users.utils.models.appointment;

import com.google.gson.annotations.SerializedName;

public class Appointment {
    @SerializedName("appointmentID") private String appointmentID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorPrefix") private String doctorPrefix;
    @SerializedName("doctorName") private String doctorName;
    @SerializedName("doctorDisplayProfile") private String doctorDisplayProfile;
    @SerializedName("clinicID") private String clinicID;
    @SerializedName("clinicName") private String clinicName;
    @SerializedName("clinicAddress") private String clinicAddress;
    @SerializedName("cityName") private String cityName;
    @SerializedName("localityName") private String localityName;
    @SerializedName("clinicLatitude") private String clinicLatitude;
    @SerializedName("clinicLongitude") private String clinicLongitude;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("petID") private String petID;
    @SerializedName("petName") private String petName;
    @SerializedName("petTypeID") private String petTypeID;
    @SerializedName("petTypeName") private String petTypeName;
    @SerializedName("breedID") private String breedID;
    @SerializedName("breedName") private String breedName;
    @SerializedName("visitReasonID") private String visitReasonID;
    @SerializedName("visitReason") private String visitReason;
    @SerializedName("appointmentDate") private String appointmentDate;
    @SerializedName("appointmentTime") private String appointmentTime;
    @SerializedName("appointmentStatus") private String appointmentStatus;
    @SerializedName("appointmentNote") private String appointmentNote;
    @SerializedName("appointmentTimestamp") private String appointmentTimestamp;

    public String getAppointmentID() {
        return appointmentID;
    }

    public void setAppointmentID(String appointmentID) {
        this.appointmentID = appointmentID;
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

    public String getClinicID() {
        return clinicID;
    }

    public void setClinicID(String clinicID) {
        this.clinicID = clinicID;
    }

    public String getClinicName() {
        return clinicName;
    }

    public void setClinicName(String clinicName) {
        this.clinicName = clinicName;
    }

    public String getClinicAddress() {
        return clinicAddress;
    }

    public void setClinicAddress(String clinicAddress) {
        this.clinicAddress = clinicAddress;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getClinicLatitude() {
        return clinicLatitude;
    }

    public void setClinicLatitude(String clinicLatitude) {
        this.clinicLatitude = clinicLatitude;
    }

    public String getClinicLongitude() {
        return clinicLongitude;
    }

    public void setClinicLongitude(String clinicLongitude) {
        this.clinicLongitude = clinicLongitude;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayProfile() {
        return userDisplayProfile;
    }

    public void setUserDisplayProfile(String userDisplayProfile) {
        this.userDisplayProfile = userDisplayProfile;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetTypeID() {
        return petTypeID;
    }

    public void setPetTypeID(String petTypeID) {
        this.petTypeID = petTypeID;
    }

    public String getPetTypeName() {
        return petTypeName;
    }

    public void setPetTypeName(String petTypeName) {
        this.petTypeName = petTypeName;
    }

    public String getBreedID() {
        return breedID;
    }

    public void setBreedID(String breedID) {
        this.breedID = breedID;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    public String getVisitReasonID() {
        return visitReasonID;
    }

    public void setVisitReasonID(String visitReasonID) {
        this.visitReasonID = visitReasonID;
    }

    public String getVisitReason() {
        return visitReason;
    }

    public void setVisitReason(String visitReason) {
        this.visitReason = visitReason;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }

    public String getAppointmentNote() {
        return appointmentNote;
    }

    public void setAppointmentNote(String appointmentNote) {
        this.appointmentNote = appointmentNote;
    }

    public String getAppointmentTimestamp() {
        return appointmentTimestamp;
    }

    public void setAppointmentTimestamp(String appointmentTimestamp) {
        this.appointmentTimestamp = appointmentTimestamp;
    }
}