package biz.zenpets.groomers.utils.models.images;

import com.google.gson.annotations.SerializedName;

public class GroomerImage {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("imageID") private String imageID;
    @SerializedName("groomerID") private String groomerID;
    @SerializedName("imageURL") private String imageURL;

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

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getGroomerID() {
        return groomerID;
    }

    public void setGroomerID(String groomerID) {
        this.groomerID = groomerID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}