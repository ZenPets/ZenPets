package co.zenpets.users.utils.models.doctors.modules;

import com.google.gson.annotations.SerializedName;

public class Service {

    @SerializedName("doctorServiceID") private String doctorServiceID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorServiceName") private String doctorServiceName;

    public String getDoctorServiceID() {
        return doctorServiceID;
    }

    public void setDoctorServiceID(String doctorServiceID) {
        this.doctorServiceID = doctorServiceID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorServiceName() {
        return doctorServiceName;
    }

    public void setDoctorServiceName(String doctorServiceName) {
        this.doctorServiceName = doctorServiceName;
    }
}