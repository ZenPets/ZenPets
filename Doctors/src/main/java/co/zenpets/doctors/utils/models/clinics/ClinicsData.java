package co.zenpets.doctors.utils.models.clinics;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ClinicsData {

    @SerializedName("error") private Boolean error;
    @SerializedName("clinics") private ArrayList<ClinicData> clinics = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<ClinicData> getClinics() {
        return clinics;
    }

    public void setClinics(ArrayList<ClinicData> clinics) {
        this.clinics = clinics;
    }
}