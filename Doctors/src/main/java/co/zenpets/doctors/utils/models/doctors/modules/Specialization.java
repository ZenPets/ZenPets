package co.zenpets.doctors.utils.models.doctors.modules;

import com.google.gson.annotations.SerializedName;

public class Specialization {

    @SerializedName("doctorSpecializationID") private String doctorSpecializationID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorSpecializationName") private String doctorSpecializationName;

    public String getDoctorSpecializationID() {
        return doctorSpecializationID;
    }

    public void setDoctorSpecializationID(String doctorSpecializationID) {
        this.doctorSpecializationID = doctorSpecializationID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorSpecializationName() {
        return doctorSpecializationName;
    }

    public void setDoctorSpecializationName(String doctorSpecializationName) {
        this.doctorSpecializationName = doctorSpecializationName;
    }
}