package co.zenpets.doctors.utils.models.doctors.clinic;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DoctorClinics {

    @SerializedName("error") private Boolean error;
    @SerializedName("clinics") private ArrayList<DoctorClinic> clinics = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<DoctorClinic> getClinics() {
        return clinics;
    }

    public void setClinics(ArrayList<DoctorClinic> clinics) {
        this.clinics = clinics;
    }
}