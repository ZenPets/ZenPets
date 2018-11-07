package co.zenpets.doctors.utils.models.consultations.consultations;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Consultations {

    @SerializedName("error") private Boolean error;
    @SerializedName("consultations") private ArrayList<Consultation> consultations = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(ArrayList<Consultation> consultations) {
        this.consultations = consultations;
    }
}