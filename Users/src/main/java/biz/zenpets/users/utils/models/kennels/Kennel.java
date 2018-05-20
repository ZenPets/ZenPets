package biz.zenpets.users.utils.models.kennels;

import com.google.gson.annotations.SerializedName;

public class Kennel {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("kennelOwnerID") private String kennelOwnerID;
    @SerializedName("kennelOwnerName") private String kennelOwnerName;
    @SerializedName("kennelOwnerDisplayProfile") private String kennelOwnerDisplayProfile;
    @SerializedName("kennelName") private String kennelName;
    @SerializedName("kennelAddress") private String kennelAddress;
    @SerializedName("kennelPinCode") private String kennelPinCode;
    @SerializedName("countryID") private String countryID;
    @SerializedName("countryName") private String countryName;
    @SerializedName("stateID") private String stateID;
    @SerializedName("stateName") private String stateName;
    @SerializedName("cityID") private String cityID;
    @SerializedName("cityName") private String cityName;
    @SerializedName("kennelLatitude") private String kennelLatitude;
    @SerializedName("kennelLongitude") private String kennelLongitude;
    @SerializedName("kennelPhonePrefix1") private String kennelPhonePrefix1;
    @SerializedName("kennelPhoneNumber1") private String kennelPhoneNumber1;
    @SerializedName("kennelPhonePrefix2") private String kennelPhonePrefix2;
    @SerializedName("kennelPhoneNumber2") private String kennelPhoneNumber2;
}