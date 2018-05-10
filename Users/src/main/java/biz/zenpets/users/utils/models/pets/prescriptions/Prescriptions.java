package biz.zenpets.users.utils.models.pets.prescriptions;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Prescriptions {

    @SerializedName("error") private Boolean error;
    @SerializedName("prescriptions") private ArrayList<Prescription> prescriptions = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Prescription> getPrescriptions() {
        return prescriptions;
    }

    public void setPrescriptions(ArrayList<Prescription> prescriptions) {
        this.prescriptions = prescriptions;
    }
}