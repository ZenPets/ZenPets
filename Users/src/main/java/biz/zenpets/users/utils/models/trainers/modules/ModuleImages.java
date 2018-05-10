package biz.zenpets.users.utils.models.trainers.modules;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ModuleImages {

    @SerializedName("error") private Boolean error;
    @SerializedName("images") private ArrayList<ModuleImage> images = null;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public ArrayList<ModuleImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<ModuleImage> images) {
        this.images = images;
    }
}