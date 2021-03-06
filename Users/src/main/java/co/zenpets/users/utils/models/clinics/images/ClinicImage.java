package co.zenpets.users.utils.models.clinics.images;

import com.google.gson.annotations.SerializedName;

public class ClinicImage {

    @SerializedName("imageID") private String imageID;
    @SerializedName("clinicID") private String clinicID;
    @SerializedName("imageURL") private String imageURL;

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }

    public String getClinicID() {
        return clinicID;
    }

    public void setClinicID(String clinicID) {
        this.clinicID = clinicID;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}