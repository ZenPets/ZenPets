package co.zenpets.users.utils.models.pets.vaccines;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Vaccines {

    @SerializedName("error") private Boolean error;
    @SerializedName("vaccines") private ArrayList<Vaccine> vaccines = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Vaccine> getVaccines() {
        return vaccines;
    }

    public void setVaccines(ArrayList<Vaccine> vaccines) {
        this.vaccines = vaccines;
    }
}