package co.zenpets.users.utils.models.pets.breeds;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Breeds {

    @SerializedName("error") private Boolean error;
    @SerializedName("breeds") private ArrayList<Breed> breeds = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Breed> getBreeds() {
        return breeds;
    }

    public void setBreeds(ArrayList<Breed> breeds) {
        this.breeds = breeds;
    }
}