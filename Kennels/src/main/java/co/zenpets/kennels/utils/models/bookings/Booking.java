package co.zenpets.kennels.utils.models.bookings;

import com.google.gson.annotations.SerializedName;

public class Booking {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("bookingID") private String bookingID;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("kennelName") private String kennelName;
    @SerializedName("kennelCoverPhoto") private String kennelCoverPhoto;
    @SerializedName("kennelInventoryID") private String kennelInventoryID;
    @SerializedName("kennelInventoryName") private String kennelInventoryName;
    @SerializedName("userID") private String userID;
    @SerializedName("userName") private String userName;
    @SerializedName("userDisplayProfile") private String userDisplayProfile;
    @SerializedName("petID") private String petID;
    @SerializedName("petName") private String petName;
    @SerializedName("petDisplayProfile") private String petDisplayProfile;
    @SerializedName("petTypeID") private String petTypeID;
    @SerializedName("petTypeName") private String petTypeName;
    @SerializedName("breedID") private String breedID;
    @SerializedName("breedName") private String breedName;
    @SerializedName("bookingFrom") private String bookingFrom;
    @SerializedName("bookingTo") private String bookingTo;

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

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getKennelID() {
        return kennelID;
    }

    public void setKennelID(String kennelID) {
        this.kennelID = kennelID;
    }

    public String getKennelName() {
        return kennelName;
    }

    public void setKennelName(String kennelName) {
        this.kennelName = kennelName;
    }

    public String getKennelCoverPhoto() {
        return kennelCoverPhoto;
    }

    public void setKennelCoverPhoto(String kennelCoverPhoto) {
        this.kennelCoverPhoto = kennelCoverPhoto;
    }

    public String getKennelInventoryID() {
        return kennelInventoryID;
    }

    public void setKennelInventoryID(String kennelInventoryID) {
        this.kennelInventoryID = kennelInventoryID;
    }

    public String getKennelInventoryName() {
        return kennelInventoryName;
    }

    public void setKennelInventoryName(String kennelInventoryName) {
        this.kennelInventoryName = kennelInventoryName;
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

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }

    public String getPetDisplayProfile() {
        return petDisplayProfile;
    }

    public void setPetDisplayProfile(String petDisplayProfile) {
        this.petDisplayProfile = petDisplayProfile;
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

    public String getBookingFrom() {
        return bookingFrom;
    }

    public void setBookingFrom(String bookingFrom) {
        this.bookingFrom = bookingFrom;
    }

    public String getBookingTo() {
        return bookingTo;
    }

    public void setBookingTo(String bookingTo) {
        this.bookingTo = bookingTo;
    }
}