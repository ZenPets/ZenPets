package co.zenpets.doctors.utils.models.doctors.account;

import com.google.gson.annotations.SerializedName;

public class SignUp {

    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("doctorID") private String doctorID;

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
}