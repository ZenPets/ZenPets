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
}