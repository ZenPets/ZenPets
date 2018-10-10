package co.zenpets.users.utils.models.adoptions.promotion;

import com.google.gson.annotations.SerializedName;

public class Promotion {

    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("promotedID") private String promotedID;
    @SerializedName("adoptionID") private String adoptionID;
    @SerializedName("optionID") private String optionID;
    @SerializedName("paymentID") private String paymentID;
    @SerializedName("promotedFrom") private String promotedFrom;
    @SerializedName("promotedTo") private String promotedTo;
    @SerializedName("promotedTimestamp") private String promotedTimestamp;
    @SerializedName("petTypeID") private String petTypeID;
    @SerializedName("petTypeName") private String petTypeName;
    @SerializedName("breedID") private String breedID;
    @SerializedName("breedName") private String breedName;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("adoptionName") private String adoptionName;
    @SerializedName("adoptionCoverPhoto") private String adoptionCoverPhoto;
    @SerializedName("adoptionDescription") private String adoptionDescription;
    @SerializedName("adoptionGender") private String adoptionGender;
    @SerializedName("adoptionTimeStamp") private String adoptionTimeStamp;
    @SerializedName("adoptionStatus") private String adoptionStatus;

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

    public String getPromotedID() {
        return promotedID;
    }

    public void setPromotedID(String promotedID) {
        this.promotedID = promotedID;
    }

    public String getAdoptionID() {
        return adoptionID;
    }

    public void setAdoptionID(String adoptionID) {
        this.adoptionID = adoptionID;
    }

    public String getOptionID() {
        return optionID;
    }

    public void setOptionID(String optionID) {
        this.optionID = optionID;
    }

    public String getPaymentID() {
        return paymentID;
    }

    public void setPaymentID(String paymentID) {
        this.paymentID = paymentID;
    }

    public String getPromotedFrom() {
        return promotedFrom;
    }

    public void setPromotedFrom(String promotedFrom) {
        this.promotedFrom = promotedFrom;
    }

    public String getPromotedTo() {
        return promotedTo;
    }

    public void setPromotedTo(String promotedTo) {
        this.promotedTo = promotedTo;
    }

    public String getPromotedTimestamp() {
        return promotedTimestamp;
    }

    public void setPromotedTimestamp(String promotedTimestamp) {
        this.promotedTimestamp = promotedTimestamp;
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

    public String getAdoptionStatus() {
        return adoptionStatus;
    }

    public void setAdoptionStatus(String adoptionStatus) {
        this.adoptionStatus = adoptionStatus;
    }
}