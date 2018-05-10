package biz.zenpets.users.utils.models.pets.breeds;

import com.google.gson.annotations.SerializedName;

public class Breed {

    @SerializedName("breedID") private String breedID;
    @SerializedName("petTypeID") private String petTypeID;
    @SerializedName("breedName") private String breedName;

    public String getBreedID() {
        return breedID;
    }

    public void setBreedID(String breedID) {
        this.breedID = breedID;
    }

    public String getPetTypeID() {
        return petTypeID;
    }

    public void setPetTypeID(String petTypeID) {
        this.petTypeID = petTypeID;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }
}