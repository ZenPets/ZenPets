package co.zenpets.doctors.utils.models.appointments;

import com.google.gson.annotations.SerializedName;

class MonthlyAppointment {

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