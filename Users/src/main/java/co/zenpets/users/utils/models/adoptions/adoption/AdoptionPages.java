package co.zenpets.users.utils.models.adoptions.adoption;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdoptionPages {

    @SerializedName("totalAdoptions") @Expose private String totalAdoptions;
    @SerializedName("totalPages") @Expose private String totalPages;

    public String getTotalAdoptions() {
        return totalAdoptions;
    }

    public void setTotalAdoptions(String totalAdoptions) {
        this.totalAdoptions = totalAdoptions;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }
}