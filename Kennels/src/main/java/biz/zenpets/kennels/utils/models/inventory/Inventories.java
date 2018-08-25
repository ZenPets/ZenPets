package biz.zenpets.kennels.utils.models.inventory;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Inventories {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("inventories") private ArrayList<Inventory> inventories = null;

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

    public ArrayList<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(ArrayList<Inventory> inventories) {
        this.inventories = inventories;
    }
}