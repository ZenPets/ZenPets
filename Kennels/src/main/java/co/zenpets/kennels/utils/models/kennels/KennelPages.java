package co.zenpets.kennels.utils.models.kennels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class KennelPages {
    @SerializedName("totalKennels") @Expose private String totalKennels;
    @SerializedName("totalPages") @Expose private String totalPages;

    public String getTotalKennels() {
        return totalKennels;
    }

    public void setTotalKennels(String totalKennels) {
        this.totalKennels = totalKennels;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }
}