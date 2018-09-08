package biz.zenpets.users.utils.models.pets.types;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PetTypes {

    @SerializedName("error") private Boolean error;
    @SerializedName("types") private ArrayList<PetType> types = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<PetType> getTypes() {
        return types;
    }

    public void setTypes(ArrayList<PetType> types) {
        this.types = types;
    }
}