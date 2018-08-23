package biz.zenpets.users.utils.models.adoptions.adoption;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import biz.zenpets.users.utils.models.adoptions.promotion.Promotion;

public class Adoption {
    @SerializedName("error") private Boolean error;
    @SerializedName("adoptionID") private String adoptionID;
    @SerializedName("petTypeID") private String petTypeID;
    @SerializedName("petTypeName") private String petTypeName;
    @SerializedName("breedID") private String breedID;
    @SerializedName("breedName") private String breedName;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("adoptionName") private String adoptionName;
    @SerializedName("adoptionCoverPhoto") private String adoptionCoverPhoto;
    @SerializedName("adoptionDescription") private String adoptionDescription;
    @SerializedName("adoptionGender") private String adoptionGender;
//    @SerializedName("adoptionVaccinated") private String adoptionVaccinated;
//    @SerializedName("adoptionDewormed") private String adoptionDewormed;
//    @SerializedName("adoptionNeutered") private String adoptionNeutered;
    @SerializedName("adoptionTimeStamp") private String adoptionTimeStamp;
    @SerializedName("adoptionPrettyDate") private String adoptionPrettyDate;
    @SerializedName("adoptionStatus") private String adoptionStatus;
    @SerializedName("promotions") private ArrayList<Promotion> promotions = new ArrayList<>();

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getAdoptionID() {
        return adoptionID;
    }

    public void setAdoptionID(String adoptionID) {
        this.adoptionID = adoptionID;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDisplayProfile() {
        return userDisplayProfile;
    }

    public void setUserDisplayProfile(String userDisplayProfile) {
        this.userDisplayProfile = userDisplayProfile;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAdoptionName() {
        return adoptionName;
    }

    public void setAdoptionName(String adoptionName) {
        this.adoptionName = adoptionName;
    }

    public String getAdoptionCoverPhoto() {
        return adoptionCoverPhoto;
    }

    public void setAdoptionCoverPhoto(String adoptionCoverPhoto) {
        this.adoptionCoverPhoto = adoptionCoverPhoto;
    }

    public String getAdoptionDescription() {
        return adoptionDescription;
    }

    public void setAdoptionDescription(String adoptionDescription) {
        this.adoptionDescription = adoptionDescription;
    }

    public String getAdoptionGender() {
        return adoptionGender;
    }

    public void setAdoptionGender(String adoptionGender) {
        this.adoptionGender = adoptionGender;
    }

    public String getAdoptionTimeStamp() {
        return adoptionTimeStamp;
    }

    public void setAdoptionTimeStamp(String adoptionTimeStamp) {
        this.adoptionTimeStamp = adoptionTimeStamp;
    }

    public String getAdoptionPrettyDate() {
        return adoptionPrettyDate;
    }

    public void setAdoptionPrettyDate(String adoptionPrettyDate) {
        this.adoptionPrettyDate = adoptionPrettyDate;
    }

    public String getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(String adoptionStatus) {
        this.adoptionStatus = adoptionStatus;
    }

    public ArrayList<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(ArrayList<Promotion> promotions) {
        this.promotions = promotions;
    }
}