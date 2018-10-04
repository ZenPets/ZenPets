package biz.zenpets.users.utils.models.kennels.images;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class KennelImages {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("images") private ArrayList<KennelImage> images = null;

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

    public ArrayList<KennelImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<KennelImage> images) {
        this.images = images;
    }
}