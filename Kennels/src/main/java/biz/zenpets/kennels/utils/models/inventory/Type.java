package biz.zenpets.kennels.utils.models.inventory;

import com.google.gson.annotations.SerializedName;

public class Type {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("inventoryTypeID") private String inventoryTypeID;
    @SerializedName("inventoryTypeName") private String inventoryTypeName;

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
}