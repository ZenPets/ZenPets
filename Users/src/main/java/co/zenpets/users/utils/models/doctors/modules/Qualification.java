package co.zenpets.users.utils.models.doctors.modules;

import com.google.gson.annotations.SerializedName;

public class Qualification {

    @SerializedName("doctorEducationID") private String doctorEducationID;
    @SerializedName("doctorID") private String doctorID;
    @SerializedName("doctorCollegeName") private String doctorCollegeName;
    @SerializedName("doctorEducationName") private String doctorEducationName;
    @SerializedName("doctorEducationYear") private String doctorEducationYear;

    public String getDoctorEducationID() {
        return doctorEducationID;
    }

    public void setDoctorEducationID(String doctorEducationID) {
        this.doctorEducationID = doctorEducationID;
    }

    public String getDoctorID() {
        return doctorID;
    }

    public void setDoctorID(String doctorID) {
        this.doctorID = doctorID;
    }

    public String getDoctorCollegeName() {
        return doctorCollegeName;
    }

    public void setDoctorCollegeName(String doctorCollegeName) {
        this.doctorCollegeName = doctorCollegeName;
    }

    public String getDoctorEducationName() {
        return doctorEducationName;
    }

    public void setDoctorEducationName(String doctorEducationName) {
        this.doctorEducationName = doctorEducationName;
    }

    public String getDoctorEducationYear() {
        return doctorEducationYear;
    }

    public void setDoctorEducationYear(String doctorEducationYear) {
        this.doctorEducationYear = doctorEducationYear;
    }
}