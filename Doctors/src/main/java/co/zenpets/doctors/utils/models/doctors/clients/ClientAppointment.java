package co.zenpets.doctors.utils.models.doctors.clients;

import com.google.gson.annotations.SerializedName;

public class ClientAppointment {

    @SerializedName("appointmentID") private String appointmentID = null;
    @SerializedName("doctorID") private String doctorID = null;
    @SerializedName("clinicID") private String clinicID = null;
    @SerializedName("visitReasonID") private String visitReasonID = null;
    @SerializedName("visitReason") private String visitReason = null;
    @SerializedName("userID") private String userID = null;
    @SerializedName("petID") private String petID = null;
    @SerializedName("petName") private String petName = null;
    @SerializedName("petTypeID") private String petTypeID = null;
    @SerializedName("petTypeName") private String petTypeName = null;
    @SerializedName("breedID") private String breedID = null;
    @SerializedName("breedName") private String breedName = null;
    @SerializedName("appointmentDate") private String appointmentDate = null;
    @SerializedName("appointmentTime") private String appointmentTime = null;
    @SerializedName("appointmentStatus") private String appointmentStatus = null;
    @SerializedName("appointmentNote") private String appointmentNote = null;
    @SerializedName("appointmentTimestamp") private String appointmentTimestamp = null;

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

    public String getClinicID() {
        return clinicID;
    }

    public void setClinicID(String clinicID) {
        this.clinicID = clinicID;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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