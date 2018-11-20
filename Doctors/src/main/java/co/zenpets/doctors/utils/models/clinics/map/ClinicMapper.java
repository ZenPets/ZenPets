package co.zenpets.doctors.utils.models.clinics.map;

import com.google.gson.annotations.SerializedName;

public class ClinicMapper {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("clinicID") private String clinicID;
    @SerializedName("clinicVerified") private String clinicVerified;

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

    public String getClinicVerified() {
        return clinicVerified;
    }

    public void setClinicVerified(String clinicVerified) {
        this.clinicVerified = clinicVerified;
    }
}