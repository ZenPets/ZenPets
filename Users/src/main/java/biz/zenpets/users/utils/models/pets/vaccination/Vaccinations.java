package biz.zenpets.users.utils.models.pets.vaccination;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Vaccinations {

    @SerializedName("error") private Boolean error;
    @SerializedName("vaccinations") private ArrayList<Vaccination> vaccinations = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Vaccination> getVaccinations() {
        return vaccinations;
    }

    public void setVaccinations(ArrayList<Vaccination> vaccinations) {
        this.vaccinations = vaccinations;
    }
}