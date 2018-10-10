package co.zenpets.users.utils.models.doctors.timings;

import com.google.gson.annotations.SerializedName;

public class TimeSlot {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("clinicID") private String clinicID;
    @SerializedName("appointmentTime") private String appointmentTime;
    @SerializedName("appointmentDate") private String appointmentDate;
    @SerializedName("appointmentStatus") private String appointmentStatus;

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

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentStatus() {
        return appointmentStatus;
    }

    public void setAppointmentStatus(String appointmentStatus) {
        this.appointmentStatus = appointmentStatus;
    }
}