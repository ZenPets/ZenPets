package co.zenpets.users.utils.models.doctors.clinics;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Clinics {

    @SerializedName("error") private Boolean error;
    @SerializedName("clinics") private ArrayList<Clinic> clinics = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Clinic> getClinics() {
        return clinics;
    }

    public void setClinics(ArrayList<Clinic> clinics) {
        this.clinics = clinics;
    }
}