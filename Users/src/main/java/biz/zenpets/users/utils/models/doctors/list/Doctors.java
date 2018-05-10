package biz.zenpets.users.utils.models.doctors.list;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Doctors {

    @SerializedName("error") private Boolean error;
    @SerializedName("doctors") private ArrayList<Doctor> doctors = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(ArrayList<Doctor> doctors) {
        this.doctors = doctors;
    }
}