package biz.zenpets.users.utils.models.kennels.images;

import com.google.gson.annotations.SerializedName;

public class KennelImage {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("kennelImageID") private String kennelImageID;
    @SerializedName("kennelID") private String kennelID;
    @SerializedName("kennelImageURL") private String kennelImageURL;

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

    public String getKennelImageID() {
        return kennelImageID;
    }

    public void setKennelImageID(String kennelImageID) {
        this.kennelImageID = kennelImageID;
    }

    public String getKennelID() {
        return kennelID;
    }

    public void setKennelID(String kennelID) {
        this.kennelID = kennelID;
    }

    public String getKennelImageURL() {
        return kennelImageURL;
    }

    public void setKennelImageURL(String kennelImageURL) {
        this.kennelImageURL = kennelImageURL;
    }
}