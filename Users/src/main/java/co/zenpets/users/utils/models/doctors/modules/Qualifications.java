package co.zenpets.users.utils.models.doctors.modules;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Qualifications {

    @SerializedName("error") private Boolean error;
    @SerializedName("qualifications") private ArrayList<Qualification> qualifications = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Qualification> getQualifications() {
        return qualifications;
    }

    public void setQualifications(ArrayList<Qualification> qualifications) {
        this.qualifications = qualifications;
    }

}