package biz.zenpets.groomers.utils.models.images;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GroomerImages {
    @SerializedName("error") private Boolean error;
    @SerializedName("message") private String message;
    @SerializedName("images") private ArrayList<GroomerImage> images = null;

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

    public ArrayList<GroomerImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<GroomerImage> images) {
        this.images = images;
    }
}