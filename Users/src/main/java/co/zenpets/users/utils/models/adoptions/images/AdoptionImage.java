package co.zenpets.users.utils.models.adoptions.images;

import com.google.gson.annotations.SerializedName;

public class AdoptionImage {

    @SerializedName("imageID") private String imageID;
    @SerializedName("adoptionID") private String adoptionID;
    @SerializedName("imageURL") private String imageURL;

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getAdoptionID() {
        return adoptionID;
    }

    public void setAdoptionID(String adoptionID) {
        this.adoptionID = adoptionID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}