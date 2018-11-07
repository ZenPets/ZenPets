package co.zenpets.doctors.utils.models.doctors.clinic;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ClinicCheckerData {

    @SerializedName("error") @Expose private Boolean error;
    @SerializedName("clinics") @Expose private ArrayList<Clinic> clinics = null;

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