package co.zenpets.users.utils.models.pets.pets;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Pets {

    @SerializedName("error") private Boolean error;
    @SerializedName("pets") private ArrayList<Pet> pets = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<Pet> getPets() {
        return pets;
    }

    public void setPets(ArrayList<Pet> pets) {
        this.pets = pets;
    }
}