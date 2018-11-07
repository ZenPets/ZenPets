package co.zenpets.doctors.utils.models.appointments;

import com.google.gson.annotations.SerializedName;

public class AppointmentSlotsData {

    @SerializedName("error") private Boolean error;
    @SerializedName("appointmentTime") private String appointmentTime;
    @SerializedName("appointmentDate") private String appointmentDate;
    @SerializedName("appointmentStatus") private String appointmentStatus;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("clinicID") private String clinicID;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
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
}