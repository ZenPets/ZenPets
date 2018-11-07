package co.zenpets.doctors.utils.models.doctors.modules;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Specializations {

    @SerializedName("error") private Boolean error;
    @SerializedName("specializations") private ArrayList<Specialization> specializations = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(ArrayList<Specialization> specializations) {
        this.specializations = specializations;
    }
}