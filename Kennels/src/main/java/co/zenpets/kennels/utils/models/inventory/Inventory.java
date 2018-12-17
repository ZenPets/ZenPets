package co.zenpets.kennels.utils.models.inventory;

import com.google.gson.annotations.SerializedName;

public class Inventory {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennelInventoryID") private String kennelInventoryID;
    @SerializedName("inventoryTypeID") private String inventoryTypeID;
    @SerializedName("inventoryTypeName") private String inventoryTypeName;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("kennelInventoryName") private String kennelInventoryName;
    @SerializedName("kennelInventoryCost") private String kennelInventoryCost;
    @SerializedName("kennelInventoryStatus") private String kennelInventoryStatus;

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

    public String getKennelInventoryID() {
        return kennelInventoryID;
    }

    public void setKennelInventoryID(String kennelInventoryID) {
        this.kennelInventoryID = kennelInventoryID;
    }

    public String getInventoryTypeID() {
        return inventoryTypeID;
    }

    public void setInventoryTypeID(String inventoryTypeID) {
        this.inventoryTypeID = inventoryTypeID;
    }

    public String getInventoryTypeName() {
        return inventoryTypeName;
    }

    public void setInventoryTypeName(String inventoryTypeName) {
        this.inventoryTypeName = inventoryTypeName;
    }

    public String getKennelID() {
        return kennelID;
    }

    public void setKennelID(String kennelID) {
        this.kennelID = kennelID;
    }

    public String getKennelInventoryName() {
        return kennelInventoryName;
    }

    public void setKennelInventoryName(String kennelInventoryName) {
        this.kennelInventoryName = kennelInventoryName;
    }

    public String getKennelInventoryCost() {
        return kennelInventoryCost;
    }

    public void setKennelInventoryCost(String kennelInventoryCost) {
        this.kennelInventoryCost = kennelInventoryCost;
    }

    public String getKennelInventoryStatus() {
        return kennelInventoryStatus;
    }

    public void setKennelInventoryStatus(String kennelInventoryStatus) {
        this.kennelInventoryStatus = kennelInventoryStatus;
    }
}