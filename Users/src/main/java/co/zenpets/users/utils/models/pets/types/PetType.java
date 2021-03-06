package co.zenpets.users.utils.models.pets.types;

import com.google.gson.annotations.SerializedName;

public class PetType {

    @SerializedName("petTypeID") private String petTypeID;
    @SerializedName("petTypeName") private String petTypeName;

    public String getPetTypeID() {
        return petTypeID;
    }

    public void setPetTypeID(String petTypeID) {
        this.petTypeID = petTypeID;
    }

    public String getPetTypeName() {
        return petTypeName;
    }

    public void setPetTypeName(String petTypeName) {
        this.petTypeName = petTypeName;
    }
}