package biz.zenpets.users.utils.models.pets.pets;

import com.google.gson.annotations.SerializedName;

public class Pet {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("petID") private String petID;
    @SerializedName("userID") private String userID;
    @SerializedName("petTypeID") private String petTypeID;
    @SerializedName("petTypeName") private String petTypeName;
    @SerializedName("breedID") private String breedID;
    @SerializedName("breedName") private String breedName;
    @SerializedName("petName") private String petName;
    @SerializedName("petGender") private String petGender;
    @SerializedName("petDOB") private String petDOB;
    @SerializedName("petNeutered") private String petNeutered;
    @SerializedName("petDisplayProfile") private String petDisplayProfile;
    @SerializedName("petActive") private String petActive;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

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

    public String getBreedID() {
        return breedID;
    }

    public void setBreedID(String breedID) {
        this.breedID = breedID;
    }

    public String getBreedName() {
        return breedName;
    }

    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetGender() {
        return petGender;
    }

    public void setPetGender(String petGender) {
        this.petGender = petGender;
    }

    public String getPetDOB() {
        return petDOB;
    }

    public void setPetDOB(String petDOB) {
        this.petDOB = petDOB;
    }

    public String getPetNeutered() {
        return petNeutered;
    }

    public void setPetNeutered(String petNeutered) {
        this.petNeutered = petNeutered;
    }

    public String getPetDisplayProfile() {
        return petDisplayProfile;
    }

    public void setPetDisplayProfile(String petDisplayProfile) {
        this.petDisplayProfile = petDisplayProfile;
    }

    public String getPetActive() {
        return petActive;
    }

    public void setPetActive(String petActive) {
        this.petActive = petActive;
    }
}