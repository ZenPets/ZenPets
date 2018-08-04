package biz.zenpets.kennels.utils.models.inventory;

import com.google.gson.annotations.SerializedName;

public class Inventory {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("kennelOwnerID") private String kennelOwnerID;
    @SerializedName("kennelChargesID") private String kennelChargesID;
    @SerializedName("paymentID") private String paymentID;
    @SerializedName("kennelOwnerName") private String kennelOwnerName;
}