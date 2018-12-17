package co.zenpets.users.utils.models.kennels.bookings;

import com.google.gson.annotations.SerializedName;

public class KennelAvailability {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("totalAvailable") private String totalAvailable;
    @SerializedName("kennelInventoryID") private String kennelInventoryID;

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

    public String getTotalAvailable() {
        return totalAvailable;
    }

    public void setTotalAvailable(String totalAvailable) {
        this.totalAvailable = totalAvailable;
    }

    public String getKennelInventoryID() {
        return kennelInventoryID;
    }

    public void setKennelInventoryID(String kennelInventoryID) {
        this.kennelInventoryID = kennelInventoryID;
    }
}